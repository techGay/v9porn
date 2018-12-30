package com.u9porn.ui.images.huaban;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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
import com.u9porn.adapter.HuaBanAdapter;
import com.u9porn.data.model.HuaBan;
import com.u9porn.di.PerFragment;
import com.u9porn.ui.MvpFragment;
import com.u9porn.ui.images.viewimage.PhotoImageActivity;
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
public class HuaBanFragment extends MvpFragment<HuaBanView, HuaBanPresenter> implements HuaBanView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    Unbinder unbinder;

    private HuaBanAdapter huaBanAdapter;

    @Inject
    protected HuaBanPresenter huaBanPresenter;

    public HuaBanFragment() {
        // Required empty public constructor
    }

    public static HuaBanFragment getInstance() {
        return new HuaBanFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        huaBanAdapter = new HuaBanAdapter(R.layout.item_hua_ban);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hua_ban, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        swipeLayout.setOnRefreshListener(this);
        AppUtils.setColorSchemeColors(context, swipeLayout);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(huaBanAdapter);
        huaBanAdapter.setWidth(QMUIDisplayHelper.getScreenWidth(context) / 2);
        huaBanAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.findPictures(Integer.parseInt(category.getCategoryValue()), false);
            }
        });
        huaBanAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                HuaBan.Picture huaBan= (HuaBan.Picture) adapter.getItem(position);
                if (huaBan != null) {
                    startOptionsActivity(getActivity(), view, position,huaBan.getPictureUrl(huaBan.getPictureKey(),QMUIDisplayHelper.getScreenWidth(context) / 2));
                }
            }
        });
    }

    @NonNull
    @Override
    public HuaBanPresenter createPresenter() {
        return huaBanPresenter;
    }
    private static final String OPTION_IMAGE = "img";

    public void startOptionsActivity(Activity activity, View transitionView, int position, String url) {
        Intent intent = new Intent(activity, PhotoImageActivity.class);
        intent.putExtra("imgurl", url);
        // 这里指定了共享的视图元素
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionView, OPTION_IMAGE);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        presenter.findPictures(Integer.parseInt(category.getCategoryValue()), true);
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
        huaBanAdapter.loadMoreFail();
    }

    @Override
    public void noMoreData() {
        huaBanAdapter.loadMoreEnd(true);
    }

    @Override
    public void setMoreData(List<HuaBan.Picture> pictureList) {
        huaBanAdapter.loadMoreComplete();
        huaBanAdapter.addData(pictureList);
    }

    @Override
    public void setData(List<HuaBan.Picture> data) {
        huaBanAdapter.setNewData(data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        presenter.findPictures(Integer.parseInt(category.getCategoryValue()), true);
    }
}
