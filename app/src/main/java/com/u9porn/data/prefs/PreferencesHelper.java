package com.u9porn.data.prefs;

/**
 * @author flymegoc
 * @date 2018/2/12
 */

public interface PreferencesHelper {
    void setPorn9VideoAddress(String address);

    String getPorn9VideoAddress();

    void setPorn9ForumAddress(String address);

    String getPorn9ForumAddress();

    void setPavAddress(String address);

    String getPavAddress();

    void setPorn9VideoLoginUserName(String userName);

    String getPorn9VideoLoginUserName();

    void setPorn9VideoLoginUserPassWord(String passWord);

    String getPorn9VideoLoginUserPassword();

    void setPorn9VideoUserAutoLogin(boolean autoLogin);

    boolean isPorn9VideoUserAutoLogin();

    void setFavoriteNeedRefresh(boolean needRefresh);

    boolean isFavoriteNeedRefresh();

    void setPlaybackEngine(int playbackEngine);

    int getPlaybackEngine();

    void setFirstInSearchPorn91Video(boolean firstInSearchPorn91Video);

    boolean isFirstInSearchPorn91Video();

    void setDownloadVideoNeedWifi(boolean downloadVideoNeedWifi);

    boolean isDownloadVideoNeedWifi();

    void setOpenHttpProxy(boolean openHttpProxy);

    boolean isOpenHttpProxy();

    void setOpenNightMode(boolean openNightMode);

    boolean isOpenNightMode();

    void setProxyIpAddress(String proxyIpAddress);

    String getProxyIpAddress();

    void setProxyPort(int port);

    int getProxyPort();

    void setNeverAskForWatchDownloadTip(boolean neverAskForWatchDownloadTip);

    boolean isNeverAskForWatchDownloadTip();

    void setIgnoreUpdateVersionCode(int versionCode);

    int getIgnoreUpdateVersionCode();

    void setForbiddenAutoReleaseMemory(boolean autoReleaseMemory);

    boolean isForbiddenAutoReleaseMemory();

    void setNeedShowTipFirstViewForum9Content(boolean contentShowTip);

    boolean isNeedShowTipFirstViewForum9Content();

    void setNoticeVersionCode(int noticeVersionCode);

    int getNoticeVersionCode();

    void setMainFirstTabShow(int firstTabShow);

    int getMainFirstTabShow();

    void setMainSecondTabShow(int secondTabShow);

    int getMainSecondTabShow();

    void setSettingScrollViewScrollPosition(int position);

    int getSettingScrollViewScrollPosition();

    void setOpenSkipPage(boolean openSkipPage);

    boolean isOpenSkipPage();

    void setCustomDownloadVideoDirPath(String customDirPath);

    String getCustomDownloadVideoDirPath();

    boolean isShowUrlRedirectTipDialog();

    void setShowUrlRedirectTipDialog(boolean showUrlRedirectTipDialog);

    void setAxgleAddress(String address);

    String getAxgleAddress();
}
