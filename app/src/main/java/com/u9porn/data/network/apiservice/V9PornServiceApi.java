package com.u9porn.data.network.apiservice;

import com.u9porn.data.network.Api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @author flymegoc
 * @date 2017/11/14
 * @describe
 */

public interface V9PornServiceApi {
    /**
     * 主页index.php
     *
     * @return body
     */
    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    @GET("/index.php")
    Observable<String> porn9VideoIndexPhp(@Header("Referer") String referer);

    /**
     * 访问页面获取视频地址页面
     *
     * @param viewkey   视频的key
     * @param ipAddress 随机访问地址，为了突破限制游客每天10次观看次数
     * @return body
     */
    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    @GET("/view_video.php")
    Observable<String> getVideoPlayPage(@Query("viewkey") String viewkey, @Header("X-Forwarded-For") String ipAddress, @Header("Referer") String referer);

    /**
     * 获取相应类别数据
     *
     * @param category 类别
     * @param viewtype 类型
     * @param m        标记上下月，上月为 -1，其他直接null即可
     * @return body
     */
    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    @GET("/v.php")
    Observable<String> getCategoryPage(@Query("category") String category, @Query("viewtype") String viewtype, @Query("page") Integer page, @Query("m") String m, @Header("Referer") String referer);

    /**
     * 最近更新
     *
     * @param next 参数
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    @GET("/v.php")
    Observable<String> recentUpdates(@Query("next") String next, @Query("page") Integer page, @Header("Referer") String referer);

    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    @GET("captcha.php")
    Observable<ResponseBody> captcha();

    /**
     * @param username     用户名
     * @param password     密码
     * @param fingerprint  机器指纹唯一识别码
     * @param fingerprint2 机器指纹唯一识别码
     * @param captcha      验证码
     * @param actionlogin  登录
     * @param x            x坐标
     * @param y            y坐标
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    @FormUrlEncoded
    @POST("/login.php")
    Observable<String> login(@Field("username") String username, @Field("password") String password, @Field("fingerprint") String fingerprint, @Field("fingerprint2") String fingerprint2, @Field("captcha_input") String captcha, @Field("action_login") String actionlogin, @Field("x") String x, @Field("y") String y, @Header("Referer") String referer);

    /**
     * 用户注册
     *
     * @param next         空
     * @param username     用户名
     * @param password1    密码1
     * @param password2    密码2
     * @param email        邮箱
     * @param captchaInput 验证码
     * @param fingerprint  机器指纹校验
     * @param vip          vip -空
     * @param actionSignup 动作 value：Sign Up
     * @param submitX      人机x坐标 45
     * @param submitY      人机y坐标 13
     * @param ipAddress    随机ip
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    @FormUrlEncoded
    @POST("/signup.php")
    Observable<String> register(@Query("next") String next, @Field("username") String username, @Field("password1") String password1, @Field("password2") String password2, @Field("email") String email, @Field("captcha_input") String captchaInput, @Field("fingerprint") String fingerprint, @Field("vip") String vip, @Field("action_signup") String actionSignup, @Field("submit.x") String submitX, @Field("submit.y") String submitY, @Header("Referer") String referer, @Header("X-Forwarded-For") String ipAddress);

    /**
     * 我的收藏
     *
     * @param page    page
     * @param referer ref
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    @GET("/my_favour.php")
    Observable<String> myFavoriteVideo(@Query("page") int page, @Header("Referer") String referer);

    /**
     * 删除我的收藏
     * rvid=250198&removfavour=Remove+FavoriteJsonResult&x=45&y=19
     *
     * @param rvid        要删除的视频id
     * @param removFavour 标志
     * @param x           点击x
     * @param y           点击y
     * @param referer     rf
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    @FormUrlEncoded
    @POST("/my_favour.php")
    Observable<String> deleteMyFavoriteVideo(@Field("rvid") String rvid, @Field("removfavour") String removFavour, @Field("x") int x, @Field("y") int y, @Header("Referer") String referer);


    /**
     * 收藏视频
     *
     * @param cpaintFunction 动作
     * @param uId            用户id
     * @param videoId        视频id
     * @param ownerId        视频发布者id
     * @param responseType   返回类型
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    @GET("/ajax/myajaxphp.php")
    Observable<String> favoriteVideo(@Query("cpaint_function") String cpaintFunction, @Query("cpaint_argument[]") String uId, @Query("cpaint_argument[]") String videoId, @Query("cpaint_argument[]") String ownerId, @Query("cpaint_response_type") String responseType, @Header("Referer") String referer);

    /**
     * //xxxxxxxxxxx/show_comments2.php?VID=247965&start=1&comment_per_page=20
     * 获取视频评论
     *
     * @param vid            视频id
     * @param start          开始页码
     * @param commentPerPage 每页数
     * @return ob
     */
    @Headers({"X-Requested-With: XMLHttpRequest",
            "Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    @GET("/show_comments2.php")
    Observable<String> getVideoComments(@Query("VID") String vid, @Query("start") int start, @Query("comment_per_page") int commentPerPage, @Header("Referer") String referer);

    /**
     * xxxxxxxxxx//ajax/myajaxphp.php?cpaint_function=process_comments&cpaint_argument[]=哈哈哈&cpaint_argument[]=6826296&cpaint_argument[]=248261&cpaint_response_type=json
     * 评论视频,无需邮箱验证，因为后台根本就不验证
     *
     * @param cpaintFunction 动作process_comments
     * @param comments       评论内容
     * @param uId            用户id
     * @param videoId        视频id
     * @param responseType   返回类型
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    @GET("/ajax/myajaxphp.php")
    Observable<String> commentVideo(@Query("cpaint_function") String cpaintFunction, @Query("cpaint_argument[]") String comments, @Query("cpaint_argument[]") String uId, @Query("cpaint_argument[]") String videoId, @Query("cpaint_response_type") String responseType, @Header("Referer") String referer);

    /**
     * 回复评论
     *
     * @param comment   评论内容
     * @param username  要回复的用户名
     * @param vId       视频id
     * @param commentId 要回复的内容id
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    @FormUrlEncoded
    @POST("/post_comment.php")
    Observable<String> replyVideoComment(@Field("comment") String comment, @Field("username") String username, @Field("VID") String vId, @Field("comment_id") String commentId, @Header("Referer") String referer);


    /**
     * <a href="xxxxxx/search_result.php?viewtype=basic&amp;page=1&amp;search_type=search_videos&amp;search_id=内射&amp;sort=addate">添加时间</a>
     * 搜索
     *
     * @param viewtype   basic
     * @param page       页码
     * @param searchType 类型
     * @param searchId   搜索内容
     * @param sort       排序
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    @GET("/search_result.php")
    Observable<String> searchVideo(@Query("viewtype") String viewtype, @Query("page") int page, @Query("search_type") String searchType, @Query("search_id") String searchId, @Query("sort") String sort, @Header("Referer") String referer, @Header("X-Forwarded-For") String ipAddress);

    /**
     * xxxxxxxxx/uvideos.php?UID=6465533&type=public&page=1
     * 查看作者所有视频
     *
     * @param uid  作者id
     * @param type 类型
     * @param page 页码
     * @return ob
     */
    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    @GET("/uvideos.php")
    Observable<String> authorVideos(@Query("UID") String uid, @Query("type") String type, @Query("page") int page);

    /**
     * 测试是否能连接
     *
     * @param url 链接
     * @return ob
     */

    @GET
    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    Observable<Response<ResponseBody>> testV9Porn(@Url String url);


    /**
     * 提交google人机验证结果
     *
     * @param action    form action url
     * @param r         r
     * @param id        clf id
     * @param recaptcha token
     * @return ob
     */
    @POST
    @FormUrlEncoded
    @Headers({"Domain-Name: " + Api.PORN9_VIDEO_DOMAIN_NAME})
    Observable<Response<ResponseBody>> verifyGoogleRecaptcha(@Url String action, @Field("r") String r, @Field("id") String id, @Field("g-recaptcha-response") String recaptcha);
}
