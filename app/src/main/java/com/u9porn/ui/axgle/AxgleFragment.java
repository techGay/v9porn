package com.u9porn.ui.axgle;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
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
import com.helper.loadviewhelper.help.OnLoadViewListener;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.u9porn.R;
import com.u9porn.adapter.AxgleAdapter;
import com.u9porn.constants.Keys;
import com.u9porn.data.model.axgle.AxgleVideo;
import com.u9porn.ui.MvpFragment;
import com.u9porn.ui.axgle.play.AxglePlayActivity;
import com.u9porn.utils.AppUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author megoc
 */
public class AxgleFragment extends MvpFragment<AxgleView, AxglePresenter> implements SwipeRefreshLayout.OnRefreshListener, AxgleView {

    @Inject
    protected AxglePresenter axglePresenter;
    @BindView(R.id.recyclerView_axgle)
    RecyclerView recyclerViewAxgle;
    @BindView(R.id.contentView)
    SwipeRefreshLayout contentView;
    Unbinder unbinder;

    private AxgleAdapter axgleAdapter;

    private LoadViewHelper helper;

    public AxgleFragment() {
        // Required empty public constructor
    }

    public static AxgleFragment getInstance() {
        return new AxgleFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        axgleAdapter = new AxgleAdapter(R.layout.item_axgle);
        axgleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getContext(), AxglePlayActivity.class);
                AxgleVideo axgleVideo = (AxgleVideo) adapter.getItem(position);
                if (axgleVideo == null) {
                    return;
                }
                intent.putExtra(Keys.KEY_INTENT_AXGLE_VIDEO_ITEM, axgleVideo);
                startActivityWithAnimation(intent);
            }
        });
        axgleAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData(false);
            }
        }, recyclerViewAxgle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_axgle, container, false);
    }

    @NonNull
    @Override
    public AxglePresenter createPresenter() {
        return axglePresenter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        contentView.setOnRefreshListener(this);
        recyclerViewAxgle.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAxgle.setAdapter(axgleAdapter);

        helper = new LoadViewHelper(recyclerViewAxgle);
        helper.setListener(new OnLoadViewListener() {
            @Override
            public void onRetryClick() {
                loadData(false);
            }
        });
        //loadData(false);
        AppUtils.setColorSchemeColors(context, contentView);
    }

    private void loadData(boolean pullToRefresh) {
        presenter.videos(category.getCategoryValue(), pullToRefresh);
    }

    @Override
    protected void onLazyLoadOnce() {
        loadData(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        contentView.setRefreshing(pullToRefresh);
        helper.showLoading();
    }

    @Override
    public void showContent() {
        contentView.setRefreshing(false);
        helper.showContent();
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    public void showError(String message) {
        contentView.setRefreshing(false);
        helper.showError();
    }

    @Override
    public void setData(List<AxgleVideo> axgleVideos) {
        axgleAdapter.setNewData(axgleVideos);
    }

    @Override
    public void loadMoreFailed() {
        axgleAdapter.loadMoreFail();
    }

    @Override
    public void noMoreData() {
        axgleAdapter.loadMoreEnd(true);
    }

    @Override
    public void setMoreData(List<AxgleVideo> axgleVideos) {
        axgleAdapter.addData(axgleVideos);
        axgleAdapter.loadMoreComplete();
    }
}
