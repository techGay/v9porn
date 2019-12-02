package com.u9porn.parser;

import android.text.TextUtils;
import android.util.Base64;

import com.orhanobut.logger.Logger;
import com.u9porn.data.db.entity.V9PornItem;
import com.u9porn.data.db.entity.VideoResult;
import com.u9porn.data.model.BaseResult;
import com.u9porn.data.model.User;
import com.u9porn.data.model.VideoComment;
import com.u9porn.utils.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author flymegoc
 * @date 2017/11/15
 * @describe
 */

public class ParseV9PronVideo {

    private static final String TAG = ParseV9PronVideo.class.getSimpleName();

    /**
     * 解析主页
     *
     * @param html 主页html
     * @return 视频列表
     */
    public static List<V9PornItem> parseIndex(String html) {
        List<V9PornItem> v9PornItemList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element body = doc.getElementById("tab-featured");
        Elements itms = body.select("p");
        for (Element element : itms) {
            V9PornItem v9PornItem = new V9PornItem();

            String title = element.getElementsByClass("title").first().text();
            v9PornItem.setTitle(title);
            // Logger.d(title);

            String imgUrl = element.select("img").first().attr("src");
            v9PornItem.setImgUrl(imgUrl);
            // Logger.d(imgUrl);

            String duration = element.getElementsByClass("duration").first().text();
            v9PornItem.setDuration(duration);
            //Logger.d(duration);

            String contentUrl = element.select("a").first().attr("href");
            String viewKey = contentUrl.substring(contentUrl.indexOf("=") + 1);
            v9PornItem.setViewKey(viewKey);
            // Logger.d(viewKey);

            String allInfo = element.text();
            int start = allInfo.indexOf("添加时间");
            String info = allInfo.substring(start);

            v9PornItem.setInfo(info);
            // Logger.d(info);
            v9PornItemList.add(v9PornItem);
        }
        return v9PornItemList;
    }

    /**
     * 解析其他类别
     *
     * @param html 类别
     * @return 列表
     */
    public static BaseResult<List<V9PornItem>> parseByCategory(String html) {
        int totalPage = 1;
        List<V9PornItem> v9PornItemList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element body = doc.getElementById("fullside");

        Elements listchannel = body.getElementsByClass("listchannel");
        for (Element element : listchannel) {
            V9PornItem v9PornItem = new V9PornItem();
            String contentUrl = element.select("a").first().attr("href");
            //Logger.d(contentUrl);
            contentUrl = contentUrl.substring(0, contentUrl.indexOf("&"));
            // Logger.d(contentUrl);
            String viewKey = contentUrl.substring(contentUrl.indexOf("=") + 1);
            v9PornItem.setViewKey(viewKey);

            String imgUrl = element.select("a").first().select("img").first().attr("src");
            //  Logger.d(imgUrl);
            v9PornItem.setImgUrl(imgUrl);

            String title = element.select("a").first().select("img").first().attr("title");
            //  Logger.d(title);
            v9PornItem.setTitle(title);


            String allInfo = element.text();

            int sindex = allInfo.indexOf("时长");

            String duration = allInfo.substring(sindex + 3, sindex + 8);
            v9PornItem.setDuration(duration);

            int start = allInfo.indexOf("添加时间");
            String info = allInfo.substring(start);
            v9PornItem.setInfo(info.replace("还未被评分", ""));
            //  Logger.d(info);

            v9PornItemList.add(v9PornItem);
        }
        //总页数
        Element pagingnav = body.getElementById("paging");
        Elements a = pagingnav.select("a");
        if (a.size() > 2) {
            String ppp = a.get(a.size() - 2).text();
            if (TextUtils.isDigitsOnly(ppp)) {
                totalPage = Integer.parseInt(ppp);
                //    Logger.d("总页数：" + totalPage);
            }
        }
        BaseResult<List<V9PornItem>> baseResult = new BaseResult<>();
        baseResult.setTotalPage(totalPage);
        baseResult.setData(v9PornItemList);
        return baseResult;
    }

