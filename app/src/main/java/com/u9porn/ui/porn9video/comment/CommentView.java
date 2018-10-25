package com.u9porn.ui.porn9video.comment;

import com.u9porn.data.model.VideoComment;
import com.u9porn.ui.BaseView;

import java.util.List;

public interface CommentView extends BaseView{

    void setVideoCommentData(List<VideoComment> videoCommentList, boolean pullToRefresh);

    void setMoreVideoCommentData(List<VideoComment> videoCommentList);

    void noMoreVideoCommentData(String message);

    void loadMoreVideoCommentError(String message);

    void loadVideoCommentError(String message);

    void commentVideoSuccess(String message);

    void commentVideoError(String message);

    void replyVideoCommentSuccess(String message);

    void replyVideoCommentError(String message);
}
