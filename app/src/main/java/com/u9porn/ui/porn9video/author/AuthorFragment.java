package com.u9porn.ui.porn9video.author;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orhanobut.logger.Logger;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.adapter.V91PornAdapter;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.ui.MvpFragment;
import com.u9porn.ui.porn9video.play.BasePlayVideo;
import com.u9porn.utils.AppUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * 作者视频
 *
 * @author megoc
 */
public class AuthorFragment extends MvpFragment<AuthorView, AuthorPresenter> implements AuthorView {

    private static final String TAG = AuthorFragment.class.getSimpleName();
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    Unbinder unbinder;
    private V9PornItem v9PornItem;

    private V91PornAdapter mV91PornAdapter;

    @Inject
    protected AuthorPresenter authorPresenter;

    @Inject
    public AuthorFragment() {
        // Required empty public constructor
        Logger.t(TAG).d("AuthorFragment初始化了.....");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mV91PornAdapter = new V91PornAdapter(R.layout.item_v_9porn);
        mV91PornAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                V9PornItem v9PornItems = (V9PornItem) adapter.getData().get(position);
                BasePlayVideo basePlayVideo = (BasePlayVideo) getActivity();
                if (basePlayVideo != null) {
                    basePlayVideo.setV9PornItems(v9PornItems);
                    basePlayVideo.initData();
                }
            }
        });
        mV91PornAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.authorVideos(v9PornItem.getVideoResult().getOwnerId(), false);

            }
        }, recyclerView);

    }

    public void setV9PornItem(V9PornItem v9PornItem) {
        this.v9PornItem = v9PornItem;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_author, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        init();
    }

    private void init() {
        AppUtils.setColorSchemeColors(getContext(), swipeLayout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.authorVideos(v9PornItem.getVideoResult().getOwnerId(), true);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mV91PornAdapter);
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        loadAuthorVideos();
    }

    public void loadAuthorVideos() {
        presenter.authorVideos(v9PornItem.getVideoResult().getOwnerId(), false);
    }

    @Override
    public String getTitle() {
        return "作者";
    }

    @NonNull
    @Override
    public AuthorPresenter createPresenter() {
        return authorPresenter;
    }

    @Override
    public void loadMoreDataComplete() {
        mV91PornAdapter.loadMoreComplete();
    }

    @Override
    public void loadMoreFailed() {
        mV91PornAdapter.loadMoreFail();
    }

    @Override
    public void noMoreData() {
        mV91PornAdapter.loadMoreEnd(true);
    }

    @Override
    public void setMoreData(List<V9PornItem> v9PornItemList) {
        mV91PornAdapter.addData(v9PornItemList);
    }

    @Override
    public void setData(List<V9PornItem> data) {
        mV91PornAdapter.setNewData(data);
        recyclerView.smoothScrollToPosition(0);
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        swipeLayout.setRefreshing(true);
    }

    @Override
    public void showContent() {
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    public void showError(String message) {
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
