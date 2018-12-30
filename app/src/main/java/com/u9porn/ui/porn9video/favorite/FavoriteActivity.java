package com.u9porn.ui.porn9video.favorite;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aitsuki.swipe.SwipeItemLayout;
import com.aitsuki.swipe.SwipeMenuRecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.helper.loadviewhelper.help.OnLoadViewListener;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.adapter.FavoriteAdapter;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.ui.MvpActivity;
import com.u9porn.utils.DialogUtils;
import com.u9porn.utils.LoadHelperUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public class FavoriteActivity extends MvpActivity<FavoriteView, FavoritePresenter> implements FavoriteView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    SwipeMenuRecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.contentView)
    SwipeRefreshLayout contentView;

    private FavoriteAdapter mUnLimit91Adapter;

    private LoadViewHelper helper;
    private AlertDialog deleteAlertDialog;

    @Inject
    protected FavoritePresenter favoritePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        ButterKnife.bind(this);
        deleteAlertDialog = DialogUtils.initLoadingDialog(this, "删除中，请稍后...");
        initToolBar(toolbar);
        toolbar.setContentInsetStartWithNavigation(0);

        // Setup contentView == SwipeRefreshView
        contentView.setOnRefreshListener(this);

        List<V9PornItem> mV9PornItemList = new ArrayList<>();
        mUnLimit91Adapter = new FavoriteAdapter(R.layout.item_right_menu_delete, mV9PornItemList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(mUnLimit91Adapter);
        mUnLimit91Adapter.setEmptyView(R.layout.empty_view, recyclerView);

        mUnLimit91Adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                goToPlayVideo((V9PornItem) adapter.getItem(position),presenter.getPlayBackEngine());
            }
        });

        mUnLimit91Adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                SwipeItemLayout swipeItemLayout = (SwipeItemLayout) view.getParent();
                swipeItemLayout.close();
                if (view.getId() == R.id.right_menu_delete) {
                    V9PornItem v9PornItem = (V9PornItem) adapter.getItem(position);
                    if (v9PornItem == null || v9PornItem.getVideoResult() == null) {
                        showMessage("信息错误，无法删除", TastyToast.WARNING);
                        return;
                    }
                    presenter.deleteFavorite(v9PornItem.getVideoResult().getVideoId());
                }
            }
        });

        mUnLimit91Adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.loadRemoteFavoriteData(false);
            }
        }, recyclerView);

        helper = new LoadViewHelper(recyclerView);
        helper.setListener(new OnLoadViewListener() {
            @Override
            public void onRetryClick() {
                presenter.loadRemoteFavoriteData(false);
            }
        });
        boolean needRefresh = presenter.isFavoriteNeedRefresh();
        presenter.loadRemoteFavoriteData(needRefresh);
    }

    @NonNull
    @Override
    public FavoritePresenter createPresenter() {
        return favoritePresenter;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.favorite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_favorite_export) {
            showMessage("暂不支持导出", TastyToast.WARNING);
            //showExportDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showExportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("导出选择");
        builder.setMessage("导出视频链接和标题还是仅导出视频链接？");
        builder.setNegativeButton("包括标题", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.exportData(false);
            }
        });
        builder.setPositiveButton("仅链接", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.exportData(true);
            }
        });
        builder.show();

    }

    @Override
    public void setFavoriteData(List<V9PornItem> v9PornItemList) {
        presenter.setFavoriteNeedRefresh(false);
        mUnLimit91Adapter.setNewData(v9PornItemList);
    }

    @Override
    public void loadMoreDataComplete() {
        mUnLimit91Adapter.loadMoreComplete();
    }

    @Override
    public void loadMoreFailed() {
        showMessage("加载更多失败", TastyToast.ERROR);
        mUnLimit91Adapter.loadMoreFail();
    }

    @Override
    public void noMoreData() {
        mUnLimit91Adapter.loadMoreEnd(true);
        showMessage("没有更多数据了", TastyToast.INFO);
    }

    @Override
    public void setMoreData(List<V9PornItem> v9PornItemList) {
        mUnLimit91Adapter.addData(v9PornItemList);
    }

    @Override
    public void deleteFavoriteSucc(String message) {
        //标志删除失败，下次加载服务器数据，清空缓存
        presenter.setFavoriteNeedRefresh(true);
        dismissDialog();
        showMessage(message, TastyToast.SUCCESS);
    }

    @Override
    public void deleteFavoriteError(String message) {
        dismissDialog();
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void showDeleteDialog() {
        deleteAlertDialog.show();
    }

    private void dismissDialog() {
        if (deleteAlertDialog != null && deleteAlertDialog.isShowing() && !isFinishing()) {
            deleteAlertDialog.dismiss();
        }
    }

    @Override
    public void showError(String message) {
        contentView.setRefreshing(false);
        helper.showError();
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        helper.showLoading();
        LoadHelperUtils.setLoadingText(helper.getLoadIng(), R.id.tv_loading_text, "拼命加载中...");
        contentView.setEnabled(false);
    }

    @Override
    public void showContent() {
        helper.showContent();
        contentView.setEnabled(true);
        contentView.setRefreshing(false);
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    public void onRefresh() {
        presenter.loadRemoteFavoriteData(true);
    }
}
