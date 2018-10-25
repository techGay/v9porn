package com.u9porn.data.db;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.u9porn.BuildConfig;
import com.u9porn.data.db.entity.Category;
import com.u9porn.data.db.entity.CategoryDao;
import com.u9porn.data.db.entity.DaoMaster;
import com.u9porn.data.db.entity.DaoSession;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.data.db.entity.V9PornItemDao;
import com.u9porn.data.db.entity.VideoResult;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author flymegoc
 * @date 2018/3/4
 */

@Singleton
public class AppDbHelper implements DbHelper {
    private final DaoSession mDaoSession;

    @Inject
    AppDbHelper(MySQLiteOpenHelper helper) {
        //如果你想查看日志信息，请将DEBUG设置为true
        MigrationHelper.DEBUG = BuildConfig.DEBUG;
        Database db = helper.getWritableDb();
        this.mDaoSession = new DaoMaster(db).newSession();
        initCategory(Category.TYPE_91PORN, Category.CATEGORY_DEFAULT_91PORN_VALUE, Category.CATEGORY_DEFAULT_91PORN_NAME);
        initCategory(Category.TYPE_91PORN_FORUM, Category.CATEGORY_DEFAULT_91PORN_FORUM_VALUE, Category.CATEGORY_DEFAULT_91PORN_FORUM_NAME);
        initCategory(Category.TYPE_MEI_ZI_TU, Category.CATEGORY_DEFAULT_MEI_ZI_TU_VALUE, Category.CATEGORY_DEFAULT_MEI_ZI_TU_NAME);
        initCategory(Category.TYPE_PIG_AV, Category.CATEGORY_DEFAULT_PIG_AV_VALUE, Category.CATEGORY_DEFAULT_PIG_AV_NAME);
        initCategory(Category.TYPE_99_MM, Category.CATEGORY_DEFAULT_99_MM_VALUE, Category.CATEGORY_DEFAULT_99_MM_NAME);
        initCategory(Category.TYPE_HUA_BAN, null, Category.CATEGORY_DEFAULT_HUA_BAN_NAME);
        initCategory(Category.TYPE_AXGLE, Category.CATEGORY_DEFAULT_AXGLE_VALUE, Category.CATEGORY_DEFAULT_AXGLE_NAME);
    }

    @Override
    public void initCategory(int type, String[] value, String[] name) {
        int length = name.length;
        List<Category> categoryList = mDaoSession.getCategoryDao().queryBuilder().where(CategoryDao.Properties.CategoryType.eq(type)).build().list();
        if (categoryList.size() == length) {
            return;
        }
        for (int i = 0; i < length; i++) {
            Category category = new Category();
            category.setCategoryName(name[i]);
            if (value == null) {
                category.setCategoryValue(String.valueOf(i + 1));
            } else {
                category.setCategoryValue(value[i]);
            }

            category.setCategoryType(type);
            category.setIsShow(true);
            category.setSortId(i);
            categoryList.add(category);
        }
        mDaoSession.getCategoryDao().insertOrReplaceInTx(categoryList);
    }

    @Override
    public void updateV9PornItem(V9PornItem v9PornItem) {
        mDaoSession.getV9PornItemDao().update(v9PornItem);
    }

    @Override
    public List<V9PornItem> loadDownloadingData() {
        return mDaoSession.getV9PornItemDao().queryBuilder().where(V9PornItemDao.Properties.Status.notEq(FileDownloadStatus.completed), V9PornItemDao.Properties.DownloadId.notEq(0)).orderDesc(V9PornItemDao.Properties.AddDownloadDate).build().list();

    }

    @Override
    public List<V9PornItem> loadFinishedData() {
        return mDaoSession.getV9PornItemDao().queryBuilder().where(V9PornItemDao.Properties.Status.eq(FileDownloadStatus.completed), V9PornItemDao.Properties.DownloadId.notEq(0)).orderDesc(V9PornItemDao.Properties.FinishedDownloadDate).build().list();
    }

    @Override
    public List<V9PornItem> loadHistoryData(int page, int pageSize) {
        return mDaoSession.getV9PornItemDao().queryBuilder().where(V9PornItemDao.Properties.ViewHistoryDate.isNotNull()).orderDesc(V9PornItemDao.Properties.ViewHistoryDate).offset((page - 1) * pageSize).limit(pageSize).build().list();
    }

    @Override
    public long saveV9PornItem(V9PornItem v9PornItem) {
        return mDaoSession.getV9PornItemDao().insertOrReplace(v9PornItem);
    }

    @Override
    public long saveVideoResult(VideoResult videoResult) {
        return mDaoSession.getVideoResultDao().insertOrReplace(videoResult);
    }

    @Override
    public V9PornItem findV9PornItemByViewKey(String viewKey) {
        V9PornItemDao v9PornItemDao = mDaoSession.getV9PornItemDao();
        try {
            return v9PornItemDao.queryBuilder().where(V9PornItemDao.Properties.ViewKey.eq(viewKey)).build().unique();
        } catch (Exception e) {
            //暂时先都删除了，之前没有设置唯一约束
            List<V9PornItem> tmp = v9PornItemDao.queryBuilder().where(V9PornItemDao.Properties.ViewKey.eq(viewKey)).build().list();
            for (V9PornItem v9PornItem : tmp) {
                v9PornItemDao.delete(v9PornItem);
            }
            if (!BuildConfig.DEBUG) {
                //Bugsnag.notify(new Throwable("findV9PornItemDaoByViewKey DaoException", e), Severity.WARNING);
            }
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public V9PornItem findV9PornItemByDownloadId(int downloadId) {
        try {
            return mDaoSession.getV9PornItemDao().queryBuilder().where(V9PornItemDao.Properties.DownloadId.eq(downloadId)).build().unique();
        } catch (Exception e) {
            //暂时先不处理这问题了，理论上一个不会发生，因为时根据url生成
            if (!BuildConfig.DEBUG) {
                //Bugsnag.notify(new Throwable("findV9PornItemDaoByDownloadId DaoException", e), Severity.WARNING);
            }
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<V9PornItem> loadV9PornItems() {
        return mDaoSession.getV9PornItemDao().loadAll();
    }

    @Override
    public List<V9PornItem> findV9PornItemByDownloadStatus(int status) {
        return mDaoSession.getV9PornItemDao().queryBuilder().where(V9PornItemDao.Properties.Status.eq(status)).build().list();
    }

    @Override
    public List<Category> loadAllCategoryDataByType(int type) {
        CategoryDao categoryDao = mDaoSession.getCategoryDao();
        categoryDao.detachAll();
        return categoryDao.queryBuilder().where(CategoryDao.Properties.CategoryType.eq(type)).orderAsc(CategoryDao.Properties.SortId).build().list();
    }

    @Override
    public List<Category> loadCategoryDataByType(int type) {
        CategoryDao categoryDao = mDaoSession.getCategoryDao();
        categoryDao.detachAll();
        return categoryDao.queryBuilder().where(CategoryDao.Properties.CategoryType.eq(type), CategoryDao.Properties.IsShow.eq(true)).orderAsc(CategoryDao.Properties.SortId).build().list();
    }

    @Override
    public void updateCategoryData(List<Category> categoryList) {
        mDaoSession.getCategoryDao().updateInTx(categoryList);
    }

    @Override
    public Category findCategoryById(Long id) {
        CategoryDao categoryDao = mDaoSession.getCategoryDao();
        categoryDao.detachAll();
        return categoryDao.load(id);
    }
}
