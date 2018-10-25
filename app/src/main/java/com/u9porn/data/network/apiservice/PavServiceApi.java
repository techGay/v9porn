package com.u9porn.data.network.apiservice;

import com.u9porn.data.network.Api;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * @author flymegoc
 * @date 2018/1/30
 */

public interface PavServiceApi {

    /**
     * 视频列表
     *
     * @param url 链接
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.PA_DOMAIN_NAME})
    @GET
    Observable<String> pigAvVideoList(@Url String url);

    /**
     * 视频链接
     *
     * @param url 链接
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.PA_DOMAIN_NAME})
    @GET
    Observable<String> pigAvVideoUrl(@Url String url);

    /**
     * 更多数据，严格说其实根本不分类，所有分类都一样
     *
     * @param action         td_ajax_block
     * @param tdAtts         json
     * @param tdBlockId      td_uid_10_5a7190641c8f5
     * @param tdColumnNumber 3
     * @param tdCurrentPage  page
     * @param blockType      td_block_16
     * @param tdFilterValue  null
     * @param tdUserAction   null
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.PA_DOMAIN_NAME})
    @FormUrlEncoded
    @POST("wp-admin/admin-ajax.php?td_theme_name=Newsmag&v=4.5")
    Observable<String> moreVideoList(@Field("action") String action, @Field("td_atts") String tdAtts, @Field("td_block_id") String tdBlockId, @Field("td_column_number") int tdColumnNumber, @Field("td_current_page") int tdCurrentPage, @Field("block_type") String blockType, @Field("td_filter_value") String tdFilterValue, @Field("td_user_action") String tdUserAction);
}
