package com.u9porn.ui.google;

import android.arch.lifecycle.Lifecycle;
import android.text.TextUtils;
import android.util.Log;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u9porn.data.DataManager;
import com.u9porn.rxjava.CallBackWrapper;
import com.u9porn.rxjava.RetryWhenProcess;
import com.u9porn.rxjava.RxSchedulersHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class GoogleRecaptchaVerifyPresenter extends MvpBasePresenter<GoogleRecaptchaVerifyView> implements IGoogleRecaptchaVerify {

    private final static String TAG = GoogleRecaptchaVerifyPresenter.class.getSimpleName();

    private final DataManager dataManager;
    private final LifecycleProvider<Lifecycle.Event> provider;

    @Inject
    public GoogleRecaptchaVerifyPresenter(DataManager dataManager, LifecycleProvider<Lifecycle.Event> provider) {
        this.dataManager = dataManager;
        this.provider = provider;
    }

    @Override
    public void testV9Porn() {
        this.dataManager.testV9Porn(dataManager.getPorn9VideoAddress())
                .retryWhen(new RetryWhenProcess(RetryWhenProcess.PROCESS_TIME))
                .compose(RxSchedulersHelper.ioMainThread())
                .compose(provider.bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .map(responseBodyResponse -> {
                    if (responseBodyResponse.isSuccessful()) {
                        Logger.t(TAG).d("加载成功，无需验证");
                        return "OK";
                    } else {
                        Logger.t(TAG).d("加载失败，需要验证");
                        if (responseBodyResponse.errorBody() != null) {
                            return injectJs(responseBodyResponse.errorBody().string());
                        }
                    }
                    return "";
                })
                .subscribe(new CallBackWrapper<String>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(GoogleRecaptchaVerifyView::startCheckNeedVerify);
                    }

                    @Override
                    public void onSuccess(String responseBody) {
                        if ("OK".equals(responseBody)) {
                            ifViewAttached(GoogleRecaptchaVerifyView::noNeedVerifyRecaptcha);
                        } else {
                            if (TextUtils.isEmpty(responseBody)) {
                                ifViewAttached(GoogleRecaptchaVerifyView::loadPageDataFailure);
                            } else {
                                ifViewAttached(view -> view.needVerifyRecaptcha(responseBody));
                            }
                        }
                    }

                    @Override
                    public void onError(String msg, int code) {
                        Logger.t(TAG).d("加载失败，网络异常");
                        ifViewAttached(GoogleRecaptchaVerifyView::loadPageDataFailure);
                    }
                });
    }

    private String injectJs(String oldHtml) {
        if (TextUtils.isEmpty(oldHtml)) {
            return "";
        }
        Document doc = Jsoup.parse(oldHtml);
        doc.head().append("<script type=\"text/javascript\">\n" +
                "        function getPostData() {\n" +
                "            let recaptcha = document.getElementById(\"g-recaptcha-response\").value;\n" +
                "            if (!recaptcha || recaptcha === '') {\n" +
                "                recaptcha = document.getElementById(\"g-recaptcha-response\").innerHTML;\n" +
                "            }\n" +
                "            const action = document.getElementById('challenge-form').getAttribute(\"action\");\n" +
                "            const r = document.getElementsByName(\"r\")[0].getAttribute(\"value\");\n" +
                "            const id = document.getElementById('id').getAttribute(\"value\");\n" +
                "            return action + \",\" + r + \",\" + id + \",\" + recaptcha;\n" +
                "        }\n" +
                "    </script>");

        String html = doc.outerHtml();
        Log.d(TAG, "JS注入完成");
        return html;
    }

    @Override
    public void verifyGoogleRecaptcha(String action, String r, String id, String recaptcha) {
        dataManager.verifyGoogleRecaptcha(action, r, id, recaptcha)
                .retryWhen(new RetryWhenProcess(RetryWhenProcess.PROCESS_TIME))
                .compose(RxSchedulersHelper.ioMainThread())
                .compose(provider.bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<Response<ResponseBody>>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(GoogleRecaptchaVerifyView::startVerifyRecaptcha);
                    }

                    @Override
                    public void onSuccess(Response<ResponseBody> responseBodyResponse) {
                        if (responseBodyResponse.isSuccessful()) {
                            Log.d(TAG, "验证成功了");
                            ifViewAttached(GoogleRecaptchaVerifyView::verifyRecaptchaSuccess);
                        } else {
                            Log.d(TAG, "验证失败了");
                            ifViewAttached(GoogleRecaptchaVerifyView::verifyRecaptchaFailure);
                        }
                    }

                    @Override
                    public void onError(String msg, int code) {
                        Log.d(TAG, "访问网络失败");
                        ifViewAttached(GoogleRecaptchaVerifyView::verifyRecaptchaFailure);
                    }
                });
    }

    @Override
    public String getBaseAddress() {
        return dataManager.getPorn9VideoAddress();
    }
}
