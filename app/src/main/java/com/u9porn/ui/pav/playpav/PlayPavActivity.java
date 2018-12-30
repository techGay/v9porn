package com.u9porn.ui.pav.playpav;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.flymegoc.exolibrary.widget.ExoVideoControlsMobile;
import com.flymegoc.exolibrary.widget.ExoVideoView;
import com.jaeger.library.StatusBarUtil;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.adapter.PaAdapter;
import com.u9porn.data.model.pxgav.PavModel;
import com.u9porn.data.model.pxgav.PavVideoParserJsonResult;
import com.u9porn.ui.MvpActivity;
import com.u9porn.utils.DialogUtils;
import com.u9porn.utils.GlideApp;
import com.u9porn.constants.Keys;
import com.u9porn.utils.MyHeaderInjector;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public class PlayPavActivity extends MvpActivity<PlayPavView, PlayPavPresenter> implements PlayPavView, OnPreparedListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.video_view)
    ExoVideoView videoPlayer;
    @BindView(R.id.play_container)
    FrameLayout playContainer;
    private ExoVideoControlsMobile videoControlsMobile;
    private boolean isPauseByActivityEvent = false;

    private AlertDialog alertDialog;

    @Inject
    protected PlayPavPresenter playPigAvPresenter;

    @Inject
    protected MyHeaderInjector myHeaderInjector;
    private PavModel pavModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_pav);
        ButterKnife.bind(this);
        setVideoViewHeight(playContainer);
        initDialog();
        videoControlsMobile = (ExoVideoControlsMobile) videoPlayer.getVideoControls();
        videoPlayer.setOnPreparedListener(this);
        videoControlsMobile.setOnBackButtonClickListener(new ExoVideoControlsMobile.OnBackButtonClickListener() {
            @Override
            public void onBackClick(View view) {
                onBackPressed();
            }
        });
        pavModel = (PavModel) getIntent().getSerializableExtra(Keys.KEY_INTENT_PAV_ITEM);
        if (pavModel != null) {
            parseVideoUrl(pavModel);
        } else {
            showMessage("参数错误，无法播放", TastyToast.WARNING);
        }
    }

    private void parseVideoUrl(PavModel pavModel) {
        videoControlsMobile.setTitle(pavModel.getTitle());
        presenter.parseVideoUrl(pavModel.getContentUrl(), pavModel.getpId(), false);
    }

    private void initDialog() {
        alertDialog = DialogUtils.initLoadingDialog(this, "解析视频地址中，请稍后...");
    }

    /**
     * 根据屏幕宽度信息重设videoview宽高为16：9比例
     */
    protected void setVideoViewHeight(View playerView) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) playerView.getLayoutParams();
        layoutParams.height = QMUIDisplayHelper.getScreenWidth(this) * 9 / 16;
        playerView.setLayoutParams(layoutParams);
    }

    @Override
    public void onPrepared() {
        videoPlayer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!videoPlayer.isPlaying() && isPauseByActivityEvent) {
            isPauseByActivityEvent = false;
            videoPlayer.start();
        }
    }

    @NonNull
    @Override
    public PlayPavPresenter createPresenter() {
        return playPigAvPresenter;
    }

    @Override
    protected void onPause() {
        videoPlayer.pause();
        isPauseByActivityEvent = true;
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        if (videoControlsMobile.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        videoPlayer.release();
        super.onDestroy();
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

    @Override
    public void playVideo(PavVideoParserJsonResult pavVideoParserJsonResult) {
        String url = pavVideoParserJsonResult.getFile();
        GlideApp.with(context).load(pavVideoParserJsonResult.getImage()).into(videoPlayer.getPreviewImageView());
        if (TextUtils.isEmpty(url)) {
            showMessage("播放地址无效", TastyToast.ERROR);
            return;
        }
        myHeaderInjector.getHashMap().put("Referer",pavModel.getContentUrl());
        myHeaderInjector.addHeaders(url);
        String proxyUrl = presenter.getVideoCacheProxyUrl(url);
        videoPlayer.setVideoURI(Uri.parse(proxyUrl));
    }

    @Override
    public void listVideo(List<PavModel> pavModelList) {
        PaAdapter paAdapter = new PaAdapter(R.layout.item_pav);
        paAdapter.setWidth(QMUIDisplayHelper.getScreenWidth(context));
        paAdapter.setNewData(pavModelList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(paAdapter);
        paAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PavModel pavModel = (PavModel) adapter.getItem(position);
                if (pavModel == null) {
                    return;
                }
                videoPlayer.pause();
                videoPlayer.reset();
                parseVideoUrl(pavModel);
            }
        });
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        alertDialog.show();
    }

    @Override
    public void showContent() {
        dismissDialog();
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    public void showError(String message) {
        showMessage(message, TastyToast.ERROR);
        dismissDialog();
    }

    private void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing() && !isFinishing()) {
            alertDialog.dismiss();
        }
    }
}
