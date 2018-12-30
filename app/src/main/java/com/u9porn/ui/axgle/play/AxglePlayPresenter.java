package com.u9porn.ui.axgle.play;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.annotation.NonNull;

import com.awesapp.isafe.svs.parsers.PSVS21;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.data.DataManager;
import com.u9porn.di.ApplicationContext;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author megoc
 */
public class AxglePlayPresenter extends MvpBasePresenter<AxglePlayView> implements IAxglePlay {
    private DataManager dataManager;
    private LifecycleProvider<Lifecycle.Event> provider;
    private Context context;

    @Inject
    public AxglePlayPresenter(DataManager dataManager, LifecycleProvider<Lifecycle.Event> provider, @ApplicationContext Context context) {
        this.dataManager = dataManager;
        this.provider = provider;
        this.context = context;
    }

    @Override
    public void getPlayVideoUrl(String vid) {
        String ts = String.valueOf(System.currentTimeMillis() / 1000);
        String axgleAddress = dataManager.getAxgleAddress();
        String url = String.format(
                axgleAddress.replace("api.", "") + "mp4.php?vid=%s&ts=%s&hash=%s&m3u8"
                , vid
                , ts
                , PSVS21.computeHash(new PSVS21.StubContext(context), vid, ts));
        ifViewAttached(new ViewAction<AxglePlayView>() {
            @Override
            public void run(@NonNull AxglePlayView view) {
                view.showLoading();
            }
        });
        dataManager.getPlayVideoUrl(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                final String url = response.raw().request().url().toString();
                ifViewAttached(new ViewAction<AxglePlayView>() {
                    @Override
                    public void run(@NonNull AxglePlayView view) {
                        view.getVideoUrlSuccess(url);
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ifViewAttached(new ViewAction<AxglePlayView>() {
                    @Override
                    public void run(@NonNull AxglePlayView view) {
                        view.getVideoUrlError();
                    }
                });
            }
        });
    }
}
