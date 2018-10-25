package com.u9porn.ui.axgle;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.data.DataManager;
import com.u9porn.data.model.axgle.AxgleResponse;
import com.u9porn.data.model.axgle.AxgleVideo;
import com.u9porn.rxjava.CallBackWrapper;
import com.u9porn.rxjava.RxSchedulersHelper;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * @author megoc
 */
public class AxglePresenter extends MvpBasePresenter<AxgleView> implements IAxgle {
    private DataManager dataManager;
    private LifecycleProvider<Lifecycle.Event> provider;
    private int page = 1;
    private String o = "mr";
    private String t = "a";
    private String type = "public";
    private boolean isHaveMore;

    @Inject
    public AxglePresenter(DataManager dataManager, LifecycleProvider<Lifecycle.Event> provider) {
        this.dataManager = dataManager;
        this.provider = provider;
    }

    @Override
    public void videos(String cid, final boolean pullToRefresh) {
        if (pullToRefresh) {
            page = 1;
        }
        dataManager.axgleVideos(page, o, t, type, cid, 10)
                .map(new Function<AxgleResponse, List<AxgleVideo>>() {
                    @Override
                    public List<AxgleVideo> apply(AxgleResponse axgleResponse) throws Exception {
                        isHaveMore = axgleResponse.isHas_more();
                        return axgleResponse.getVideos();
                    }
                })
                .compose(provider.<List<AxgleVideo>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .compose(RxSchedulersHelper.<List<AxgleVideo>>ioMainThread())
                .subscribe(new CallBackWrapper<List<AxgleVideo>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        ifViewAttached(new ViewAction<AxgleView>() {
                            @Override
                            public void run(@NonNull AxgleView view) {
                                if (page == 1) {
                                    view.showLoading(pullToRefresh);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<AxgleVideo> axgleVideos) {
                        ifViewAttached(new ViewAction<AxgleView>() {
                            @Override
                            public void run(@NonNull AxgleView view) {
                                if (page == 1) {
                                    view.setData(axgleVideos);
                                } else {
                                    view.setMoreData(axgleVideos);
                                }
                                if (isHaveMore) {
                                    page++;
                                } else {
                                    view.noMoreData();
                                }
                                view.showContent();
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<AxgleView>() {
                            @Override
                            public void run(@NonNull AxgleView view) {
                                if (page == 1) {
                                    view.showError(msg);
                                } else {
                                    view.loadMoreFailed();
                                }
                            }
                        });
                    }
                });
    }
}
