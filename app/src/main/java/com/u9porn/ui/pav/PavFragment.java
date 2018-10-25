package com.u9porn.ui.pav;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.adapter.PaAdapter;
import com.u9porn.data.model.pxgav.PavModel;
import com.u9porn.ui.MvpFragment;
import com.u9porn.ui.pav.playpav.PlayPavActivity;
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
public class PavFragment extends MvpFragment<PavView, PavPresenter> implements PavView, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    Unbinder unbinder;
    private PaAdapter piaAvAdapter;

    @Inject
    protected PavPresenter pigAvPresenter;

    public PavFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        piaAvAdapter = new PaAdapter(R.layout.item_pav);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_pav, container, false);
    }

    @NonNull
    @Override
    public PavPresenter createPresenter() {

        return pigAvPresenter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        swipeLayout.setOnRefreshListener(this);
        AppUtils.setColorSchemeColors(context, swipeLayout);
        piaAvAdapter.setWidth(QMUIDisplayHelper.getScreenWidth(context));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(piaAvAdapter);
        piaAvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PavModel pavModel = (PavModel) adapter.getItem(position);
                if (pavModel == null) {
                    return;
                }
                Intent intent = new Intent(context, PlayPavActivity.class);
                intent.putExtra(Keys.KEY_INTENT_PAV_ITEM, pavModel);
                startActivityWithAnimation(intent);
            }
        });
        piaAvAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.moreVideoList(category.getCategoryValue(), false);
            }
        });
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        presenter.videoList(category.getCategoryValue(), false);
    }

    public static PavFragment getInstance() {
        return new PavFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setData(List<PavModel> pavModelList) {
        piaAvAdapter.setNewData(pavModelList);
    }

    @Override
    public void loadMoreFailed() {
        piaAvAdapter.loadMoreFail();
    }

    @Override
    public void noMoreData() {
        piaAvAdapter.loadMoreEnd(true);
    }

    @Override
    public void setMoreData(List<PavModel> pavModelList) {
        piaAvAdapter.loadMoreComplete();
        piaAvAdapter.addData(pavModelList);
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
    public void onRefresh() {
        presenter.videoList(category.getCategoryValue(), true);
    }
}
