package com.u9porn.ui.porn9video.comment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orhanobut.logger.Logger;
import com.sdsmdg.tastytoast.TastyToast;
import com.u9porn.R;
import com.u9porn.adapter.VideoCommentAdapter;
import com.u9porn.constants.Keys;
import com.u9porn.constants.KeysActivityRequestResultCode;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.data.model.VideoComment;
import com.u9porn.ui.MvpFragment;
import com.u9porn.ui.porn9video.user.UserLoginActivity;
import com.u9porn.utils.AppUtils;
import com.u9porn.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * 视频评论
 *
 * @author megoc
 */
public class CommentFragment extends MvpFragment<CommentView, CommentPresenter> implements CommentView, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = CommentFragment.class.getSimpleName();
    @BindView(R.id.recyclerView_video_comment)
    RecyclerView recyclerViewVideoComment;
    @BindView(R.id.comment_swipe_refreshLayout)
    SwipeRefreshLayout commentSwipeRefreshLayout;
    @BindView(R.id.et_comment_video)
    AppCompatEditText etCommentVideo;
    @BindView(R.id.iv_send_video_comment)
    ImageView ivSendVideoComment;
    Unbinder unbinder;

    private VideoCommentAdapter videoCommentAdapter;

    private boolean isComment = true;
    private VideoComment videoComment;

    private AlertDialog commentVideoDialog;

    private V9PornItem v9PornItem;

    @Inject
    protected CommentPresenter commentPresenter;
    /**
     * 延迟加载评论
     */
    private boolean delayLoadComment;
    private ArrayList<VideoComment> videoCommentList;

    private DividerItemDecoration dividerItemDecoration;

    @Inject
    public CommentFragment() {
        // Required empty public constructor
        Logger.t(TAG).d("CommentFragment初始化了.....");
        videoCommentList = new ArrayList<>();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dividerItemDecoration = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
        videoCommentAdapter = new VideoCommentAdapter(getContext(), R.layout.item_video_comment, videoCommentList);
        videoCommentAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                //加载评论
                if (v9PornItem.getVideoResultId() == 0) {
                    videoCommentAdapter.loadMoreFail();
                    return;
                }
                presenter.loadVideoComment(v9PornItem.getVideoResult().getVideoId(), v9PornItem.getViewKey(), false);
            }
        }, recyclerViewVideoComment);
        videoCommentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                isComment = false;
                videoCommentAdapter.setClickPosition(position);
                videoCommentAdapter.notifyDataSetChanged();
                videoComment = (VideoComment) adapter.getData().get(position);
                etCommentVideo.setHint("回复：" + videoComment.getuName());
            }
        });
        commentVideoDialog = DialogUtils.initLoadingDialog(getContext(), "提交评论中,请稍后...");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        initVideoComments();
        initListener();
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        if (delayLoadComment && presenter != null) {
            delayLoadComment = false;
            presenter.loadVideoComment(v9PornItem.getVideoResult().getVideoId(), v9PornItem.getViewKey(), true);
        }
    }

    @Override
    public String getTitle() {
        return "评论";
    }


    private void cleanVideoCommentInput() {
        etCommentVideo.setText("");
    }

    private void initVideoComments() {
        recyclerViewVideoComment.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewVideoComment.addItemDecoration(dividerItemDecoration);
        recyclerViewVideoComment.setAdapter(videoCommentAdapter);
    }

    private void initListener() {
        commentSwipeRefreshLayout.setEnabled(false);
        AppUtils.setColorSchemeColors(getContext(), commentSwipeRefreshLayout);
        commentSwipeRefreshLayout.setOnRefreshListener(this);
        ivSendVideoComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = etCommentVideo.getText().toString().trim();
                commentOrReplyVideo(comment);
            }
        });
    }

    public void setV9PornItem(V9PornItem v9PornItem) {
        this.v9PornItem = v9PornItem;
    }

    /**
     * 评论视频或者回复评论
     *
     * @param comment 留言内容
     */
    private synchronized void commentOrReplyVideo(String comment) {
        Logger.t(TAG).d("评论视频或者回复评论....");
        if (TextUtils.isEmpty(comment)) {
            showMessage("请填写评论", TastyToast.INFO);
            return;
        }

        if (!presenter.isUserLogin()) {
            showMessage("请先登录帐号", TastyToast.INFO);
            goToLogin(KeysActivityRequestResultCode.LOGIN_ACTION_FOR_GET_UID);
            return;
        }
        if (v9PornItem.getVideoResultId() == 0) {
            showMessage("视频地址还未解析成功，无法评论", TastyToast.INFO);
            return;
        }
        String vid = v9PornItem.getVideoResult().getVideoId();
        String uid = String.valueOf(presenter.getLoginUserId());
        if (isComment) {
            commentVideoDialog.show();
            presenter.commentVideo(comment, uid, vid, v9PornItem.getViewKey());
        } else {
            if (videoComment == null) {
                showMessage("请先选择需要回复的评论！", TastyToast.INFO);
                return;
            }
            commentVideoDialog.show();
            String username = videoComment.getuName();
            String commentId = videoComment.getReplyId();
            presenter.replyComment(comment, username, vid, commentId, v9PornItem.getViewKey());
        }
    }

    /**
     * 去登录
     *
     * @param actionKey 登录之后的动作key
     */
    private void goToLogin(int actionKey) {
        Intent intent = new Intent(getContext(), UserLoginActivity.class);
        intent.putExtra(Keys.KEY_INTENT_LOGIN_FOR_ACTION, actionKey);
        startActivityForResultWithAnimation(intent, 0);
    }

    @Override
    public void setVideoCommentData(List<VideoComment> videoCommentList, boolean pullToRefresh) {
        if (pullToRefresh) {
            recyclerViewVideoComment.smoothScrollToPosition(0);
        }
        videoCommentAdapter.setNewData(videoCommentList);
        commentSwipeRefreshLayout.setEnabled(true);
    }

    @Override
    public void setMoreVideoCommentData(List<VideoComment> videoCommentList) {
        videoCommentAdapter.loadMoreComplete();
        videoCommentAdapter.addData(videoCommentList);
    }

    @Override
    public void noMoreVideoCommentData(String message) {
        videoCommentAdapter.loadMoreEnd(true);
        //showMessage(message, TastyToast.INFO);
    }

    @Override
    public void loadMoreVideoCommentError(String message) {
        videoCommentAdapter.loadMoreFail();
    }

    @Override
    public void loadVideoCommentError(String message) {
        showMessage("加载评论失败了，点击重试", TastyToast.ERROR);
    }

    @Override
    public void commentVideoSuccess(String message) {
        cleanVideoCommentInput();
        reFreshData(v9PornItem);
        showMessage(message, TastyToast.SUCCESS);
    }

    @Override
    public void commentVideoError(String message) {
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void replyVideoCommentSuccess(String message) {
        cleanVideoCommentInput();
        isComment = true;
        etCommentVideo.setHint(R.string.comment_video_hint_tip);
        videoCommentAdapter.setClickPosition(-1);
        reFreshData(v9PornItem);
        showMessage(message, TastyToast.SUCCESS);
    }

    private void reFreshData(V9PornItem v9PornItem) {
        if (v9PornItem.getVideoResultId() == 0) {
            return;
        }
        //刷新
        commentSwipeRefreshLayout.setRefreshing(true);
        String videoId = v9PornItem.getVideoResult().getVideoId();
        presenter.loadVideoComment(videoId, v9PornItem.getViewKey(), true);
    }

    @Override
    public void replyVideoCommentError(String message) {
        showMessage(message, TastyToast.ERROR);
        dismissDialog();
    }

    @NonNull
    @Override
    public CommentPresenter createPresenter() {
        Logger.t(TAG).d("createPresenter初始化了.....");
        return commentPresenter;
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        commentSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void showContent() {
        commentSwipeRefreshLayout.setRefreshing(false);
        dismissDialog();
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    public void showError(String message) {
        commentSwipeRefreshLayout.setRefreshing(false);
        dismissDialog();
    }

    private void dismissDialog() {
        if (commentVideoDialog != null && commentVideoDialog.isShowing()) {
            commentVideoDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void loadVideoComment(String videoId, String viewKey, boolean pullToRefresh) {
        if (presenter != null) {
            presenter.loadVideoComment(videoId, viewKey, pullToRefresh);
        } else {
            delayLoadComment = true;
        }
    }

    @Override
    public void onRefresh() {
        if (v9PornItem.getVideoResultId() == 0) {
            commentSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        String videoId = v9PornItem.getVideoResult().getVideoId();
        presenter.loadVideoComment(videoId, v9PornItem.getViewKey(), true);
    }
}
