package com.u9porn.ui.setting;

import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

/**
 * @author flymegoc
 * @date 2018/2/6
 */

public interface ISetting {

    void test9PornVideo(String baseUrl, QMUICommonListItemView qmuiCommonListItemView, String key);

    void test9PornForum(String baseUrl, QMUICommonListItemView qmuiCommonListItemView, String key);

    void testPav(String baseUrl, QMUICommonListItemView qmuiCommonListItemView, String key);

    void testAxgle(String baseUrl, QMUICommonListItemView qmuiCommonListItemView, String key);

    boolean isHaveUnFinishDownloadVideo();

    boolean isHaveFinishDownloadVideoFile();

    void moveOldDownloadVideoToNewDir(String newDirPath, QMUICommonListItemView qmuiCommonListItemView);

    boolean isUserLogin();

    void existLogin();

    int getPlaybackEngine();

    void setPlaybackEngine(int playbackEngine);

    void setPorn9VideoAddress(String porn9VideoAddress);

    void setPorn9ForumAddress(String porn9ForumAddress);

    void setPavAddress(String pavAddress);

    void setCustomDownloadVideoDirPath(String newDirPath);

    String getCustomDownloadVideoDirPath();

    boolean isForbiddenAutoReleaseMemory();

    void setForbiddenAutoReleaseMemory(boolean forbiddenAutoReleaseMemory);

    boolean isDownloadVideoNeedWifi();

    void setDownloadVideoNeedWifi(boolean downloadVideoNeedWifi);

    boolean isOpenSkipPage();

    void setOpenSkipPage(boolean openSkipPage);

    String getVideo9PornAddress();

    String getForum9PornAddress();

    String getPavAddress();

    boolean isShowUrlRedirectTipDialog();

    void setShowUrlRedirectTipDialog(boolean showUrlRedirectTipDialog);

    void setAxgleAddress(String address);

    String getAxgleAddress();
}
