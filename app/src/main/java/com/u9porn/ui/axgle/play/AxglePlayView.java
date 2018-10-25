package com.u9porn.ui.axgle.play;

import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface AxglePlayView extends MvpView {
    void showLoading();

    void getVideoUrlSuccess(String videoUrl);

    void getVideoUrlError();
}