    public static BaseResult<List<V9PornItem>> parseSearchVideos(String html) {
        int totalPage = 1;
        List<V9PornItem> v9PornItemList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element body = doc.getElementById("fullside");
        if (body == null) {
            String errorMsg = parseErrorInfo(html);
            Logger.t(TAG).d(errorMsg);
            BaseResult<List<V9PornItem>> baseResult = new BaseResult<>();

            baseResult.setCode(BaseResult.ERROR_CODE);
            baseResult.setMessage(errorMsg);
            return baseResult;
        }
        Elements listchannel = body.getElementsByClass("listchannel");
        for (Element element : listchannel) {
            V9PornItem v9PornItem = new V9PornItem();
            String contentUrl = element.select("a").first().attr("href");
            //Logger.d(contentUrl);
            contentUrl = contentUrl.substring(0, contentUrl.indexOf("&"));
            //Logger.d(contentUrl);

            String viewKey = contentUrl.substring(contentUrl.indexOf("=") + 1);
            v9PornItem.setViewKey(viewKey);
            //Logger.d(viewKey);

            String imgUrl = element.select("a").first().select("img").first().attr("src");
            //Logger.d(imgUrl);
            v9PornItem.setImgUrl(imgUrl);

            String title = element.select("span.title").text();
            //Logger.d(title);
            v9PornItem.setTitle(title);

            String duration = element.select("span.duration").text();
            v9PornItem.setDuration(duration);

            String allInfo = element.text();

            int start = allInfo.indexOf("添加时间");
            String info = allInfo.substring(start);
            v9PornItem.setInfo(info.replace("还未被评分", ""));
            //Logger.d(info);

            v9PornItemList.add(v9PornItem);
        }
        //总页数
        Element pagingnav = body.getElementById("paging");
        Elements a = pagingnav.select("a");
        if (a.size() > 2) {
            String ppp = a.get(a.size() - 2).text();
            if (TextUtils.isDigitsOnly(ppp)) {
                totalPage = Integer.parseInt(ppp);
                //Logger.d("总页数：" + totalPage);
            }
        }
        BaseResult<List<V9PornItem>> baseResult = new BaseResult<>();
        baseResult.setTotalPage(totalPage);
        baseResult.setData(v9PornItemList);
        return baseResult;
    }

