package com.u9porn.ui.update;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.data.DataManager;
import com.u9porn.data.model.UpdateVersion;
import com.u9porn.rxjava.CallBackWrapper;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author flymegoc
 * @date 2017/12/22
 */
public class UpdatePresenter extends MvpBasePresenter<UpdateView> implements IUpdate {

    private LifecycleProvider<Lifecycle.Event> provider;

    private DataManager dataManager;

    @Inject
    public UpdatePresenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        this.provider = provider;
        this.dataManager = dataManager;
    }

    @Override
    public void checkUpdate(int versionCode) {
        checkUpdate(versionCode, null);
    }

    public void checkUpdate(final int versionCode, final UpdateListener updateListener) {
        dataManager.checkUpdate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<UpdateVersion>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<UpdateVersion>() {
                    @Override
                    public void onBegin(Disposable d) {

                    }

                    @Override
                    public void onSuccess(final UpdateVersion updateVersion) {
                        if (updateVersion.getVersionCode() > versionCode) {
                            if (updateListener != null) {
                                updateListener.needUpdate(updateVersion);
                            } else {
                                ifViewAttached(new ViewAction<UpdateView>() {
                                    @Override
                                    public void run(@NonNull UpdateView view) {
                                        view.needUpdate(updateVersion);
                                    }
                                });
                            }
                        } else {
                            if (updateListener != null) {
                                updateListener.noNeedUpdate();
                            } else {
                                ifViewAttached(new ViewAction<UpdateView>() {
                                    @Override
                                    public void run(@NonNull UpdateView view) {
                                        view.noNeedUpdate();
                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        if (updateListener != null) {
                            updateListener.checkUpdateError(msg);
                        } else {
                            ifViewAttached(new ViewAction<UpdateView>() {
                                @Override
                                public void run(@NonNull UpdateView view) {
                                    view.checkUpdateError(msg);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    public interface UpdateListener {
        void needUpdate(UpdateVersion updateVersion);

        void noNeedUpdate();

        void checkUpdateError(String message);
    }
}
