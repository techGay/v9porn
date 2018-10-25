package com.u9porn.ui.main;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.u9porn.data.DataManager;
import com.u9porn.data.model.Notice;
import com.u9porn.data.model.UpdateVersion;
import com.u9porn.ui.notice.NoticePresenter;
import com.u9porn.ui.update.UpdatePresenter;

import javax.inject.Inject;

/**
 * @author flymegoc
 * @date 2017/12/23
 */
public class MainPresenter extends MvpBasePresenter<MainView> implements IMain {

    private UpdatePresenter updatePresenter;
    private NoticePresenter noticePresenter;
    private DataManager dataManager;

    @Inject
    public MainPresenter(DataManager dataManager, UpdatePresenter updatePresenter, NoticePresenter noticePresenter) {
        this.dataManager = dataManager;
        this.updatePresenter = updatePresenter;
        this.noticePresenter = noticePresenter;
    }

    @Override
    public void checkUpdate(int versionCode) {
        updatePresenter.checkUpdate(versionCode, new UpdatePresenter.UpdateListener() {
            @Override
            public void needUpdate(final UpdateVersion updateVersion) {
                ifViewAttached(new ViewAction<MainView>() {
                    @Override
                    public void run(@NonNull MainView view) {
                        view.needUpdate(updateVersion);
                    }
                });
            }

            @Override
            public void noNeedUpdate() {
                ifViewAttached(new ViewAction<MainView>() {
                    @Override
                    public void run(@NonNull MainView view) {
                        view.noNeedUpdate();
                    }
                });
            }

            @Override
            public void checkUpdateError(final String message) {
                ifViewAttached(new ViewAction<MainView>() {
                    @Override
                    public void run(@NonNull MainView view) {
                        view.checkUpdateError(message);
                    }
                });
            }
        });
    }

    @Override
    public void checkNewNotice() {
        noticePresenter.checkNewNotice(new NoticePresenter.CheckNewNoticeListener() {
            @Override
            public void haveNewNotice(final Notice notice) {
                ifViewAttached(new ViewAction<MainView>() {
                    @Override
                    public void run(@NonNull MainView view) {
                        view.haveNewNotice(notice);
                    }
                });
            }

            @Override
            public void noNewNotice() {
                ifViewAttached(new ViewAction<MainView>() {
                    @Override
                    public void run(@NonNull MainView view) {
                        view.noNewNotice();
                    }
                });
            }

            @Override
            public void checkNewNoticeError(final String message) {
                ifViewAttached(new ViewAction<MainView>() {
                    @Override
                    public void run(@NonNull MainView view) {
                        view.checkNewNoticeError(message);
                    }
                });
            }
        });
    }

    @Override
    public void saveNoticeVersionCode(int versionCode) {
        dataManager.setNoticeVersionCode(versionCode);
    }

    @Override
    public int getIgnoreUpdateVersionCode() {
        return dataManager.getIgnoreUpdateVersionCode();
    }

    @Override
    public void setIgnoreUpdateVersionCode(int versionCode) {
        dataManager.setIgnoreUpdateVersionCode(versionCode);
    }

    @Override
    public void setMainSecondTabShow(int tabId) {
        dataManager.setMainSecondTabShow(tabId);
    }

    @Override
    public int getMainSecondTabShow() {
        return dataManager.getMainSecondTabShow();
    }

    @Override
    public void setMainFirstTabShow(int tabId) {
        dataManager.setMainFirstTabShow(tabId);
    }

    @Override
    public int getMainFirstTabShow() {
        return dataManager.getMainFirstTabShow();
    }

    @Override
    public boolean haveNotSetF9pornAddress() {
        return TextUtils.isEmpty(dataManager.getPorn9ForumAddress());
    }

    @Override
    public boolean haveNotSetV9pronAddress() {
        return TextUtils.isEmpty(dataManager.getPorn9VideoAddress());
    }

    @Override
    public boolean haveNotSetPavAddress() {
        return TextUtils.isEmpty(dataManager.getPavAddress());
    }

    @Override
    public boolean haveNotSetAxgleAddress() {
        return false;
    }

    @Override
    public boolean isUserLogin() {
        return dataManager.isUserLogin();
    }

    @Override
    public void setPorn9VideoAddress(String porn9VideoAddress) {
        dataManager.setPorn9VideoAddress(porn9VideoAddress);
    }

    @Override
    public void setPorn9ForumAddress(String porn9ForumAddress) {
        dataManager.setPorn9ForumAddress(porn9ForumAddress);
    }
}
