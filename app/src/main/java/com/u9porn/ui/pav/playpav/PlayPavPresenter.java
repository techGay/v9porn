package com.u9porn.ui.pav.playpav;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.data.DataManager;
import com.u9porn.data.model.pxgav.PavVideoParserJsonResult;
import com.u9porn.rxjava.CallBackWrapper;
import com.u9porn.rxjava.RxSchedulersHelper;
import com.u9porn.ui.MvpBasePresenter;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * @author flymegoc
 * @date 2018/1/30
 */

public class PlayPavPresenter extends MvpBasePresenter<PlayPavView> implements IPlayPav {
    private static final String TAG = PlayPavPresenter.class.getSimpleName();

    private DataManager dataManager;

    @Inject
    public PlayPavPresenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        super(provider);
        this.dataManager = dataManager;
    }

    @Override
    public void parseVideoUrl(String url, String pId, boolean pullToRefresh) {

        dataManager.loadPavVideoUrl(url, pId, pullToRefresh)
                .compose(RxSchedulersHelper.<PavVideoParserJsonResult>ioMainThread())
                .compose(provider.<PavVideoParserJsonResult>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<PavVideoParserJsonResult>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<PlayPavView>() {
                            @Override
                            public void run(@NonNull PlayPavView view) {
                                view.showLoading(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final PavVideoParserJsonResult pavVideoParserJsonResult) {
                        ifViewAttached(new ViewAction<PlayPavView>() {
                            @Override
                            public void run(@NonNull PlayPavView view) {
                                view.showContent();
                                view.playVideo(pavVideoParserJsonResult);
                                view.listVideo(pavVideoParserJsonResult.getPavModelList());
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<PlayPavView>() {
                            @Override
                            public void run(@NonNull PlayPavView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }

    @Override
    public String getVideoCacheProxyUrl(final String originalVideoUrl) {
        return dataManager.getVideoCacheProxyUrl(originalVideoUrl);
    }
}
