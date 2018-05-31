package com.u9porn.data.network.apiservice;

import com.u9porn.data.network.Api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @author flymegoc
 * @date 2018/2/1
 */

public interface Mm99ServiceApi {

    /**
     * 图库列表
     *
     * @param url 链接
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.MM_99_DOMAIN_NAME})
    @GET
    Observable<String> imageList(@Url String url);

    /**
     * 对应图库图片列表
     *
     * @param url url
     * @return ob
     */
    @Headers({"Referer: http://www.99mm.me/meitui/",
            "Domain-Name: " + Api.MM_99_DOMAIN_NAME})
    @GET
    Observable<String> imageLists(@Url String url);
}
