package com.u9porn.data.network.apiservice;

import com.u9porn.data.network.Api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface HuaBanServiceApi {
    @Headers({"Domain-Name: " + Api.HUA_BAN_DOMAIN_NAME})
    @GET("/findPictures")
    Observable<String> findPictures(@Query("categoryId") int categoryId, @Query("page") int page, @Query("pageSize") int pageSize);
}
