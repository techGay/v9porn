package com.u9porn.ui.porn9video.author;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.helper.loadviewhelper.help.OnLoadViewListener;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.adapter.V91PornAdapter;
import com.u9porn.constants.KeysActivityRequestResultCode;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.ui.MvpActivity;
import com.u9porn.utils.LoadHelperUtils;
import com.u9porn.constants.Keys;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public class AuthorActivity extends MvpActivity<AuthorView, AuthorPresenter> implements AuthorView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    private V91PornAdapter mV91PornAdapter;
    private LoadViewHelper helper;
    private String uid;

    @Inject
    protected AuthorPresenter authorPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        ButterKnife.bind(this);
        initToolBar(toolbar);
        uid = getIntent().getStringExtra(Keys.KEY_INTENT_UID);
        if (TextUtils.isEmpty(uid)) {
            showMessage("用户信息错误，无法获取数据", TastyToast.ERROR);
            return;
        }
        init();
    }

    private void init() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.authorVideos(uid, true);
            }
        });
        swipeLayout.setEnabled(false);
        mV91PornAdapter = new V91PornAdapter(R.layout.item_v_9porn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mV91PornAdapter);

        mV91PornAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                V9PornItem v9PornItems = (V9PornItem) adapter.getData().get(position);
                Intent intent = new Intent();
                intent.putExtra(Keys.KEY_INTENT_V9PORN_ITEM, v9PornItems);
                //setResult(KeysActivityRequestResultCode.AUTHOR_ACTIVITY_RESULT_CODE, intent);
                onBackPressed();
            }
        });
        mV91PornAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

                presenter.authorVideos(uid, false);
            }
        }, recyclerView);

        helper = new LoadViewHelper(recyclerView);
        helper.setListener(new OnLoadViewListener() {
            @Override
            public void onRetryClick() {
                swipeLayout.setEnabled(false);
                presenter.authorVideos(uid, true);
            }
        });
        presenter.authorVideos(uid, false);
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
        swipeLayout.setEnabled(true);
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        helper.showLoading();
        LoadHelperUtils.setLoadingText(helper.getLoadIng(), R.id.tv_loading_text, "加载中，请稍候...");
    }

    @Override
    public void showContent() {
        helper.showContent();
        if (mV91PornAdapter.getData().size() == 0) {
            helper.showEmpty();
            LoadHelperUtils.setEmptyText(helper.getLoadEmpty(), R.id.tv_empty_info, "暂无数据");
        }
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    public void showError(String message) {
        showMessage(message, TastyToast.ERROR);
        helper.showError();
        LoadHelperUtils.setErrorText(helper.getLoadError(), R.id.tv_error_text, "加载数据失败了，点击重试");
    }
}
