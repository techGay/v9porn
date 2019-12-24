package com.u9porn.ui.google;

import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface GoogleRecaptchaVerifyView extends MvpView {

    void startCheckNeedVerify();

    void noNeedVerifyRecaptcha();

    void needVerifyRecaptcha(String html);

    void loadPageDataFailure();

    void startVerifyRecaptcha();

    void verifyRecaptchaSuccess();

    void verifyRecaptchaFailure();
}
