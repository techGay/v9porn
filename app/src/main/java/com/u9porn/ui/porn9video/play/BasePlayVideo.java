package com.u9porn.ui.porn9video.play;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.rubensousa.floatingtoolbar.FloatingToolbar;
import com.helper.loadviewhelper.help.OnLoadViewListener;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.jaeger.library.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.adapter.PlayFragmentAdapter;
import com.u9porn.constants.Keys;
import com.u9porn.constants.KeysActivityRequestResultCode;
import com.u9porn.data.db.entity.Category;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.data.db.entity.VideoResult;
import com.u9porn.service.DownloadVideoService;
import com.u9porn.ui.MvpActivity;
import com.u9porn.ui.porn9video.author.AuthorFragment;
import com.u9porn.ui.porn9video.comment.CommentFragment;
import com.u9porn.ui.porn9video.index.IndexFragment;
import com.u9porn.ui.porn9video.user.UserLoginActivity;
import com.u9porn.ui.porn9video.videolist.VideoListFragment;
import com.u9porn.utils.DialogUtils;
import com.u9porn.utils.LoadHelperUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public abstract class BasePlayVideo extends MvpActivity<PlayVideoView, PlayVideoPresenter> implements PlayVideoView {

    private final String TAG = BasePlayVideo.class.getSimpleName();

    @BindView(R.id.floatingToolbar)
    FloatingToolbar floatingToolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.tv_play_video_title)
    TextView tvPlayVideoTitle;
    @BindView(R.id.tv_play_video_author)
    TextView tvPlayVideoAuthor;
    @BindView(R.id.tv_play_video_add_date)
    TextView tvPlayVideoAddDate;
    @BindView(R.id.tv_play_video_info)
    TextView tvPlayVideoInfo;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;

    @BindView(R.id.video_player_container)
    FrameLayout videoPlayerContainer;
    @BindView(R.id.tab_play)
    TabLayout tabPlay;
    @BindView(R.id.viewPager_play)
    ViewPager viewPagerPlay;

    private AlertDialog mAlertDialog;
    private AlertDialog favoriteDialog;

    private LoadViewHelper helper;

    protected V9PornItem v9PornItem;

    protected Category category;

    protected int skipPage;

    protected int position;

    @Inject
    protected CommentFragment commentFragment;

    @Inject
    protected AuthorFragment authorFragment;

    @Inject
    protected PlayFragmentAdapter playFragmentAdapter;

    @Inject
    protected PlayVideoPresenter playVideoPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_play_video);
        ButterKnife.bind(this);
        initPlayerView();
        initIntentData();
        initDialog();
        initLoadHelper();
        initData();
        initBottomMenu();

        initTab();
    }

    private void initIntentData() {
        v9PornItem = (V9PornItem) getIntent().getSerializableExtra(Keys.KEY_INTENT_V9PORN_ITEM);
        category = (Category) getIntent().getSerializableExtra(Keys.KEY_INTENT_CATEGORY_ITEM);
        skipPage = getIntent().getIntExtra(Keys.KEY_INTENT_SKIP_PAGE, 0);
        position = getIntent().getIntExtra(Keys.KEY_INTENT_SCROLL_TO_POSITION, 0);
    }

    /**
     * 底部切换标签tab
     */
    private void initTab() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(commentFragment);
        if (category != null) {
            if ("index".equalsIgnoreCase(category.getCategoryValue())) {
                IndexFragment indexFragment = IndexFragment.getInstance();
                indexFragment.setCategory(category);
                indexFragment.setPosition(position);
                fragments.add(indexFragment);
            } else {
                VideoListFragment videoListFragment = VideoListFragment.getInstance();
                videoListFragment.setCategory(category);
                videoListFragment.setSkipPage(skipPage);
                videoListFragment.setPosition(position);
                fragments.add(videoListFragment);
            }
        }else {
            //TODO
        }

        fragments.add(authorFragment);
        playFragmentAdapter.setData(fragments);
        viewPagerPlay.setAdapter(playFragmentAdapter);
        tabPlay.setupWithViewPager(viewPagerPlay);
    }

    /**
     * 初始化视频引擎视图
     */
    public abstract void initPlayerView();

    public void initData() {
        V9PornItem tmp = presenter.findV9PornItemByViewKey(v9PornItem.getViewKey());
        //登录之后，第一次需要刷新获取uid,否则无法使用收藏功能
        if (tmp == null || tmp.getVideoResultId() == 0 || presenter.isLoadForUid()) {
            if (tmp == null) {
                presenter.loadVideoUrl(v9PornItem);
            } else {
                presenter.loadVideoUrl(tmp);
            }
        } else {
            v9PornItem = tmp;
            videoPlayerContainer.setVisibility(View.VISIBLE);
            Logger.t(TAG).d("使用已有播放地址");
            //浏览历史
            v9PornItem.setViewHistoryDate(new Date());
            presenter.updateV9PornItemForHistory(v9PornItem);
            VideoResult videoResult = v9PornItem.getVideoResult();
            setToolBarLayoutInfo(v9PornItem);
            playVideo(v9PornItem.getTitle(), presenter.getVideoCacheProxyUrl(videoResult.getVideoUrl()), videoResult.getVideoName(), videoResult.getThumbImgUrl());
            //加载评论
            if (commentFragment != null) {
                commentFragment.setV9PornItem(v9PornItem);
                commentFragment.loadVideoComment(videoResult.getVideoId(), v9PornItem.getViewKey(), true);
            }
            if (authorFragment != null) {
                authorFragment.setV9PornItem(v9PornItem);
            }
        }
    }

    private void setToolBarLayoutInfo(final V9PornItem v9PornItem) {
        if (v9PornItem.getVideoResultId() == 0) {
            return;
        }
        String searchTitleTag = "...";
        VideoResult videoResult = v9PornItem.getVideoResult();
        if (v9PornItem.getTitle().contains(searchTitleTag) || v9PornItem.getTitle().endsWith(searchTitleTag)) {
            tvPlayVideoTitle.setText(videoResult.getVideoName());
        } else {
            tvPlayVideoTitle.setText(v9PornItem.getTitle());
        }
        tvPlayVideoAuthor.setText(videoResult.getOwnerName());
        tvPlayVideoAddDate.setText(videoResult.getAddDate());
        tvPlayVideoInfo.setText(videoResult.getUserOtherInfo());
    }

    private void initLoadHelper() {
        helper = new LoadViewHelper(viewPagerPlay);
        helper.setListener(new OnLoadViewListener() {
            @Override
            public void onRetryClick() {
                presenter.loadVideoUrl(v9PornItem);
            }
        });
    }

    private void initDialog() {
        mAlertDialog = DialogUtils.initLoadingDialog(this, "视频地址解析中...");
        favoriteDialog = DialogUtils.initLoadingDialog(this, "收藏中,请稍后...");
    }

    private void initBottomMenu() {
        floatingToolbar.attachFab(fab);
        floatingToolbar.setClickListener(new FloatingToolbar.ItemClickListener() {
            @Override
            public void onItemClick(MenuItem item) {
                onOptionsItemSelected(item);
            }

            @Override
            public void onItemLongClick(MenuItem item) {

            }
        });
    }

    /**
     * 开始播放视频
     *
     * @param title      视频标题
     * @param videoUrl   视频链接
     * @param name       视频名字
     * @param thumImgUrl 视频缩略图
     */
    public abstract void playVideo(String title, String videoUrl, String name, String thumImgUrl);


    @NonNull
    @Override
    public PlayVideoPresenter createPresenter() {
        return playVideoPresenter;
    }

    @Override
    public void showParsingDialog() {
        if (mAlertDialog == null) {
            return;
        }
        mAlertDialog.show();
    }

    @Override
    public void parseVideoUrlSuccess(V9PornItem v9PornItem) {
        this.v9PornItem = v9PornItem;
        videoPlayerContainer.setVisibility(View.VISIBLE);
        setToolBarLayoutInfo(v9PornItem);
        VideoResult videoResult = v9PornItem.getVideoResult();
        //开始播放
        playVideo(v9PornItem.getTitle(), presenter.getVideoCacheProxyUrl(videoResult.getVideoUrl()), "", videoResult.getThumbImgUrl());
        helper.showContent();
        if (commentFragment != null) {
            commentFragment.setV9PornItem(v9PornItem);
            commentFragment.loadVideoComment(videoResult.getVideoId(), v9PornItem.getViewKey(), true);
        }
        if (authorFragment != null) {
            authorFragment.setV9PornItem(v9PornItem);
        }
        boolean neverAskForWatchDownloadTip = presenter.isNeverAskForWatchDownloadTip();
        if (!neverAskForWatchDownloadTip) {
            showWatchDownloadVideoTipDialog();
        }
        dismissDialog();
    }

    private void showWatchDownloadVideoTipDialog() {
        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("1. 通常你无法在线观看视频就意味着你也无法下载视频，所以如果你不能在线观看视频就不要想着下载了再看了，那样绝大多数时候都是不能下载的；\n" +
                "2. 如果在线观看速度慢可以选择先下载后再观看，因为是多线程下载，有时候能够比在线观看要快；\n" +
                "3. 如果想要更好的在线观看和下载体验，目前最好的办法就是使用梯子（非设置中的HTTP代理）；\n" +
                "4. 查看该作者其他视频（需要登录帐号）。");
        builder.addAction("我知道了", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        });
        builder.addAction("不再提示", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
                presenter.setNeverAskForWatchDownloadTip(true);
            }
        });
        builder.show();
    }

    @Override
    public void errorParseVideoUrl(String errorMessage) {
        dismissDialog();
        helper.showError();
        LoadHelperUtils.setErrorText(helper.getLoadError(), R.id.tv_error_text, "解析视频地址失败了，点击重试");
        showMessage(errorMessage, TastyToast.ERROR);
    }

    @Override
    public void favoriteSuccess() {
        presenter.setFavoriteNeedRefresh(true);
        showMessage("收藏成功", TastyToast.SUCCESS);
    }

    @Override
    public void showError(String message) {
        showMessage(message, TastyToast.ERROR);
        dismissDialog();
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        helper.showLoading();
        LoadHelperUtils.setLoadingText(helper.getLoadIng(), R.id.tv_loading_text, "拼命加载评论中...");
    }

    @Override
    public void showContent() {
        helper.showContent();
        dismissDialog();
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
        dismissDialog();
    }

    private void dismissDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing() && !isFinishing()) {
            mAlertDialog.dismiss();
        }
        if (favoriteDialog != null && favoriteDialog.isShowing() && !isFinishing()) {
            favoriteDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_play_collect) {
            favoriteVideo();
            return true;
        } else if (id == R.id.menu_play_download) {
            startDownloadVideo();
            return true;
        } else if (id == R.id.menu_play_share) {
            shareVideoUrl();
            return true;
        } else if (id == R.id.menu_play_comment) {
            showMessage("向下滑动即可评论", TastyToast.INFO);
            return true;
        } else if (id == R.id.menu_play_close) {
            floatingToolbar.hide();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startDownloadVideo() {
        presenter.downloadVideo(v9PornItem, false);
        Intent intent = new Intent(this, DownloadVideoService.class);
        startService(intent);
    }

    private void favoriteVideo() {
        if (v9PornItem == null || v9PornItem.getVideoResultId() == 0) {
            showMessage("还未成功解析视频链接，不能收藏！", TastyToast.INFO);
            return;
        }
        VideoResult videoResult = v9PornItem.getVideoResult();
        if (!presenter.isUserLogin()) {
            goToLogin(KeysActivityRequestResultCode.LOGIN_ACTION_FOR_GET_UID);
            showMessage("请先登录", TastyToast.SUCCESS);
            return;
        }
        if (Integer.parseInt(videoResult.getAuthorId()) == presenter.getLoginUserId()) {
            showMessage("不能收藏自己的视频", TastyToast.WARNING);
            return;
        }
        favoriteDialog.show();
        presenter.favorite(String.valueOf(presenter.getLoginUserId()), videoResult.getVideoId(), videoResult.getAuthorId());
    }

    private void shareVideoUrl() {
        if (v9PornItem == null || v9PornItem.getVideoResultId() == 0) {
            showMessage("还未成功解析视频链接，不能分享！", TastyToast.INFO);
            return;
        }
        String url = v9PornItem.getVideoResult().getVideoUrl();
        if (TextUtils.isEmpty(url)) {
            showMessage("还未成功解析视频链接，不能分享！", TastyToast.INFO);
            return;
        }
        Intent textIntent = new Intent(Intent.ACTION_SEND);
        textIntent.setType("text/plain");
        textIntent.putExtra(Intent.EXTRA_TEXT, "链接：" + url);
        startActivity(Intent.createChooser(textIntent, "分享视频地址"));
    }

    /**
     * 去登录
     *
     * @param actionKey 登录之后的动作key
     */
    private void goToLogin(int actionKey) {
        Intent intent = new Intent(this, UserLoginActivity.class);
        intent.putExtra(Keys.KEY_INTENT_LOGIN_FOR_ACTION, actionKey);
        startActivityForResultWithAnimation(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == KeysActivityRequestResultCode.RESULT_FOR_LOOK_AUTHOR_VIDEO) {
            if (authorFragment != null) {
                authorFragment.loadAuthorVideos();
            }
        } else if (resultCode == KeysActivityRequestResultCode.RESULT_CODE_FOR_REFRESH_GET_UID) {
            Logger.t(TAG).d("登录成功，需要刷新以获取uid");
            presenter.loadVideoUrl(v9PornItem);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            //这里没必要，因为我们使用的是setColorForSwipeBack，并不会有这个虚拟的view，而是设置的padding
            StatusBarUtil.hideFakeStatusBarView(this);
        } else if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    public void setV9PornItems(V9PornItem v9PornItems) {
        this.v9PornItem = v9PornItems;
    }
}
