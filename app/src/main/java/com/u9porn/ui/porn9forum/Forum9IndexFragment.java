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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.adapter.BaseHeaderAdapter;
import com.u9porn.adapter.Forum9PornIndexAdapter;
import com.u9porn.data.model.F9PronItem;
import com.u9porn.data.model.PinnedHeaderEntity;
import com.u9porn.ui.MvpFragment;
import com.u9porn.ui.porn9forum.browse9forum.Browse9PForumActivity;
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
public class Forum9IndexFragment extends MvpFragment<ForumView, ForumPresenter> implements ForumView, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    Unbinder unbinder;
    private Forum9PornIndexAdapter forum9PornIndexAdapter;

    @Inject
    protected ForumPresenter forumPresenter;

    public Forum9IndexFragment() {
        // Required empty public constructor
        List<PinnedHeaderEntity<F9PronItem>> forum91PornItemSectionList = new ArrayList<>();
        forum9PornIndexAdapter = new Forum9PornIndexAdapter(forum91PornItemSectionList);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_forum9_index, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        AppUtils.setColorSchemeColors(context, swipeLayout);
        swipeLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new PinnedHeaderItemDecoration.Builder(BaseHeaderAdapter.TYPE_HEADER).setDividerId(R.drawable.divider).enableDivider(true).create());
        recyclerView.setAdapter(forum9PornIndexAdapter);
        forum9PornIndexAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                PinnedHeaderEntity<F9PronItem> forum91PronItemPinnedHeaderEntity = (PinnedHeaderEntity<F9PronItem>) adapter.getItem(position);
                if (forum91PronItemPinnedHeaderEntity == null || forum91PronItemPinnedHeaderEntity.getData() == null) {
                    return;
                }
                Intent intent = new Intent(context, Browse9PForumActivity.class);
                intent.putExtra(Keys.KEY_INTENT_BROWSE_FORUM_9PORN_ITEM, forum91PronItemPinnedHeaderEntity.getData());
                startActivityWithAnimation(intent);
            }
        });
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        loadData(true);
    }

    public static Forum9IndexFragment getInstance() {
        return new Forum9IndexFragment();
    }

    @Override
    public void onRefresh() {
        loadData(true);
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

    }

    @Override
    public void setForumIndexListData(List<PinnedHeaderEntity<F9PronItem>> pinnedHeaderEntityList) {
        forum9PornIndexAdapter.setNewData(pinnedHeaderEntityList);
    }

    @Override
    public void loadMoreFailed() {

    }

    @Override
    public void noMoreData() {

    }

    @Override
    public void setMoreData(List<F9PronItem> f9PronItemList) {

    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadForumIndexListData(pullToRefresh);
    }

    @NonNull
    @Override
    public ForumPresenter createPresenter() {
        return forumPresenter;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
