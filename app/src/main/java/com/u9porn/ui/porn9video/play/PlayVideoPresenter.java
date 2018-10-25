package com.u9porn.ui.porn9video.play;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.sdsmdg.tastytoast.TastyToast;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.data.DataManager;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.data.db.entity.VideoResult;
import com.u9porn.data.model.User;
import com.u9porn.exception.VideoException;
import com.u9porn.rxjava.CallBackWrapper;
import com.u9porn.rxjava.RetryWhenProcess;
import com.u9porn.rxjava.RxSchedulersHelper;
import com.u9porn.ui.download.DownloadPresenter;
import com.u9porn.ui.porn9video.favorite.FavoritePresenter;

import java.util.Date;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * @author flymegoc
 * @date 2017/11/15
 * @describe
 */
public class PlayVideoPresenter extends MvpBasePresenter<PlayVideoView> implements IPlay {

    private static final String TAG = PlayVideoPresenter.class.getSimpleName();

    private FavoritePresenter favoritePresenter;
    private DownloadPresenter downloadPresenter;

    private LifecycleProvider<Lifecycle.Event> provider;

    private int start = 1;
    private DataManager dataManager;

    @Inject
    public PlayVideoPresenter(FavoritePresenter favoritePresenter, DownloadPresenter downloadPresenter, LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        this.favoritePresenter = favoritePresenter;
        this.downloadPresenter = downloadPresenter;
        this.provider = provider;
        this.dataManager = dataManager;
    }

    @Override
    public void loadVideoUrl(final V9PornItem v9PornItem) {
        String viewKey = v9PornItem.getViewKey();
        dataManager.loadPorn9VideoUrl(viewKey)
                .map(new Function<VideoResult, VideoResult>() {
                    @Override
                    public VideoResult apply(VideoResult videoResult) throws VideoException {
                        if (TextUtils.isEmpty(videoResult.getVideoUrl())) {
                            if (VideoResult.OUT_OF_WATCH_TIMES.equals(videoResult.getId())) {
                                //尝试强行重置，并上报异常
                                dataManager.resetPorn91VideoWatchTime(true);
                               // Bugsnag.notify(new Throwable(TAG + "Ten videos each day address: " + dataManager.getPorn9VideoAddress()), Severity.WARNING);
                                throw new VideoException("观看次数达到上限了,请更换地址或者代理服务器！");
                            } else {
                                throw new VideoException("解析视频链接失败了");
                            }
                        }
                        return videoResult;
                    }
                })
                .retryWhen(new RetryWhenProcess(RetryWhenProcess.PROCESS_TIME))
                .compose(RxSchedulersHelper.<VideoResult>ioMainThread())
                .compose(provider.<VideoResult>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<VideoResult>() {
                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<PlayVideoView>() {
                            @Override
                            public void run(@NonNull PlayVideoView view) {
                                view.showParsingDialog();
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final VideoResult videoResult) {
                        dataManager.resetPorn91VideoWatchTime(false);
                        ifViewAttached(new ViewAction<PlayVideoView>() {
                            @Override
                            public void run(@NonNull PlayVideoView view) {
                                view.parseVideoUrlSuccess(saveVideoUrl(videoResult, v9PornItem));
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<PlayVideoView>() {
                            @Override
                            public void run(@NonNull PlayVideoView view) {
                                view.errorParseVideoUrl(msg);
                            }
                        });
                    }
                });
    }

    @Override
    public String getVideoCacheProxyUrl(String originalVideoUrl) {
        return dataManager.getVideoCacheProxyUrl(originalVideoUrl);
    }

    @Override
    public boolean isUserLogin() {
        return dataManager.isUserLogin();
    }

    @Override
    public int getLoginUserId() {
        return dataManager.getUser().getUserId();
    }

    @Override
    public void updateV9PornItemForHistory(V9PornItem v9PornItem) {
        dataManager.updateV9PornItem(v9PornItem);
    }

    @Override
    public V9PornItem findV9PornItemByViewKey(String viewKey) {
        return dataManager.findV9PornItemByViewKey(viewKey);
    }

    @Override
    public boolean isNeverAskForWatchDownloadTip() {
        return dataManager.isNeverAskForWatchDownloadTip();
    }

    @Override
    public void setNeverAskForWatchDownloadTip(boolean neverAskForWatchDownloadTip) {
        dataManager.setNeverAskForWatchDownloadTip(neverAskForWatchDownloadTip);
    }

    @Override
    public void setFavoriteNeedRefresh(boolean favoriteNeedRefresh) {
        dataManager.setFavoriteNeedRefresh(favoriteNeedRefresh);
    }

    private V9PornItem saveVideoUrl(VideoResult videoResult, V9PornItem v9PornItem) {
        dataManager.saveVideoResult(videoResult);
        v9PornItem.setVideoResult(videoResult);
        v9PornItem.setViewHistoryDate(new Date());
        dataManager.saveV9PornItem(v9PornItem);
        return v9PornItem;
    }

    @Override
    public void downloadVideo(V9PornItem v9PornItem, boolean isForceReDownload) {

        downloadPresenter.downloadVideo(v9PornItem, isForceReDownload, new DownloadPresenter.DownloadListener() {
            @Override
            public void onSuccess(final String message) {
                ifViewAttached(new ViewAction<PlayVideoView>() {
                    @Override
                    public void run(@NonNull PlayVideoView view) {
                        view.showMessage(message, TastyToast.SUCCESS);
                    }
                });
            }

            @Override
            public void onError(final String message) {
                ifViewAttached(new ViewAction<PlayVideoView>() {
                    @Override
                    public void run(@NonNull PlayVideoView view) {
                        view.showMessage(message, TastyToast.ERROR);
                    }
                });
            }
        });
    }

    @Override
    public void favorite(String uId, String videoId, String ownnerId) {
        favoritePresenter.favorite(uId, videoId, ownnerId, new FavoritePresenter.FavoriteListener() {
            @Override
            public void onSuccess(String message) {
                ifViewAttached(new ViewAction<PlayVideoView>() {
                    @Override
                    public void run(@NonNull PlayVideoView view) {
                        view.favoriteSuccess();
                    }
                });
            }

            @Override
            public void onError(final String message) {
                ifViewAttached(new ViewAction<PlayVideoView>() {
                    @Override
                    public void run(@NonNull PlayVideoView view) {
                        view.showError(message);
                    }
                });
            }
        });
    }


    /**
     * 是否需要为了解析uid，只有登录状态下且uid还未解析过才需要解析
     *
     * @return true
     */
    public boolean isLoadForUid() {
        User user = dataManager.getUser();
        return dataManager.isUserLogin() && user.getUserId() == 0;
    }
}
