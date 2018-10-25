package com.u9porn.ui.pav;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.data.DataManager;
import com.u9porn.data.model.pxgav.PavModel;
import com.u9porn.rxjava.CallBackWrapper;
import com.u9porn.rxjava.RxSchedulersHelper;
import com.u9porn.ui.MvpBasePresenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * @author flymegoc
 * @date 2018/1/30
 */

public class PavPresenter extends MvpBasePresenter<PavView> implements IPav {
    private static final String TAG = PavPresenter.class.getSimpleName();

    private int page = 2;
    private DataManager dataManager;

    @Inject
    public PavPresenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        super(provider);
        this.dataManager = dataManager;
    }

    @Override
    public void videoList(String category, boolean pullToRefresh) {
        if (pullToRefresh) {
            page = 2;
        }
        dataManager.loadPavListByCategory(category, pullToRefresh)
                .compose(RxSchedulersHelper.<List<PavModel>>ioMainThread())
                .compose(provider.<List<PavModel>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<PavModel>>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<PavView>() {
                            @Override
                            public void run(@NonNull PavView view) {
                                view.showLoading(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<PavModel> pavModels) {
                        ifViewAttached(new ViewAction<PavView>() {
                            @Override
                            public void run(@NonNull PavView view) {
                                view.setData(pavModels);
                                view.showContent();
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<PavView>() {
                            @Override
                            public void run(@NonNull PavView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });

    }

    @Override
    public void moreVideoList(String category, boolean pullToRefresh) {
        dataManager.loadMorePavListByCategory(category, page, pullToRefresh)
                .compose(RxSchedulersHelper.<List<PavModel>>ioMainThread())
                .compose(provider.<List<PavModel>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<PavModel>>() {
                    @Override
                    public void onSuccess(final List<PavModel> pavModels) {
                        ifViewAttached(new ViewAction<PavView>() {
                            @Override
                            public void run(@NonNull PavView view) {
                                if (pavModels.size() == 0) {
                                    Logger.t(TAG).d("没有数据哦");
                                } else {
                                    view.setMoreData(pavModels);
                                    page++;
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(String msg, int code) {
                        ifViewAttached(new ViewAction<PavView>() {
                            @Override
                            public void run(@NonNull PavView view) {
                                view.loadMoreFailed();
                            }
                        });
                    }
                });

    }
}
