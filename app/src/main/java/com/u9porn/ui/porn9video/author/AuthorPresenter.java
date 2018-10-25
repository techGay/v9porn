package com.u9porn.ui.porn9video.author;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.data.DataManager;
import com.u9porn.data.model.BaseResult;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.exception.MessageException;
import com.u9porn.rxjava.CallBackWrapper;
import com.u9porn.rxjava.RetryWhenProcess;
import com.u9porn.rxjava.RxSchedulersHelper;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * @author flymegoc
 * @date 2018/1/8
 */

public class AuthorPresenter extends MvpBasePresenter<AuthorView> implements IAuthor {

    private static final String TAG = AuthorPresenter.class.getSimpleName();
    private LifecycleProvider<Lifecycle.Event> provider;
    private int page = 1;
    private Integer totalPage;
    private boolean cleanCache;
    private DataManager dataManager;

    @Inject
    public AuthorPresenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        this.provider = provider;
        this.dataManager = dataManager;
        Logger.t(TAG).d("AuthorPresenter初始化了.....");
    }

    @Override
    public void authorVideos(String uid, final boolean pullToRefresh) {
        String type = "public";
        if (pullToRefresh) {
            page = 1;
            cleanCache = true;
        }
        dataManager.loadPorn9authorVideos(uid, type, page, cleanCache)
                .map(new Function<BaseResult<List<V9PornItem>>, List<V9PornItem>>() {
                    @Override
                    public List<V9PornItem> apply(BaseResult<List<V9PornItem>> baseResult) throws Exception {
                        if (baseResult.getCode() == BaseResult.ERROR_CODE) {
                            throw new MessageException(baseResult.getMessage());
                        }
                        if (page == 1) {
                            totalPage = baseResult.getTotalPage();
                        }
                        return baseResult.getData();
                    }
                })
                .retryWhen(new RetryWhenProcess(RetryWhenProcess.PROCESS_TIME))
                .compose(RxSchedulersHelper.<List<V9PornItem>>ioMainThread())
                .compose(provider.<List<V9PornItem>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<V9PornItem>>() {
                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<AuthorView>() {
                            @Override
                            public void run(@NonNull AuthorView view) {
                                if (page == 1 && !pullToRefresh) {
                                    view.showLoading(pullToRefresh);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<V9PornItem> v9PornItems) {
                        ifViewAttached(new ViewAction<AuthorView>() {
                            @Override
                            public void run(@NonNull AuthorView view) {
                                if (page == 1) {
                                    view.setData(v9PornItems);
                                    view.showContent();
                                } else {
                                    view.loadMoreDataComplete();
                                    view.setMoreData(v9PornItems);
                                }
                                //已经最后一页了
                                if (page >= totalPage) {
                                    view.noMoreData();
                                } else {
                                    page++;
                                }
                                view.showContent();
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        //首次加载失败，显示重试页
                        ifViewAttached(new ViewAction<AuthorView>() {
                            @Override
                            public void run(@NonNull AuthorView view) {
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

    @Override
    public boolean isUserLogin() {
        return dataManager.isUserLogin();
    }
}
