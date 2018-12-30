package com.u9porn.data.network;

import com.u9porn.data.model.BaseResult;
import com.u9porn.data.model.F9PronItem;
import com.u9porn.data.model.HuaBan;
import com.u9porn.data.model.MeiZiTu;
import com.u9porn.data.model.Mm99;
import com.u9porn.data.model.Notice;
import com.u9porn.data.model.pxgav.PavModel;
import com.u9porn.data.model.pxgav.PavVideoParserJsonResult;
import com.u9porn.data.model.PinnedHeaderEntity;
import com.u9porn.data.model.F9PornContent;
import com.u9porn.data.model.ProxyModel;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.data.model.UpdateVersion;
import com.u9porn.data.model.User;
import com.u9porn.data.model.VideoComment;
import com.u9porn.data.db.entity.VideoResult;
import com.u9porn.data.model.axgle.AxgleResponse;
import com.u9porn.data.model.axgle.AxgleVideo;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * @author flymegoc
 * @date 2018/3/4
 */

public interface ApiHelper {
    Observable<List<V9PornItem>> loadPorn9VideoIndex(boolean cleanCache);

    Observable<BaseResult<List<V9PornItem>>> loadPorn9VideoByCategory(String category, String viewType, int page, String m, boolean cleanCache, boolean isLoadMoreCleanCache);

    Observable<BaseResult<List<V9PornItem>>> loadPorn9authorVideos(String uid, String type, int page, boolean cleanCache);

    Observable<BaseResult<List<V9PornItem>>> loadPorn9VideoRecentUpdates(String next, int page, boolean cleanCache, boolean isLoadMoreCleanCache);

    Observable<VideoResult> loadPorn9VideoUrl(String viewKey);

    Observable<List<VideoComment>> loadPorn9VideoComments(String videoId, int page, String viewKey);

    Observable<String> commentPorn9Video(String cpaintFunction, String comment, String uid, String vid, String viewKey, String responseType);

    Observable<String> replyPorn9VideoComment(String comment, String username, String vid, String commentId, String viewKey);

    Observable<BaseResult<List<V9PornItem>>> searchPorn9Videos(String viewType, int page, String searchType, String searchId, String sort);

    Observable<String> favoritePorn9Video(String uId, String videoId, String ownnerId);

    Observable<BaseResult<List<V9PornItem>>> loadPorn9MyFavoriteVideos(String userName, int page, boolean cleanCache);

    Observable<List<V9PornItem>> deletePorn9MyFavoriteVideo(String rvid);

    Observable<User> userLoginPorn9Video(String username, String password, String captcha);

    Observable<User> userRegisterPorn9Video(String username, String password1, String password2, String email, String captchaInput);

    Observable<List<PinnedHeaderEntity<F9PronItem>>> loadPorn9ForumIndex();

    Observable<BaseResult<List<F9PronItem>>> loadPorn9ForumListData(String fid, int page);

    Observable<F9PornContent> loadPorn9ForumContent(Long tid, final boolean isNightModel);

    Observable<UpdateVersion> checkUpdate();

    Observable<Notice> checkNewNotice();

    Observable<BaseResult<List<MeiZiTu>>> listMeiZiTu(String tag, int page, boolean pullToRefresh);

    Observable<List<String>> meiZiTuImageList(int id, boolean pullToRefresh);

    Observable<BaseResult<List<Mm99>>> list99Mm(String category, int page, boolean cleanCache);

    Observable<List<String>> mm99ImageList(int id, String contentUrl, boolean pullToRefresh);

    Observable<List<PavModel>> loadPavListByCategory(String category, boolean pullToRefresh);

    Observable<List<PavModel>> loadMorePavListByCategory(String category, int page, boolean pullToRefresh);

    Observable<PavVideoParserJsonResult> loadPavVideoUrl(String url, String pId, boolean pullToRefresh);

    Observable<BaseResult<List<ProxyModel>>> loadXiCiDaiLiProxyData(int page);

    Observable<Boolean> testProxy(String proxyIpAddress, int proxyPort);

    void existProxyTest();

    Observable<Boolean> testPorn9VideoAddress();

    Observable<Boolean> testPorn9ForumAddress();

    Observable<Boolean> testPavAddress(String url);

    Observable<Boolean> testAxgle();

    Observable<List<HuaBan.Picture>> findPictures(int categoryId, int page);

    Observable<AxgleResponse> axgleVideos(int page, String o, String t, String type, String c, int limit);

    Observable<List<AxgleVideo>> searchAxgleVideo();

    Observable<List<AxgleVideo>> searchAxgleJavVideo();

    Call<ResponseBody> getPlayVideoUrl(String url);
}
