package com.u9porn.ui.porn9video.play;

import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.data.model.VideoComment;
import com.u9porn.ui.BaseView;

import java.util.List;

/**
 * @author flymegoc
 * @date 2017/11/15
 * @describe
 */

public interface PlayVideoView extends BaseView {
    void showParsingDialog();

    void parseVideoUrlSuccess(V9PornItem v9PornItem);

    void errorParseVideoUrl(String errorMessage);

    void favoriteSuccess();
}
