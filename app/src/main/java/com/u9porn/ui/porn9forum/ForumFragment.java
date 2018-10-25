package com.u9porn.ui.porn9forum;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.adapter.Forum9PornAdapter;
import com.u9porn.data.model.F9PronItem;
import com.u9porn.data.model.PinnedHeaderEntity;
import com.u9porn.ui.MvpFragment;
import com.u9porn.ui.porn9forum.browse9forum.Browse9PForumActivity;
import com.u9porn.utils.AddressHelper;
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
public class ForumFragment extends MvpFragment<ForumView, ForumPresenter> implements ForumView, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.tv_forum_91_porn_tip)
    TextView tipTextView;
    Unbinder unbinder;
    private Forum9PornAdapter forum91PornAdapter;

    @Inject
    protected ForumPresenter forumPresenter;

    public ForumFragment() {
        // Required empty public constructor
    }

    public static ForumFragment getInstance() {
        return new ForumFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<F9PronItem> f9PronItemList = new ArrayList<>();
        forum91PornAdapter = new Forum9PornAdapter(context, presenter.getForum9PornAddress(), R.layout.item_forum_9_porn, f9PronItemList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_forum, container, false);
    }

    @NonNull
    @Override
    public ForumPresenter createPresenter() {
        return forumPresenter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        swipeLayout.setOnRefreshListener(this);
        AppUtils.setColorSchemeColors(context, swipeLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(forum91PornAdapter);
        forum91PornAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData(false);
            }
        });
        if ("17".equals(category.getCategoryValue()) || "4".equals(category.getCategoryValue())) {
            tipTextView.setVisibility(View.VISIBLE);
            swipeLayout.setEnabled(false);
        }
        forum91PornAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                F9PronItem f9PronItem = (F9PronItem) adapter.getItem(position);
                Intent intent = new Intent(context, Browse9PForumActivity.class);
                intent.putExtra(Keys.KEY_INTENT_BROWSE_FORUM_9PORN_ITEM, f9PronItem);
                startActivityWithAnimation(intent);
            }
        });
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        loadData(true);
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        if ("index".equals(category.getCategoryValue())) {
            presenter.loadForumIndexListData(true);
        } else if ("17".equals(category.getCategoryValue()) || "4".equals(category.getCategoryValue())) {
            swipeLayout.setEnabled(false);
        } else {
            presenter.loadForumListData(pullToRefresh, category.getCategoryValue());
        }
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
    public void setForumListData(List<F9PronItem> f9PronItemList) {
        forum91PornAdapter.setNewData(f9PronItemList);
    }

    @Override
    public void setForumIndexListData(List<PinnedHeaderEntity<F9PronItem>> pinnedHeaderEntityList) {

    }

    @Override
    public void loadMoreFailed() {
        forum91PornAdapter.loadMoreFail();
    }

    @Override
    public void noMoreData() {
        forum91PornAdapter.loadMoreEnd(true);
    }

    @Override
    public void setMoreData(List<F9PronItem> f9PronItemList) {
        forum91PornAdapter.loadMoreComplete();
        forum91PornAdapter.addData(f9PronItemList);
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }
}
