package com.u9porn.ui.images.huaban;


import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.data.DataManager;
import com.u9porn.data.model.HuaBan;
import com.u9porn.rxjava.CallBackWrapper;
import com.u9porn.rxjava.RxSchedulersHelper;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

public class HuaBanPresenter extends MvpBasePresenter<HuaBanView> implements IHuaBan {

    private LifecycleProvider<Lifecycle.Event> provider;
    private DataManager dataManager;
    private int page;

    @Inject
    public HuaBanPresenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        this.provider = provider;
        this.dataManager = dataManager;
    }

    @Override
    public void findPictures(int categoryId, final boolean pullToRefresh) {
        if (pullToRefresh) {
            page = 1;
        }
        dataManager.findPictures(categoryId, page)
                .compose(RxSchedulersHelper.<List<HuaBan.Picture>>ioMainThread())
                .compose(provider.<List<HuaBan.Picture>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<HuaBan.Picture>>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<HuaBanView>() {
                            @Override
                            public void run(@NonNull HuaBanView view) {
                                view.showLoading(pullToRefresh);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<HuaBan.Picture> pictures) {
                        ifViewAttached(new ViewAction<HuaBanView>() {
                            @Override
                            public void run(@NonNull HuaBanView view) {
                                if (page == 1) {
                                    view.setData(pictures);
                                    view.showContent();
                                } else {
                                    view.setMoreData(pictures);
                                }
                                page++;
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<HuaBanView>() {
                            @Override
                            public void run(@NonNull HuaBanView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }
}
