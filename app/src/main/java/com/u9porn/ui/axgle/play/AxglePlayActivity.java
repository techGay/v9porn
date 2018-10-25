package com.u9porn.ui.axgle.play;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.flymegoc.exolibrary.widget.ExoVideoControlsMobile;
import com.flymegoc.exolibrary.widget.ExoVideoView;
import com.jaeger.library.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.constants.Keys;
import com.u9porn.data.model.axgle.AxgleVideo;
import com.u9porn.ui.MvpActivity;
import com.u9porn.utils.DialogUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author megoc
 */
public class AxglePlayActivity extends MvpActivity<AxglePlayView, AxglePlayPresenter> implements AxglePlayView, OnPreparedListener {

    @Inject
    protected AxglePlayPresenter axglePlayPresenter;
    @BindView(R.id.video_view)
    ExoVideoView videoView;

    private AlertDialog alertDialog;
    private boolean isPauseByActivityEvent;
    private ExoVideoControlsMobile videoControlsMobile;
    private AxgleVideo axgleVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_axgle_play);
        ButterKnife.bind(this);

        axgleVideo = (AxgleVideo) getIntent().getSerializableExtra(Keys.KEY_INTENT_AXGLE_VIDEO_ITEM);
        if (axgleVideo == null) {
            return;
        }
        alertDialog = DialogUtils.initLoadingDialog(this, "获取视频地址中，请稍候...");
        videoView.setOnPreparedListener(this);
        videoControlsMobile = (ExoVideoControlsMobile) videoView.getVideoControls();
        videoControlsMobile.setOnBackButtonClickListener(new ExoVideoControlsMobile.OnBackButtonClickListener() {
            @Override
            public void onBackClick(View view) {
                onBackPressed();
            }
        });
        presenter.getPlayVideoUrl(axgleVideo.getVid());
    }

    @NonNull
    @Override
    public AxglePlayPresenter createPresenter() {
        return axglePlayPresenter;
    }

    @Override
    public void showLoading() {
        if (alertDialog != null && !alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    @Override
    public void getVideoUrlSuccess(String videoUrl) {
        Logger.t("AAA").d("视频播放地址：" + videoUrl);
        videoView.setVideoURI(Uri.parse(videoUrl));
        videoControlsMobile.setTitle(axgleVideo.getTitle());
        dismissDialog();
    }

    @Override
    public void getVideoUrlError() {
        showMessage("获取视频地址失败", TastyToast.ERROR);
        dismissDialog();
    }

    private void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onPrepared() {
        videoView.start();
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
    protected void onResume() {
        super.onResume();
        if (!videoView.isPlaying() && isPauseByActivityEvent) {
            isPauseByActivityEvent = false;
            videoView.start();
        }
    }

    @Override
    protected void onPause() {
        videoView.pause();
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
        videoView.release();
        super.onDestroy();
    }
}
