package com.u9porn.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.devbrackets.android.exomedia.util.ResourceUtil;
import com.liulishuo.filedownloader.FileDownloader;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.BuildConfig;
import com.u9porn.R;
import com.u9porn.constants.Constants;
import com.u9porn.constants.Keys;
import com.u9porn.constants.KeysActivityRequestResultCode;
import com.u9porn.constants.PermissionConstants;
import com.u9porn.data.model.Notice;
import com.u9porn.data.model.UpdateVersion;
import com.u9porn.data.network.Api;
import com.u9porn.eventbus.LowMemoryEvent;
import com.u9porn.eventbus.UrlRedirectEvent;
import com.u9porn.service.UpdateDownloadService;
import com.u9porn.ui.MvpActivity;
import com.u9porn.ui.axgle.MainAxgleFragment;
import com.u9porn.ui.axgle.search.SearchAxgleVideoActivity;
import com.u9porn.ui.basemain.BaseMainFragment;
import com.u9porn.ui.download.DownloadActivity;
import com.u9porn.ui.images.Main99MmFragment;
import com.u9porn.ui.images.MainHuaBanFragment;
import com.u9porn.ui.images.MainMeiZiTuFragment;
import com.u9porn.ui.mine.MineFragment;
import com.u9porn.ui.music.MusicFragment;
import com.u9porn.ui.porn9forum.Main9ForumFragment;
import com.u9porn.ui.porn9video.Main9PronVideoFragment;
import com.u9porn.ui.porn9video.search.SearchActivity;
import com.u9porn.ui.porn9video.user.UserLoginActivity;
import com.u9porn.ui.pxgav.MainPxgavFragment;
import com.u9porn.ui.setting.SettingActivity;
import com.u9porn.utils.FragmentUtils;
import com.u9porn.utils.NotificationChannelHelper;
import com.u9porn.utils.SDCardUtils;
import com.u9porn.utils.Tags;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public class MainActivity extends MvpActivity<MainView, MainPresenter> implements MainView {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationBar bottomNavigationBar;
    @BindView(R.id.fab_search)
    FloatingActionButton fabSearch;
    @BindView(R.id.content)
    FrameLayout contentFrameLayout;

    private Fragment mCurrentFragment;
    private int permisionCode = 300;
    private int permisionReqCode = 400;
    private String[] permission = PermissionConstants.getPermissions(PermissionConstants.STORAGE);
    private Main9PronVideoFragment mMain9PronVideoFragment;
    private MainMeiZiTuFragment mMaiMeiZiTuFragment;
    private Main9ForumFragment mMain9ForumFragment;
    private Main99MmFragment mMain99MmFragment;
    private MainHuaBanFragment mMainHuaBanFragment;
    private MainPxgavFragment mMainPxgavFragment;
    private MusicFragment mMusicFragment;
    private MineFragment mMineFragment;
    private MainAxgleFragment mainAxgleFragment;
    private FragmentManager fragmentManager;
    private int selectIndex;
    private String firstTabShow;
    private String secondTabShow;
    private boolean isBackground = false;

    private List<String> firstTagsArray = new ArrayList<>();
    private List<String> secondTagsArray = new ArrayList<>();

    @Inject
    MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotificationChannelHelper.initChannel(this);
        ButterKnife.bind(this);
        firstTagsArray.add(Tags.TAG_SEARCH_PORN_AXGLE_VIDEO);
        firstTagsArray.add(Tags.TAG_MY_DOWNLOAD);
        firstTagsArray.add(Tags.TAG_PRON_9_VIDEO);
        firstTagsArray.add(Tags.TAG_PXGAV_VIDEO);
        firstTagsArray.add(Tags.TAG_AXGLE_VIDEO);

        secondTagsArray.add(Tags.TAG_MEI_ZI_TU);
        secondTagsArray.add(Tags.TAG_MM_99);
        secondTagsArray.add(Tags.TAG_HUA_BAN);

        fragmentManager = getSupportFragmentManager();
        selectIndex = getIntent().getIntExtra(Keys.KEY_SELECT_INDEX, 0);
        if (savedInstanceState != null) {
            selectIndex = savedInstanceState.getInt(Keys.KEY_SELECT_INDEX);
        }
        handlerContentMargin();
        initBottomNavigationBar(selectIndex);

        makeDirAndCheckPermission();

        fabSearch.setOnClickListener(v -> doOnFloatingActionButtonClick(selectIndex));
        firstTabShow = presenter.getMainFirstTabShow();
        secondTabShow = presenter.getMainSecondTabShow();
        doOnTabSelected(selectIndex);
        checkNeedToShowUpdateOrNoticeDialog();
    }

    private void checkNeedToShowUpdateOrNoticeDialog() {
        UpdateVersion updateVersion = (UpdateVersion) getIntent().getSerializableExtra(Keys.KEY_INTENT_UPDATE);
        if (updateVersion != null) {
            showUpdateDialog(updateVersion);
            return;
        }
        Notice notice = (Notice) getIntent().getSerializableExtra(Keys.KEY_INTENT_NOTICE);
        if (notice != null) {
            showNewNoticeDialog(notice);
        }
    }

    private void doOnFloatingActionButtonClick(@IntRange(from = 0, to = 4) int position) {
        switch (position) {
            case 0:
                showVideoBottomSheet(firstTagsArray.indexOf(firstTabShow));
                break;
            case 1:
                showPictureBottomSheet(secondTagsArray.indexOf(secondTabShow));
                break;
            case 2:
                showForumBottomSheet(0);
                break;
            case 3:

                break;
            case 4:
                break;
            default:
        }
    }

    private void showVideoBottomSheet(final int checkIndex) {
        new QMUIBottomSheet.BottomListSheetBuilder(this, true)
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_search_black_24dp), Tags.TAG_SEARCH_PORN_AXGLE_VIDEO)
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_my_download), Tags.TAG_MY_DOWNLOAD)
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_video_library_black_24dp), Tags.TAG_PRON_9_VIDEO)
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_video_library_black_24dp), Tags.TAG_PXGAV_VIDEO)
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_video_library_black_24dp), Tags.TAG_AXGLE_VIDEO)
                .setCheckedIndex(checkIndex)
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                    dialog.dismiss();
                    switch (tag) {
                        case Tags.TAG_SEARCH_PORN_AXGLE_VIDEO:
                            goToSearchVideo();
                            break;
                        case Tags.TAG_MY_DOWNLOAD:
                            Intent intent = new Intent(context, DownloadActivity.class);
                            startActivityWithAnimation(intent);
                            break;
                        default:
                            handlerFirstTabClickToShow(tag, selectIndex, true);
                    }
                })
                .build()
                .show();
    }

    private void showPictureBottomSheet(int checkIndex) {
        new QMUIBottomSheet.BottomListSheetBuilder(this, true)
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_photo_library_black_24dp), Tags.TAG_MEI_ZI_TU)
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_photo_library_black_24dp), Tags.TAG_MM_99)
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_photo_library_black_24dp), Tags.TAG_HUA_BAN)
                .setCheckedIndex(checkIndex)
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                    dialog.dismiss();
                    handlerSecondTabClickToShow(tag, selectIndex, true);
                })
                .build()
                .show();
    }

    private void showForumBottomSheet(int selectIndex) {
        new QMUIBottomSheet.BottomListSheetBuilder(this, true)
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_library_books_black_24dp), "9*PORN论坛")
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_library_books_black_24dp), "CaoLiu社区")
                .setCheckedIndex(selectIndex)
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                    dialog.dismiss();
                    switch (position) {
                        case 0:

                            break;
                        case 1:
                            showMessage("还未支持，敬请期待", TastyToast.INFO);
                            break;
                        default:
                    }
                })
                .build()
                .show();
    }

    private void initBottomNavigationBar(@IntRange(from = 0, to = 4) int position) {
        bottomNavigationBar.addItem(new BottomNavigationItem(ResourceUtil.getDrawable(this, R.drawable.ic_video_library_black_24dp), R.string.title_video));
        bottomNavigationBar.addItem(new BottomNavigationItem(ResourceUtil.getDrawable(this, R.drawable.ic_photo_library_black_24dp), R.string.title_photo));
        bottomNavigationBar.addItem(new BottomNavigationItem(ResourceUtil.getDrawable(this, R.drawable.ic_library_books_black_24dp), R.string.title_forum));
        bottomNavigationBar.addItem(new BottomNavigationItem(ResourceUtil.getDrawable(this, R.drawable.ic_library_music_black_24dp), R.string.title_music));
        bottomNavigationBar.addItem(new BottomNavigationItem(ResourceUtil.getDrawable(this, R.drawable.ic_menu_black_24dp), R.string.title_me));

        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setActiveColor(R.color.bottom_navigation_bar_active);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);

        bottomNavigationBar.setFirstSelectedPosition(position);
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.SimpleOnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                doOnTabSelected(position);
            }
        });
        bottomNavigationBar.setBarBackgroundColor(R.color.bottom_navigation_bar_background);
        bottomNavigationBar.setFab(fabSearch);
        bottomNavigationBar.setAutoHideEnabled(!presenter.isFixMainNavigation());
        bottomNavigationBar.initialise();
    }

    private void handlerContentMargin() {
        if (contentFrameLayout == null || bottomNavigationBar == null || !presenter.isFixMainNavigation()) {
            return;
        }
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) contentFrameLayout.getLayoutParams();
        layoutParams.bottomMargin = QMUIDisplayHelper.getActionBarHeight(this);
        contentFrameLayout.setLayoutParams(layoutParams);
    }

    private void doOnTabSelected(@IntRange(from = 0, to = 4) int position) {
        switch (position) {
            case 0:
                handlerFirstTabClickToShow(firstTabShow, position, false);
                showFloatingActionButton(fabSearch);
                break;
            case 1:
                handlerSecondTabClickToShow(secondTabShow, position, false);
                showFloatingActionButton(fabSearch);
                break;
            case 2:
                if (presenter.haveNotSetF9pornAddress()) {
                    showNeedSetAddressDialog();
                    return;
                }
                if (mMain9ForumFragment == null) {
                    mMain9ForumFragment = Main9ForumFragment.getInstance();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mMain9ForumFragment, contentFrameLayout.getId(), position, false);
                showFloatingActionButton(fabSearch);
                break;
            case 3:
                if (mMusicFragment == null) {
                    mMusicFragment = MusicFragment.getInstance();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mMusicFragment, contentFrameLayout.getId(), position, false);
                hideFloatingActionButton(fabSearch);
                break;
            case 4:
                if (mMineFragment == null) {
                    mMineFragment = MineFragment.getInstance();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mMineFragment, contentFrameLayout.getId(), position, false);
                hideFloatingActionButton(fabSearch);
                break;
            default:
        }
        selectIndex = position;
    }

    private void handlerFirstTabClickToShow(String tag, int itemId, boolean isInnerReplace) {
        switch (tag) {
            case Tags.TAG_PRON_9_VIDEO:
                if (presenter.haveNotSetV9pronAddress()) {
                    showNeedSetAddressDialog();
                    return;
                }
                if (mMain9PronVideoFragment == null) {
                    mMain9PronVideoFragment = Main9PronVideoFragment.getInstance();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mMain9PronVideoFragment, contentFrameLayout.getId(), itemId, isInnerReplace);
                firstTabShow = Tags.TAG_PRON_9_VIDEO;
                presenter.setMainFirstTabShow(Tags.TAG_PRON_9_VIDEO);
                mMainPxgavFragment = null;
                break;
            case Tags.TAG_PXGAV_VIDEO:
                if (presenter.haveNotSetPavAddress()) {
                    showNeedSetAddressDialog();
                    return;
                }
                if (mMainPxgavFragment == null) {
                    mMainPxgavFragment = MainPxgavFragment.getInstance();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mMainPxgavFragment, contentFrameLayout.getId(), itemId, isInnerReplace);
                firstTabShow = Tags.TAG_PXGAV_VIDEO;
                presenter.setMainFirstTabShow(Tags.TAG_PXGAV_VIDEO);
                mMain9PronVideoFragment = null;
                break;
            case Tags.TAG_AXGLE_VIDEO:
                if (presenter.haveNotSetAxgleAddress()) {
                    return;
                }
                if (mainAxgleFragment == null) {
                    mainAxgleFragment = MainAxgleFragment.getInstance();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mainAxgleFragment, contentFrameLayout.getId(), itemId, isInnerReplace);
                firstTabShow = Tags.TAG_AXGLE_VIDEO;
                presenter.setMainFirstTabShow(Tags.TAG_AXGLE_VIDEO);
                break;
            default:
        }
    }

    private void showNeedSetAddressDialog() {
        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(context);
        builder.setTitle("温馨提示");
        builder.setMessage("还未设置对应地址，现在去设置？");
        builder.addAction("去设置", (dialog, index) -> {
            dialog.dismiss();
            Intent intent = new Intent(context, SettingActivity.class);
            startActivityWithAnimation(intent);
        });
        builder.addAction("返回", (dialog, index) -> dialog.dismiss());
        builder.show();
    }

    private void handlerSecondTabClickToShow(String tag, int itemId, boolean isInnerReplace) {
        switch (tag) {
            case Tags.TAG_MEI_ZI_TU:
                if (mMaiMeiZiTuFragment == null) {
                    mMaiMeiZiTuFragment = MainMeiZiTuFragment.getInstance();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mMaiMeiZiTuFragment, contentFrameLayout.getId(), itemId, isInnerReplace);
                secondTabShow = Tags.TAG_MEI_ZI_TU;
                presenter.setMainSecondTabShow(Tags.TAG_MEI_ZI_TU);
                mMain99MmFragment = null;
                mMainHuaBanFragment = null;
                break;
            case Tags.TAG_MM_99:
//                if (mMain99MmFragment == null) {
//                    mMain99MmFragment = Main99MmFragment.getInstance();
//                }
//                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mMain99MmFragment, contentFrameLayout.getId(), itemId, isInnerReplace);
//                secondTabShow = Tags.TAG_MM_99;
//                presenter.setMainSecondTabShow(Tags.TAG_MM_99);
//                mMaiMeiZiTuFragment = null;
//                mMainHuaBanFragment = null;
                showMessage("停止支持，网址已无法访问", TastyToast.INFO);
                break;
            case Tags.TAG_HUA_BAN:
//                if (mMainHuaBanFragment == null) {
//                    mMainHuaBanFragment = MainHuaBanFragment.getInstance();
//                }
//                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mMainHuaBanFragment, contentFrameLayout.getId(), itemId, isInnerReplace);
//                secondTabShow = HUA_BAN;
//                presenter.setMainSecondTabShow(HUA_BAN);
//                mMain99MmFragment = null;
//                mMaiMeiZiTuFragment = null;
                showMessage("暂停支持", TastyToast.INFO);
                break;
            default:
        }
    }

    private void hideFloatingActionButton(FloatingActionButton fabSearch) {
        ViewGroup.LayoutParams layoutParams = fabSearch.getLayoutParams();
        if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams coLayoutParams = (CoordinatorLayout.LayoutParams) layoutParams;
            FloatingActionButton.Behavior behavior = new FloatingActionButton.Behavior();
            coLayoutParams.setBehavior(behavior);
        }
        fabSearch.hide();
    }

    private void showFloatingActionButton(final FloatingActionButton fabSearch) {
        fabSearch.show(new FloatingActionButton.OnVisibilityChangedListener() {
            @Override
            public void onShown(FloatingActionButton fab) {
                fabSearch.requestLayout();
                bottomNavigationBar.setFab(fab);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Keys.KEY_SELECT_INDEX, selectIndex);
    }

    /**
     * 申请权限并创建下载目录
     */
    private void makeDirAndCheckPermission() {
        if (!AndPermission.hasPermission(MainActivity.this, permission)) {
            AndPermission.with(this)
                    .requestCode(permisionCode)
                    .permission(permission)
                    .rationale((requestCode, rationale) -> {
                        // 此对话框可以自定义，调用rationale.resume()就可以继续申请。
                        AndPermission.rationaleDialog(MainActivity.this, rationale).show();
                    })
                    .callback(listener)
                    .start();
        }
    }

    private PermissionListener listener = new PermissionListener() {
        File file = new File(SDCardUtils.DOWNLOAD_VIDEO_PATH);

        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantedPermissions) {
            // 权限申请成功回调。

            // 这里的requestCode就是申请时设置的requestCode。
            // 和onActivityResult()的requestCode一样，用来区分多个不同的请求。
            if (requestCode == permisionCode) {
                // TODO ...
                if (AndPermission.hasPermission(MainActivity.this, grantedPermissions)) {
                    if (!file.exists()) {
                        if (!file.mkdirs()) {
                            showMessage("创建下载目录失败了", TastyToast.ERROR);
                        }
                    }
                } else {
                    AndPermission.defaultSettingDialog(MainActivity.this, permisionReqCode).show();
                }
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            // 权限申请失败回调。
            if (requestCode == permisionCode) {
                // TODO ...
                if (!AndPermission.hasPermission(MainActivity.this, deniedPermissions)) {
                    // 是否有不再提示并拒绝的权限。
                    if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                        // 第一种：用AndPermission默认的提示语。
                        AndPermission.defaultSettingDialog(MainActivity.this, permisionReqCode).show();
                    } else {
                        AndPermission.defaultSettingDialog(MainActivity.this, permisionReqCode).show();
                    }
                }
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == permisionReqCode) {
            if (!AndPermission.hasPermission(MainActivity.this, permission)) {
                showMessage("你拒绝了读写存储卡权限，这将影响下载视频等功能！", TastyToast.WARNING);
            }
        }
        if (mCurrentFragment != null) {
            mCurrentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static final int MIN_CLICK_DELAY_TIME = 2000;
    private long lastClickTime = 0;

    @Override
    public void onBackPressed() {
        if (mCurrentFragment instanceof BaseMainFragment && ((BaseMainFragment) mCurrentFragment).onBackPressed()) {
            return;
        }
        showMessage("再次点击退出程序", TastyToast.INFO);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
        } else {
            FileDownloader.getImpl().pauseAll();
            FileDownloader.getImpl().unBindService();
            //没啥意义
            if (!existActivityWithAnimation && !isFinishing()) {
                super.onBackPressed();
            }
            finishAffinity();
            new Handler().postDelayed(() -> {
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
            }, 500);
        }
    }

    private void goToSearchVideo() {
        String[] items = {"搜9*Porn视频", "搜a*gle视频"};
        new QMUIDialog.CheckableDialogBuilder(this)
                .setTitle("搜索啥呀")
                .addItems(items, (dialog, which) -> {
                    dialog.dismiss();
                    switch (which) {
                        case 0:
                            if (!presenter.isUserLogin()) {
                                showMessage("请先登录", TastyToast.INFO);
                                Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
                                intent.putExtra(Keys.KEY_INTENT_LOGIN_FOR_ACTION, KeysActivityRequestResultCode.LOGIN_ACTION_FOR_SEARCH_91PRON_VIDEO);
                                startActivityForResultWithAnimation(intent, Constants.USER_LOGIN_REQUEST_CODE);
                                return;
                            }
                            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                            startActivityWithAnimation(intent);
                            break;
                        case 1:
                            Intent axgleIntent = new Intent(MainActivity.this, SearchAxgleVideoActivity.class);
                            startActivityWithAnimation(axgleIntent);
                            break;
                    }
                })
                .show();

    }

    private void showUpdateDialog(final UpdateVersion updateVersion) {
        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(this);
        builder.setTitle("发现新版本--v" + updateVersion.getVersionName());
        builder.setMessage(updateVersion.getUpdateMessage());
        builder.addAction("立即更新", (dialog, index) -> {
            dialog.dismiss();
            showMessage("开始下载", TastyToast.INFO);
            Intent intent = new Intent(MainActivity.this, UpdateDownloadService.class);
            intent.putExtra("updateVersion", updateVersion);
            startService(intent);
        });
        builder.addAction("稍后更新", (dialog, index) -> dialog.dismiss());
        builder.addAction("该版本不再提示", (dialog, index) -> {
            //保存版本号，用户对于此版本选择了不在提示
            presenter.setIgnoreUpdateVersionCode(updateVersion.getVersionCode());
            dialog.dismiss();
        });
        builder.show();
    }

    private void showNewNoticeDialog(final Notice notice) {
        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(this);
        builder.setTitle("新公告");
        builder.setMessage(notice.getNoticeMessage());
        builder.addAction("我知道了", (dialog, index) -> {
            dialog.dismiss();
            presenter.saveNoticeVersionCode(notice.getVersionCode());
        });
        builder.show();
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return mainPresenter;
    }


    @Override
    public void showError(String message) {

    }

    @Override
    public void showLoading(boolean pullToRefresh) {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isBackground = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isBackground = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTryToReleaseMemory(LowMemoryEvent lowMemoryEvent) {
        if (contentFrameLayout == null || fragmentManager == null || !isBackground) {
            return;
        }
        if (!BuildConfig.DEBUG) {
            //Bugsnag.notify(new Throwable(TAG + ":LowMemory,try to release some memory now!"), Severity.INFO);
        }
        try {
            Logger.t(TAG).d("start try to release memory ....");
            FragmentTransaction bt = fragmentManager.beginTransaction();
            for (int i = 0; i < 5; i++) {
                //只移除当前未选中的
                if (i != selectIndex) {
                    String name = FragmentUtils.makeFragmentName(contentFrameLayout.getId(), i);
                    Fragment fragment = fragmentManager.findFragmentByTag(name);
                    if (fragment != null) {
                        bt.remove(fragment);
                        setNull(i);
                    }
                }
            }
            bt.commitAllowingStateLoss();
            //通知系统尝试释放内存
            System.gc();
            System.runFinalization();
            Logger.t(TAG).d("try to release memory success !!!");
        } catch (Exception e) {
            e.printStackTrace();
            if (!BuildConfig.DEBUG) {
                //Bugsnag.notify(new Throwable(TAG + " tryToReleaseMemory error::", e), Severity.WARNING);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void urlRedirectEvent(final UrlRedirectEvent urlRedirectEvent) {
        if (isBackground) {
            return;
        }
        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("服务器连接发生跳转，新地址为：\n" + urlRedirectEvent.getNewUrl() + "\n原地址：\n" + urlRedirectEvent.getOldUrl() + "\n是否保存为最新地址？");
        builder.addAction("保存", (dialog, index) -> {
            if (Api.PORN9_VIDEO_DOMAIN_NAME.equals(urlRedirectEvent.getHeader())) {
                presenter.setPorn9VideoAddress(urlRedirectEvent.getNewUrl());
                showMessage("保存成功", TastyToast.SUCCESS);
            } else if (Api.PORN9_FORUM_DOMAIN_NAME.equals(urlRedirectEvent.getHeader())) {
                presenter.setPorn9ForumAddress(urlRedirectEvent.getNewUrl());
                showMessage("保存成功", TastyToast.SUCCESS);
            } else {
                showMessage("保存失败，信息错误", TastyToast.ERROR);
            }

            dialog.dismiss();
        });
        builder.addAction("取消", (dialog, index) -> dialog.dismiss());
        builder.show();
    }

    private void setNull(int position) {
        switch (position) {
            case 0:
                mMainPxgavFragment = null;
                mMain9PronVideoFragment = null;
                break;
            case 1:
                mMaiMeiZiTuFragment = null;
                mMain99MmFragment = null;
                break;
            case 2:
                mMain9ForumFragment = null;
                break;
            case 3:
                mMusicFragment = null;
                break;
            case 4:
                mMineFragment = null;
                break;
            default:
        }
    }
}
