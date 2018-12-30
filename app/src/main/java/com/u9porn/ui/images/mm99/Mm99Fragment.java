package com.u9porn.ui.images.mm99;


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
import com.u9porn.adapter.Mm99Adapter;
import com.u9porn.data.model.Mm99;
import com.u9porn.ui.MvpFragment;
import com.u9porn.ui.images.viewimage.PictureViewerActivity;
import com.u9porn.utils.AppUtils;
import com.u9porn.constants.Keys;

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
public class Mm99Fragment extends MvpFragment<Mm99View, Mm99Presenter> implements Mm99View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    Unbinder unbinder;
    private Mm99Adapter mm99Adapter;

    @Inject
    protected Mm99Presenter mm99Presenter;

    public Mm99Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mm99Adapter = new Mm99Adapter(R.layout.item_99_mm);
    }

    public static Mm99Fragment getInstance() {
        return new Mm99Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_mm99, container, false);
    }

    @NonNull
    @Override
    public Mm99Presenter createPresenter() {
        return mm99Presenter;
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        loadData(true, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        swipeLayout.setOnRefreshListener(this);
        AppUtils.setColorSchemeColors(context, swipeLayout);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(mm99Adapter);
        mm99Adapter.setWidth(QMUIDisplayHelper.getScreenWidth(context) / 2);
        mm99Adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData(false, false);
            }
        });
        mm99Adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Mm99 mm99 = (Mm99) adapter.getItem(position);
                if (mm99 == null) {
                    return;
                }
                Intent intent = new Intent(context, PictureViewerActivity.class);
                intent.putExtra(Keys.KEY_INTENT_99_MM_ITEM, mm99);
                startActivityWithAnimation(intent);
            }
        });
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
        mm99Adapter.loadMoreFail();
    }

    @Override
    public void noMoreData() {
        mm99Adapter.loadMoreEnd(true);
    }

    @Override
    public void setMoreData(List<Mm99> mm99List) {
        mm99Adapter.loadMoreComplete();
        mm99Adapter.addData(mm99List);
    }

    @Override
    public void loadData(boolean pullToRefresh, boolean cleanCache) {
        presenter.loadData(category.getCategoryValue(), pullToRefresh, cleanCache);
    }

    @Override
    public void setData(List<Mm99> data) {
        mm99Adapter.setNewData(data);
    }

    @Override
    public void onRefresh() {
        loadData(true, true);
    }
}
