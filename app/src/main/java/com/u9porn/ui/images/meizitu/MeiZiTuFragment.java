package com.u9porn.ui.images.meizitu;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.adapter.MeiZiTuAdapter;
import com.u9porn.data.model.MeiZiTu;
import com.u9porn.ui.MvpFragment;
import com.u9porn.ui.images.viewimage.PictureViewerActivity;
import com.u9porn.utils.AppUtils;
import com.u9porn.constants.Keys;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author flymegoc
 */
public class MeiZiTuFragment extends MvpFragment<MeiZiTuView, MeiZiTuPresenter> implements MeiZiTuView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    private MeiZiTuAdapter meiZiTuAdapter;

    @Inject
    protected MeiZiTuPresenter meiZiTuPresenter;

    public MeiZiTuFragment() {
        // Required empty public constructor
        List<MeiZiTu> meiZiTuList = new ArrayList<>();
        meiZiTuAdapter = new MeiZiTuAdapter(R.layout.item_mei_zi_tu, meiZiTuList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        meiZiTuAdapter.setWidth(QMUIDisplayHelper.getScreenWidth(context) / 2);
    }

    public static MeiZiTuFragment getInstance() {
        return new MeiZiTuFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_mei_zi_tu, container, false);
    }

    @NonNull
    @Override
    public MeiZiTuPresenter createPresenter() {
        return meiZiTuPresenter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        swipeLayout.setOnRefreshListener(this);
        AppUtils.setColorSchemeColors(context, swipeLayout);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(meiZiTuAdapter);
        meiZiTuAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData(false, false);
            }
        });
        meiZiTuAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MeiZiTu meiZiTu = (MeiZiTu) adapter.getItem(position);
                if (meiZiTu == null) {
                    return;
                }
                Intent intent = new Intent(context, PictureViewerActivity.class);
                intent.putExtra(Keys.KEY_INTENT_MEI_ZI_TU_CONTENT_ID, meiZiTu.getId());
                startActivityWithAnimation(intent);
            }
        });
    }

    @Override
    protected void onLazyLoadOnce() {
        loadData(true, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        swipeLayout.setRefreshing(pullToRefresh);
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
        swipeLayout.setRefreshing(false);
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void loadMoreFailed() {
        meiZiTuAdapter.loadMoreFail();
    }

    @Override
    public void noMoreData() {
        meiZiTuAdapter.loadMoreEnd(true);
    }

    @Override
    public void setMoreData(List<MeiZiTu> meiZiTuList) {
        meiZiTuAdapter.loadMoreComplete();
        meiZiTuAdapter.addData(meiZiTuList);
    }

    @Override
    public void loadData(boolean pullToRefresh, boolean cleanCache) {
        presenter.listMeiZi(category.getCategoryValue(), pullToRefresh);
    }

    @Override
    public void setData(List<MeiZiTu> meiZiTuList) {
        meiZiTuAdapter.setNewData(meiZiTuList);
    }

    @Override
    public void onRefresh() {
        loadData(true, true);
    }
}
