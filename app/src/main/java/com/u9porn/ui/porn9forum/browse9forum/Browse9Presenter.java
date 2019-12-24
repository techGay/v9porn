package com.u9porn.ui.porn9forum.browse9forum;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.data.DataManager;
import com.u9porn.data.model.F9PornContent;
import com.u9porn.rxjava.CallBackWrapper;
import com.u9porn.rxjava.RxSchedulersHelper;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * @author flymegoc
 * @date 2018/1/24
 */

public class Browse9Presenter extends MvpBasePresenter<Browse9View> implements IBrowse9 {

    private DataManager dataManager;
    private LifecycleProvider<Lifecycle.Event> provider;

    @Inject
    public Browse9Presenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        this.provider = provider;
        this.dataManager = dataManager;
    }

    @Override
    public void loadContent(Long tid) {
        final boolean isNightModel = dataManager.isOpenNightMode();
        dataManager.loadPorn9ForumContent(tid, isNightModel)
                .compose(RxSchedulersHelper.ioMainThread())
                .compose(provider.bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<F9PornContent>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<Browse9View>() {
                            @Override
                            public void run(@NonNull Browse9View view) {
                                view.showLoading(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final F9PornContent f9PornContent) {
                        ifViewAttached(new ViewAction<Browse9View>() {
                            @Override
                            public void run(@NonNull Browse9View view) {
                                view.showContent();
                                view.loadContentSuccess(f9PornContent.getContent(), f9PornContent.getImageList(),isNightModel);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<Browse9View>() {
                            @Override
                            public void run(@NonNull Browse9View view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }

    @Override
    public void setNeedShowTipFirstViewForum9Content(boolean needShow) {
        dataManager.setNeedShowTipFirstViewForum9Content(needShow);
    }

    @Override
    public boolean isNeedShowTipFirstViewForum9Content() {
        return dataManager.isNeedShowTipFirstViewForum9Content();
    }
}
