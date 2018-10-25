package com.u9porn.ui.porn9video.comment;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.data.DataManager;
import com.u9porn.data.model.VideoComment;
import com.u9porn.di.PerActivity;
import com.u9porn.rxjava.CallBackWrapper;
import com.u9porn.rxjava.RetryWhenProcess;
import com.u9porn.rxjava.RxSchedulersHelper;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * @author megoc
 */
public class CommentPresenter extends MvpBasePresenter<CommentView> implements IComment {

    private static final String TAG = CommentPresenter.class.getSimpleName();
    private LifecycleProvider<Lifecycle.Event> provider;

    private int start = 1;
    private DataManager dataManager;

    @Inject
    public CommentPresenter(LifecycleProvider<Lifecycle.Event> provider, DataManager dataManager) {
        this.provider = provider;
        this.dataManager = dataManager;
        Logger.t(TAG).d("CommentPresenter初始化了.....");
    }

    @Override
    public void loadVideoComment(String videoId, String viewKey, final boolean pullToRefresh) {
        if (pullToRefresh) {
            start = 1;
        }
        dataManager.loadPorn9VideoComments(videoId, start, viewKey)
                .retryWhen(new RetryWhenProcess(RetryWhenProcess.PROCESS_TIME))
                .compose(RxSchedulersHelper.<List<VideoComment>>ioMainThread())
                .compose(provider.<List<VideoComment>>bindUntilEvent(Lifecycle.Event.ON_STOP))
                .subscribe(new CallBackWrapper<List<VideoComment>>() {
                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<CommentView>() {
                            @Override
                            public void run(@NonNull CommentView view) {
                                if (start == 1 && pullToRefresh) {
                                    view.showLoading(pullToRefresh);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final List<VideoComment> videoCommentList) {
                        ifViewAttached(new ViewAction<CommentView>() {
                            @Override
                            public void run(@NonNull CommentView view) {
                                if (start == 1) {
                                    view.setVideoCommentData(videoCommentList, pullToRefresh);
                                } else {
                                    view.setMoreVideoCommentData(videoCommentList);
                                }
                                if (videoCommentList.size() == 0 && start == 1) {
                                    view.noMoreVideoCommentData("暂无评论");
                                } else if (videoCommentList.size() == 0 && start > 1) {
                                    view.noMoreVideoCommentData("没有更多评论了");
                                }
                                start++;
                                view.showContent();
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<CommentView>() {
                            @Override
                            public void run(@NonNull CommentView view) {
                                if (start == 1) {
                                    view.loadVideoCommentError(msg);
                                } else {
                                    view.loadMoreVideoCommentError(msg);
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancel(boolean isCancel) {
                        ifViewAttached(new ViewAction<CommentView>() {
                            @Override
                            public void run(@NonNull CommentView view) {
                                Logger.t(TAG).d("------getVideoComments  onCancel----------------------------");
                                if (start == 1) {
                                    view.loadVideoCommentError("取消请求");
                                } else {
                                    view.loadMoreVideoCommentError("取消请求");
                                }
                            }
                        });
                    }
                });
    }

    @Override
    public void commentVideo(String comment, String uid, String vid, String viewKey) {
        String cpaintFunction = "process_comments";
        String responseType = "json";
        String comments = "\"" + comment + "\"";
        Logger.t(TAG).d(comments);
        dataManager.commentPorn9Video(cpaintFunction, comments, uid, vid, viewKey, responseType)
                .retryWhen(new RetryWhenProcess(RetryWhenProcess.PROCESS_TIME))
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .compose(provider.<String>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<String>() {
                    @Override
                    public void onBegin(Disposable d) {

                    }

                    @Override
                    public void onSuccess(final String result) {
                        ifViewAttached(new ViewAction<CommentView>() {
                            @Override
                            public void run(@NonNull CommentView view) {
                                view.showContent();
                                view.commentVideoSuccess(result);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<CommentView>() {
                            @Override
                            public void run(@NonNull CommentView view) {
                                view.showContent();
                                view.commentVideoError(msg);
                            }
                        });
                    }
                });
    }

    @Override
    public void replyComment(String comment, String username, String vid, String commentId, String viewKey) {
        dataManager.replyPorn9VideoComment(comment, username, vid, commentId, viewKey)
                .retryWhen(new RetryWhenProcess(RetryWhenProcess.PROCESS_TIME))
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .compose(provider.<String>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<String>() {
                    @Override
                    public void onBegin(Disposable d) {

                    }

                    @Override
                    public void onSuccess(final String s) {
                        ifViewAttached(new ViewAction<CommentView>() {
                            @Override
                            public void run(@NonNull CommentView view) {
                                view.showContent();
                                if ("OK".equals(s)) {
                                    view.replyVideoCommentSuccess("留言已经提交，审核后通过");
                                } else {
                                    view.replyVideoCommentError("回复评论失败");
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<CommentView>() {
                            @Override
                            public void run(@NonNull CommentView view) {
                                view.showContent();
                                view.showError(msg);
                            }
                        });
                    }
                })
        ;
    }

    @Override
    public boolean isUserLogin() {
        return dataManager.isUserLogin();
    }

    @Override
    public int getLoginUserId() {
        return dataManager.getUser().getUserId();
    }
}
