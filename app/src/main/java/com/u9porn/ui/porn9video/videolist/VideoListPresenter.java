package com.u9porn.ui.porn9video.videolist;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.data.DataManager;
import com.u9porn.data.model.BaseResult;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.di.PerActivity;
import com.u9porn.di.PerFragment;
import com.u9porn.rxjava.CallBackWrapper;
import com.u9porn.rxjava.RetryWhenProcess;
import com.u9porn.rxjava.RxSchedulersHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * @author flymegoc
 * @date 2017/11/16
 */

public class VideoListPresenter extends MvpBasePresenter<VideoListView> implements IVideoList {

    private static final String TAG = VideoListFragment.class.getSimpleName();
    private Integer totalPage = 1;
    private int page = 1;
    private LifecycleProvider<Lifecycle.Event> provider;
    /**
     * 本次强制刷新过那下面的请求也一起刷新
     */
    private boolean isLoadMoreCleanCache = false;
    private DataManager dataManager;
    private String m;

    @Inject
    public VideoListPresenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        this.provider = provider;
        this.dataManager = dataManager;
        Logger.t(TAG).d("VideoListPresenter init______________");
    }

    @Override
    public void loadVideoListData(final boolean pullToRefresh, boolean cleanCache, String category, int skipPage) {
        String viewType = "basic";
        //如果刷新则重置页数
        if (pullToRefresh) {
            page = 1;
            isLoadMoreCleanCache = true;
        }
        if (skipPage > 0) {
            page = skipPage;
        }
        if ("watch".equalsIgnoreCase(category)) {
            //最近更新
            action(dataManager.loadPorn9VideoRecentUpdates(category, page, cleanCache, isLoadMoreCleanCache)
                    .map(new Function<BaseResult<List<V9PornItem>>, List<V9PornItem>>() {
                        @Override
                        public List<V9PornItem> apply(BaseResult<List<V9PornItem>> baseResult) throws Exception {
                            if (page == 1) {
                                totalPage = baseResult.getTotalPage();
                            }
                            return baseResult.getData();
                        }
                    }), pullToRefresh, skipPage);
        } else {
            //其他栏目
            if (!"top1".equals(category)) {
                m = null;
            } else {
                m = "-1";
            }
            Observable<List<V9PornItem>> ob = dataManager.loadPorn9VideoByCategory(category, viewType, page, m, cleanCache, isLoadMoreCleanCache)
                    .map(new Function<BaseResult<List<V9PornItem>>, List<V9PornItem>>() {
                        @Override
                        public List<V9PornItem> apply(BaseResult<List<V9PornItem>> baseResult) throws Exception {
                            if (page == 1) {
                                totalPage = baseResult.getTotalPage();
                            }
                            return baseResult.getData();
                        }
                    });
            action(ob, pullToRefresh, skipPage);
        }
    }

    @Override
    public int getPlayBackEngine() {
        return dataManager.getPlaybackEngine();
    }

    @Override
    public boolean isOpenSkipPage() {
        return dataManager.isOpenSkipPage();
    }

    private void action(Observable<List<V9PornItem>> observable, final boolean pullToRefresh, final int skipPage) {
        observable.retryWhen(new RetryWhenProcess(RetryWhenProcess.PROCESS_TIME))
                .compose(RxSchedulersHelper.<List<V9PornItem>>ioMainThread())
                .compose(provider.<List<V9PornItem>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<V9PornItem>>() {
                    @Override
                    public void onBegin(Disposable d) {
                        //首次加载显示加载页
                        ifViewAttached(new ViewAction<VideoListView>() {
                            @Override
                            public void run(@NonNull VideoListView view) {
                                if (page == 1 && !pullToRefresh && skipPage != 1) {
                                    view.showLoading(pullToRefresh);
                                }
                                if (skipPage > 0) {
                                    view.showSkipPageLoading();
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<V9PornItem> v9PornItems) {
                        ifViewAttached(new ViewAction<VideoListView>() {
                            @Override
                            public void run(@NonNull VideoListView view) {
                                if (page == 1 || skipPage > 0) {
                                    view.setData(v9PornItems);
                                    view.showContent();
                                    if (page == 1) {
                                        view.setPageData(getPageList());
                                    }
                                    view.updateCurrentPage(page);
                                } else {
                                    view.loadMoreDataComplete();
                                    view.setMoreData(v9PornItems);
                                    view.updateCurrentPage(page);
                                }
                                if (skipPage > 0) {
                                    view.hideSkipPageLoading();
                                }
                                //已经最后一页了
                                if (page >= totalPage) {
                                    view.noMoreData();
                                } else {
                                    page++;
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        //首次加载失败，显示重试页
                        ifViewAttached(new ViewAction<VideoListView>() {
                            @Override
                            public void run(@NonNull VideoListView view) {
                                if (page == 1) {
                                    view.showError(msg);
                                } else {
                                    view.loadMoreFailed();
                                }
                                if (skipPage > 0) {
                                    view.hideSkipPageLoading();
                                }
                            }
                        });
                    }
                });
    }

    public List<Integer> getPageList() {
        List<Integer> pageList = new ArrayList<>();
        for (int i = 1; i <= totalPage; i++) {
            pageList.add(i);
        }
        return pageList;
    }

    public int getPage() {
        return page - 1;
    }
}
