package com.u9porn.ui.mine;


import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.u9porn.data.DataManager;
import com.u9porn.data.model.User;

import javax.inject.Inject;

/**
 * @author megoc
 */
public class MinePresenter extends MvpBasePresenter<MineView> implements IMine {

    private DataManager dataManager;

    @Inject
    public MinePresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public boolean isUserLogin() {
        return dataManager.isUserLogin();
    }

    @Override
    public User getLoginUser() {
        return dataManager.getUser();
    }

    @Override
    public boolean isOpenHttpProxy() {
        return dataManager.isOpenHttpProxy();
    }

    @Override
    public void setOpenHttpProxy(boolean openHttpProxy) {
        dataManager.setOpenHttpProxy(openHttpProxy);
    }

    @Override
    public String getProxyIpAddress() {
        return dataManager.getProxyIpAddress();
    }

    @Override
    public int getProxyPort() {
        return dataManager.getProxyPort();
    }

    @Override
    public boolean isOpenNightMode() {
        return dataManager.isOpenNightMode();
    }

    @Override
    public void setOpenNightMode(boolean openNightMode) {
        dataManager.setOpenNightMode(openNightMode);
    }

    @Override
    public void setSettingScrollViewScrollPosition(int settingScrollViewScrollPosition) {
        dataManager.setSettingScrollViewScrollPosition(settingScrollViewScrollPosition);
    }

    @Override
    public int getSettingScrollViewScrollPosition() {
        return dataManager.getSettingScrollViewScrollPosition();
    }
}
