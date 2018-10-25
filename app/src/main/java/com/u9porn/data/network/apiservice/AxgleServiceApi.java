package com.u9porn.data.network.apiservice;

import com.u9porn.data.network.Api;

import io.reactivex.Observable;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @author megoc
 */
public interface AxgleServiceApi {

    /**
     * 收藏夹
     *
     * @param page 页码
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.AXGLE_DOMAIN_NAME})
    @GET("v1/collections/{page}")
    Observable<String> collections(@Path("page") int page);


    /**
     * 各分类视频
     *
     * @param page  页码
     * @param o     排序 default mr
     *              bw (Last viewed)
     *              mr (Latest)
     *              mv (Most viewed)
     *              tr (Top rated)
     *              tf (Most favoured)
     *              lg (Longest)
     * @param t     时间 default a
     *              t (1 day)
     *              w (1 week)
     *              m (1 month)
     *              a (Forever)
     * @param type  类别 public  private
     * @param c     CHID of a valid video category (integer)
     * @param limit 每页数 [1, 250]
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.AXGLE_DOMAIN_NAME})
    @GET("v1/videos/{page}")
    Observable<String> videos(@Path("page") int page, @Query("o") String o, @Query("t") String t, @Query("type") String type, @Query("c") String c, @Query("limit") int limit);

    /**
     * 搜索视频
     *
     * @param query 关键词
     * @param page  页码
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.AXGLE_DOMAIN_NAME})
    @GET("v1/search/{query}/{page}")
    Observable<String> search(@Path("query") String query, @Path("page") int page);

    /**
     * 搜索jav视频
     *
     * @param query 关键词
     * @param page  页码
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.AXGLE_DOMAIN_NAME})
    @GET("v1/jav/{query}/{page}")
    Observable<String> searchJav(@Path("query") String query, @Path("page") int page);

    /**
     * 获取视频播放地址
     *
     * @param url 连接
     * @return ob
     */
    @GET
    Call<ResponseBody> getPlayVideoUrl(@Url String url);
}