    /**
     * 解析视频播放连接
     *
     * @param html 视频页
     * @return 视频连接
     */
    public static VideoResult parseVideoPlayUrl(String html, User user) {
        VideoResult videoResult = new VideoResult();
        if (html.contains("你每天只可观看10个视频")) {
            Logger.d("已经超出观看上限了");
            //设置标志位,用于上传日志
            videoResult.setId(VideoResult.OUT_OF_WATCH_TIMES);
            return videoResult;
        }
        if (html.contains("视频不存在,可能已经被删除或者被举报为不良内容!")){
            videoResult.setId(VideoResult.VIDEO_NOT_EXIST_OR_DELETE);
            return videoResult;
        }
        final String reg = "document.write\\(strencode\\(\"(.+)\",\"(.+)\",.+\\)\\);";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(html);
        String param1 = "", param2 = "";
        if (m.find()) {
            param1 = m.group(1);
            param2 = m.group(2);
        }
        param1 = new String(Base64.decode(param1.getBytes(), Base64.DEFAULT));
        String source_str = "";
        for (int i = 0, k = 0; i < param1.length(); i++) {
            k = i % param2.length();
            source_str += "" + (char) (param1.codePointAt(i) ^ param2.codePointAt(k));
        }
        Logger.t(TAG).d("视频source1：" + source_str);
        source_str = new String(Base64.decode(source_str.getBytes(), Base64.DEFAULT));
        Logger.t(TAG).d("视频source2：" + source_str);

        String videoUrl;
        if (TextUtils.isEmpty(source_str)) {
            Logger.t(TAG).d(html);
            Document doc = Jsoup.parse(html);
            videoUrl = doc.select("video").first().select("source").first().attr("src");
        } else {
            Document source = Jsoup.parse(source_str);
            videoUrl = source.select("source").first().attr("src");
            videoResult.setVideoUrl(videoUrl);
            Logger.t(TAG).d("视频链接：" + videoUrl);
        }


        int startIndex = videoUrl.lastIndexOf("/");
        int endIndex = videoUrl.indexOf(".mp4");
        String videoId = videoUrl.substring(startIndex + 1, endIndex);
        videoResult.setVideoId(videoId);
        Logger.t(TAG).d("视频Id：" + videoId);

        //这里解析的作者id已经变了，非纯数字了
        Document doc = Jsoup.parse(html);
        String ownerUrl = doc.select("a[href*=UID]").first().attr("href");
        String ownerId = ownerUrl.substring(ownerUrl.indexOf("=") + 1, ownerUrl.length());
        videoResult.setOwnerId(ownerId);
        Logger.t(TAG).d("作者Id：" + ownerId);

        String addToFavLink = doc.getElementById("addToFavLink").selectFirst("a").attr("onClick");
        String args[] = addToFavLink.split(",");
        String userId = args[1].trim();
        Logger.t(TAG).d("userId:::" + userId);
        user.setUserId(Integer.parseInt(userId));

        //原始纯数字作者id，用于收藏接口
        String authorId = args[3].replace(");", "").trim();
        Logger.t(TAG).d("authorId:::" + authorId);
        videoResult.setAuthorId(authorId);

        String ownerName = doc.select("a[href*=UID]").first().text();
        videoResult.setOwnerName(ownerName);
        Logger.t(TAG).d("作者：" + ownerName);

        String allInfo = doc.getElementById("videodetails-content").text();
        String addDate = allInfo.substring(allInfo.indexOf("添加时间"), allInfo.indexOf("作者"));
        videoResult.setAddDate(addDate);
        Logger.t(TAG).d("添加时间：" + addDate);

        String otherInfo = allInfo.substring(allInfo.indexOf("注册"), allInfo.indexOf("简介"));
        videoResult.setUserOtherInfo(otherInfo);
        Logger.t(TAG).d(otherInfo);

        try {
            String thumImg = doc.getElementById("player_one").attr("poster");
            videoResult.setThumbImgUrl(thumImg);
            Logger.t(TAG).d("缩略图：" + thumImg);
        } catch (Exception e) {
            e.printStackTrace();
        }


        String videoName = doc.getElementById("viewvideo-title").text();
        videoResult.setVideoName(videoName);
        Logger.t(TAG).d("视频标题：" + videoName);

        return videoResult;
    }

