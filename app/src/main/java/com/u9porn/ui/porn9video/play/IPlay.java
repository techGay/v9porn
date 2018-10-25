package com.u9porn.ui.porn9video.play;

import com.u9porn.data.db.entity.V9PornItem;

/**
 * @author flymegoc
 * @date 2017/11/27
 * @describe
 */

public interface IPlay extends IBasePlay {
    void loadVideoUrl(V9PornItem v9PornItem);

    String getVideoCacheProxyUrl(String originalVideoUrl);

    boolean isUserLogin();

    int getLoginUserId();

    void updateV9PornItemForHistory(V9PornItem v9PornItem);

    V9PornItem findV9PornItemByViewKey(String viewKey);

    boolean isNeverAskForWatchDownloadTip();

    void setNeverAskForWatchDownloadTip(boolean neverAskForWatchDownloadTip);

    void setFavoriteNeedRefresh(boolean favoriteNeedRefresh);
}
