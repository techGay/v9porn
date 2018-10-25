package com.u9porn.cookie;

import android.text.TextUtils;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.orhanobut.logger.Logger;
import com.u9porn.data.AppDataManager;
import com.u9porn.rxjava.CallBackWrapper;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cookie;

/**
 * @author flymegoc
 * @date 2018/3/5
 */
@Singleton
public class AppCookieManager implements CookieManager {

    private static final String TAG = AppDataManager.class.getSimpleName();
    private SharedPrefsCookiePersistor sharedPrefsCookiePersistor;

    private SetCookieCache setCookieCache;

    private PersistentCookieJar persistentCookieJar;

    @Inject
    public AppCookieManager(SharedPrefsCookiePersistor sharedPrefsCookiePersistor, SetCookieCache setCookieCache, PersistentCookieJar persistentCookieJar) {
        this.sharedPrefsCookiePersistor = sharedPrefsCookiePersistor;
        this.setCookieCache = setCookieCache;
        this.persistentCookieJar = persistentCookieJar;
    }

    @Override
    public void resetPorn91VideoWatchTime(final boolean forceReset) {
        Observable.fromCallable(new Callable<List<Cookie>>() {
            @Override
            public List<Cookie> call() throws Exception {
                return sharedPrefsCookiePersistor.loadAll();
            }
        }).flatMap(new Function<List<Cookie>, ObservableSource<Cookie>>() {
            @Override
            public ObservableSource<Cookie> apply(List<Cookie> cookies) throws Exception {
                return Observable.fromIterable(cookies);
            }
        }).filter(new Predicate<Cookie>() {
            @Override
            public boolean test(Cookie cookie) throws Exception {
                return "watch_times".equals(cookie.name());
            }
        }).filter(new Predicate<Cookie>() {
            @Override
            public boolean test(Cookie cookie) throws Exception {
                boolean isDigitsOnly = TextUtils.isDigitsOnly(cookie.value());
                if (!isDigitsOnly) {
                    Logger.t(TAG).d("观看次数cookies异常");
                 //   Bugsnag.notify(new Throwable(TAG + ":cookie watchTimes is not DigitsOnly"), Severity.WARNING);
                }
                return isDigitsOnly;
            }
        }).filter(new Predicate<Cookie>() {
            @Override
            public boolean test(Cookie cookie) throws Exception {
                int watchTime = Integer.parseInt(cookie.value());
                Logger.t(TAG).d("当前已经看了：" + watchTime + " 次");
                if (forceReset) {
                    Logger.t(TAG).d("已经观看10次，重置cookies");
                    sharedPrefsCookiePersistor.delete(cookie);
                    setCookieCache.delete(cookie);
                }
                return watchTime >= 10;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CallBackWrapper<Cookie>() {
                    @Override
                    public void onBegin(Disposable d) {
                        Logger.t(TAG).d("开始读取观看次数");
                    }

                    @Override
                    public void onSuccess(Cookie cookie) {
                        Logger.t(TAG).d("已经观看10次，重置cookies");
                        sharedPrefsCookiePersistor.delete(cookie);
                        setCookieCache.delete(cookie);
                    }

                    @Override
                    public void onError(String msg, int code) {
                        Logger.t(TAG).d("重置观看次数出错了：" + msg);
                      //  Bugsnag.notify(new Throwable(TAG + ":reset watchTimes error:" + msg), Severity.WARNING);
                    }
                });
    }

    @Override
    public void cleanAllCookies() {
        persistentCookieJar.clear();
    }
}