    /**
     * 解析登录用户信息
     *
     * @return 用户
     */
    public static User parseUserInfo(String html) {
        User user = new User();
        Document doc = Jsoup.parse(html);
        //新帐号注册成功登录后信息不一样，导致无法解析
        Element element = doc.getElementById("userinfo-title");
        if (element == null) {
            Logger.t(TAG).d(html);
            user.setLogin(true);
            user.setUserName("无法解析用户信息...");
            return user;
        }

        //解析用户uid，2018年3月29日 似乎已经失效了,可在播放界面获取
        Element userLickElement = element.select("a").first();
        if (userLickElement != null) {
            String userLinks = userLickElement.attr("href");
            int uid = Integer.parseInt(StringUtils.subString(userLinks, userLinks.indexOf("=") + 1, userLinks.length()));
            user.setUserId(uid);
            Logger.t(TAG).d(userLinks);
            Logger.t(TAG).d(uid);
        } else {
            Logger.t(TAG).d("无法解析用户uid");
        }
        String userInfoTitle = doc.getElementById("userinfo-title").text();
        String userName = StringUtils.subString(userInfoTitle, userInfoTitle.indexOf("欢迎") + 3, userInfoTitle.indexOf("用户状态"));
        Logger.t(TAG).d(userName);
        user.setUserName(userName);

        String userAccountStatus = doc.getElementById("userinfo-title").select("font").first().text();
        Logger.t(TAG).d(userAccountStatus);
        user.setStatus(userAccountStatus);

        String userContent = doc.getElementById("userinfo-content").text();
        Logger.t(TAG).d(userContent);

        try {
            String lastLoginTime = userContent.substring(userContent.indexOf("):"), userContent.indexOf("IP:"));
            String lastLoginIP = userContent.substring(userContent.indexOf("IP:"), userContent.indexOf("我的状态"));
            user.setLastLoginTime(lastLoginTime);
            user.setLastLoginIP(lastLoginIP);

            Logger.t(TAG).d(lastLoginTime);
            Logger.t(TAG).d(lastLoginIP);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * 解析我的收藏
     *
     * @param html html
     * @return list
     */
    public static BaseResult<List<V9PornItem>> parseMyFavorite(String html) {
        int totalPage = 1;
        List<V9PornItem> v9PornItemList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element body = doc.getElementById("leftside");

        Elements videos = doc.select("div.myvideo");

        for (Element element : videos) {

            V9PornItem v9PornItem = new V9PornItem();

            String contentUrl = element.select("a").first().attr("href");

            String viewKey = contentUrl.substring(contentUrl.indexOf("=") + 1, contentUrl.length());
            v9PornItem.setViewKey(viewKey);
            Logger.t(TAG).d(viewKey);

            String title = element.select("strong").first().text();
            v9PornItem.setTitle(title);
            Logger.t(TAG).d(title);

            String imgUrl = element.select("img").first().attr("src");
            v9PornItem.setImgUrl(imgUrl);
            Logger.t(TAG).d(imgUrl);

            String allInfo = element.text();
            Logger.t(TAG).d(allInfo);

            String duration = allInfo.substring(allInfo.indexOf("时长") + 3, allInfo.indexOf("Views") - 3);
            v9PornItem.setDuration(duration);
            Logger.t(TAG).d(duration);

            String info = allInfo.substring(allInfo.indexOf("添加时间"), allInfo.length());
            v9PornItem.setInfo(info);
            Logger.t(TAG).d(info);

            String rvid = element.select("input").first().attr("value");
            Logger.t(TAG).d("rvid::" + rvid);
            VideoResult videoResult = new VideoResult();
            videoResult.setId(VideoResult.OUT_OF_WATCH_TIMES);
            videoResult.setVideoId(rvid);
            v9PornItem.setVideoResult(videoResult);

            v9PornItemList.add(v9PornItem);
        }

        //总页数
        Element pagingnav = body.getElementById("paging");
        Elements a = pagingnav.select("a");
        if (a.size() >= 2) {
            String ppp = a.get(a.size() - 2).text();
            if (TextUtils.isDigitsOnly(ppp)) {
                totalPage = Integer.parseInt(ppp);
                Logger.d("总页数：" + totalPage);
            }
        }
        BaseResult<List<V9PornItem>> baseResult = new BaseResult<>();
        //尝试解析删除信息
        Elements msgElements = doc.select("div.msgbox");
        if (msgElements != null) {
            String msgInfo = msgElements.text();
            if (!TextUtils.isEmpty(msgInfo)) {
                baseResult.setCode(BaseResult.SUCCESS_CODE);
                baseResult.setMessage(msgInfo);
            }
        } else {
            String errorMsg = parseErrorInfo(html);
            if (!TextUtils.isEmpty(errorMsg)) {
                baseResult.setMessage(errorMsg);
                baseResult.setCode(BaseResult.ERROR_CODE);
            }
        }

        baseResult.setTotalPage(totalPage);
        baseResult.setData(v9PornItemList);

        return baseResult;
    }

    /**
     * 解析作者更多视频
     *
     * @param html html
     * @return list
     */
    public static BaseResult<List<V9PornItem>> parseAuthorVideos(String html) {
        int totalPage = 1;
        List<V9PornItem> v9PornItemList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element body = doc.getElementById("leftside");

        Elements videos = doc.select("div.myvideo");

        for (Element element : videos) {

            V9PornItem v9PornItem = new V9PornItem();

            String contentUrl = element.select("a").first().attr("href");

            String viewKey = contentUrl.substring(contentUrl.indexOf("=") + 1, contentUrl.length());
            v9PornItem.setViewKey(viewKey);
            //Logger.t(TAG).d(viewKey);

            String title = element.select("strong").first().text();
            v9PornItem.setTitle(title);
            //Logger.t(TAG).d(title);

            String imgUrl = element.select("img").first().attr("src");
            v9PornItem.setImgUrl(imgUrl);
            //Logger.t(TAG).d(imgUrl);

            String allInfo = element.text();
            //Logger.t(TAG).d(allInfo);

            String duration = allInfo.substring(allInfo.indexOf("时长") + 3, allInfo.indexOf("查看") - 3);
            v9PornItem.setDuration(duration);
            //Logger.t(TAG).d(duration);

            String info = allInfo.substring(allInfo.indexOf("添加时间"), allInfo.length());
            v9PornItem.setInfo(info);
            //Logger.t(TAG).d(info);

            v9PornItemList.add(v9PornItem);
        }

        //总页数
        Element pagingnav = body.getElementById("paging");
        Elements a = pagingnav.select("a");
        if (a.size() >= 2) {
            String ppp = a.get(a.size() - 2).text();
            if (TextUtils.isDigitsOnly(ppp)) {
                totalPage = Integer.parseInt(ppp);
                //Logger.d("总页数：" + totalPage);
            }
        }

        BaseResult<List<V9PornItem>> baseResult = new BaseResult<>();
        baseResult.setTotalPage(totalPage);
        baseResult.setData(v9PornItemList);

        return baseResult;
    }

    /**
     * 解析错误提示
     *
     * @param html html
     * @return 错误洗洗脑
     */
    public static String parseErrorInfo(String html) {
        String errorInfo = "";
        Document doc = Jsoup.parse(html);
        Elements errorElements = doc.select("div.errorbox");
        if (errorElements != null) {
            errorInfo = errorElements.text();
        }
        return errorInfo;
    }

    /**
     * 解析视频评论
     *
     * @param html 评论html
     * @return 评论列表
     */
    public static List<VideoComment> parseVideoComment(String html) {
        List<VideoComment> videoCommentList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("table.comment-divider");
        for (Element element : elements) {
            VideoComment videoComment = new VideoComment();

            String ownnerUrl = element.select("a[href*=UID]").first().attr("href");
            String uid = ownnerUrl.substring(ownnerUrl.indexOf("=") + 1, ownnerUrl.length());
            videoComment.setUid(uid);
            //Logger.t(TAG).d(uid);

            String uName = element.select("a[href*=UID]").first().text();
            videoComment.setuName(uName);
            Logger.t(TAG).d(uName);

            String replyTime = element.select("span.comment-info").first().text();
            videoComment.setReplyTime(replyTime.replace("(", "").replace(")", ""));
            // Logger.t(TAG).d(replyTime);

            String tmpreplyId = element.select("div.comment-body").first().attr("id");
            String replyId = tmpreplyId.substring(tmpreplyId.lastIndexOf("_") + 1, tmpreplyId.length());
            videoComment.setReplyId(replyId);
            // Logger.t(TAG).d("replyId:" + replyId);

            String comment = element.select("div.comment-body").first().text().replace("举报", "").replace("Show", "");
//            videoComment.setContentMessage(comment.replace("Show", ""));
            //Logger.t(TAG).d(comment);

            List<String> tmpQuoteList = new ArrayList<>();
            tmpQuoteList.add(comment);
            Elements quotes = element.select("div.comment-body").select("div.comment_quote");
            for (Element element1 : quotes) {
                String quote = element1.text();
                tmpQuoteList.add(quote);
            }

            List<String> quoteList = new ArrayList<>();
            for (int i = 0; i < tmpQuoteList.size(); i++) {
                String quote;
                if (i + 1 >= tmpQuoteList.size()) {
                    quote = tmpQuoteList.get(i);
                    quoteList.add(0, quote.trim());
                    //Logger.t(TAG).d(quote);
                    break;
                }
                quote = tmpQuoteList.get(i).replace(tmpQuoteList.get(i + 1), "");
                quoteList.add(0, quote.trim());
                //Logger.t(TAG).d(quote);
            }

            videoComment.setCommentQuoteList(quoteList);

            String info = element.select("td").first().text();
            String titleInfo = info.substring(0, info.indexOf("("));
            videoComment.setTitleInfo(titleInfo.replace(uName, ""));
            // Logger.t(TAG).d(titleInfo);

            videoCommentList.add(videoComment);
        }
        return videoCommentList;
    }
}
