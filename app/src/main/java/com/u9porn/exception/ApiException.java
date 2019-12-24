package com.u9porn.exception;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.orhanobut.logger.Logger;
import com.u9porn.BuildConfig;
import com.u9porn.eventbus.NeedCheckGoogleRecaptchaEvent;

import org.apache.http.conn.ConnectTimeoutException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.DaoException;
import org.json.JSONException;

import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.exceptions.CompositeException;
import io.rx_cache2.RxCacheException;
import retrofit2.HttpException;

/**
 * @author flymegoc
 * @date 2017/12/26
 */

public class ApiException extends Exception {
    private static final String TAG = ApiException.class.getSimpleName();
    private static final int BADREQUEST = 400;
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int METHOD_NOT_ALLOWED = 405;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    private final int code;

    public static final int UNKNOWN = 1000;
    public static final int PARSE_ERROR = 1001;
    private String message;

    private ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
        this.message = throwable.getMessage();
    }

    public int getCode() {
        return code;
    }


    public static ApiException handleException(Throwable e) {
        //使用RxCache之后返回的是包裹的CompositeException，一般包含2个异常，rxcache异常和原本的异常
        Logger.t(TAG).d("开始解析错误------");
        if (e instanceof CompositeException) {
            CompositeException compositeException = (CompositeException) e;
            for (Throwable throwable : compositeException.getExceptions()) {
                if (!(throwable instanceof RxCacheException)) {
                    e = throwable;
                    Logger.t(TAG).d("其他异常：" + throwable.getMessage());
                } else {
                    Logger.t(TAG).d("RxCache 异常");
                }
            }
        }
        ApiException ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ApiException(httpException, httpException.code());
            ex.message = httpException.getMessage();
            //如果是403，尝试让用户手动验证
            if (httpException.code() == 403) {
                EventBus.getDefault().post(new NeedCheckGoogleRecaptchaEvent());
            }
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof JsonSerializer
                || e instanceof NotSerializableException
                || e instanceof ParseException) {
            ex = new ApiException(e, Error.PARSE_ERROR);
            ex.message = "数据解析错误";
            return ex;
        } else if (e instanceof ClassCastException) {
            ex = new ApiException(e, Error.CAST_ERROR);
            ex.message = "类型转换错误";
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ApiException(e, Error.NETWORD_ERROR);
            ex.message = "连接失败";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ApiException(e, Error.SSL_ERROR);
            ex.message = "证书验证失败";
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ApiException(e, Error.TIMEOUT_ERROR);
            ex.message = "网络连接超时";
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ApiException(e, Error.TIMEOUT_ERROR);
            ex.message = "网络连接超时";
            return ex;
        } else if (e instanceof UnknownHostException) {
            ex = new ApiException(e, Error.UNKNOWNHOST_ERROR);
            ex.message = "无法解析该域名";
            return ex;
        } else if (e instanceof NullPointerException) {
            if (!BuildConfig.DEBUG) {
                //Bugsnag.notify(new Throwable("NullPointerException:" + MyApplication.getInstance().getDataManager().getPorn9VideoAddress() + ":::" + MyApplication.getInstance().getDataManager().getPorn9ForumAddress(), e), Severity.WARNING);
            }
            ex = new ApiException(e, Error.NULLPOINTER_EXCEPTION);
            ex.message = "NullPointerException";
            return ex;
        } else if (e instanceof VideoException) {
            ex = new ApiException(e, Error.PARSE_VIDEO_URL_ERROR);
            ex.message = e.getMessage();
            return ex;
        } else if (e instanceof FavoriteException) {
            ex = new ApiException(e, Error.FAVORITE_VIDEO_ERROR);
            ex.message = e.getMessage();
            return ex;
        } else if (e instanceof DaoException) {
            ex = new ApiException(e, Error.GREEN_DAO_ERROR);
            ex.message = "数据库错误";
            return ex;
        } else if (e instanceof MessageException) {
            ex = new ApiException(e, Error.COMMON_MESSAGE_ERROR);
            ex.message = e.getMessage();
            return ex;
        } else {
            ex = new ApiException(e, Error.UNKNOWN);
            ex.message = "未知错误：" + e.getMessage();
            return ex;
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 约定异常
     */
    public static class Error {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = UNKNOWN + 1;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = PARSE_ERROR + 1;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = NETWORD_ERROR + 1;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = HTTP_ERROR + 1;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = SSL_ERROR + 1;

        /**
         * 调用错误
         */
        public static final int INVOKE_ERROR = TIMEOUT_ERROR + 1;
        /**
         * 类转换错误
         */
        public static final int CAST_ERROR = INVOKE_ERROR + 1;
        /**
         * 请求取消
         */
        public static final int REQUEST_CANCEL = CAST_ERROR + 1;
        /**
         * 未知主机错误
         */
        public static final int UNKNOWNHOST_ERROR = REQUEST_CANCEL + 1;

        /**
         * 空指针错误
         */
        public static final int NULLPOINTER_EXCEPTION = UNKNOWNHOST_ERROR + 1;
        /**
         * 解析视频链接错误
         */
        private static final int PARSE_VIDEO_URL_ERROR = NULLPOINTER_EXCEPTION + 1;
        /**
         * 解析视频链接错误
         */
        private static final int FAVORITE_VIDEO_ERROR = PARSE_VIDEO_URL_ERROR + 1;

        private static final int GREEN_DAO_ERROR = FAVORITE_VIDEO_ERROR + 1;

        private static final int COMMON_MESSAGE_ERROR = GREEN_DAO_ERROR + 1;
    }
}
