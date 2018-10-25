package com.u9porn.parser;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.u9porn.data.model.BaseResult;
import com.u9porn.data.model.pxgav.PavModel;
import com.u9porn.data.model.pxgav.PavVideoParserJsonResult;
import com.u9porn.utils.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/22
 */

public class ParsePa {
    private static final String TAG = ParsePa.class.getSimpleName();

    /**
     * @param html 原网页
     * @return json===
     */
    public static BaseResult<PavVideoParserJsonResult> parserVideoUrl(String html) {
        BaseResult<PavVideoParserJsonResult> baseResult = new BaseResult<>();
        Document document = Jsoup.parse(html);
        Element videoWrapper = document.getElementsByClass("td-post-content td-pb-padding-side").first();
        String videoHtml = videoWrapper.html();
        Logger.t(TAG).d(videoHtml);
        int index = videoHtml.indexOf("setup") + 6;
        int endIndexV = videoHtml.indexOf(");");
        String videoUrl = videoHtml.substring(index, endIndexV);
        Logger.t(TAG).d(videoUrl);

        PavVideoParserJsonResult pavVideoParserJsonResult = new Gson().fromJson(videoUrl, PavVideoParserJsonResult.class);

        Elements items = document.getElementsByClass("td-block-span12");
        List<PavModel> pavModelList = new ArrayList<>();
        for (Element element : items) {
            PavModel pavModel = new PavModel();
            Element a = element.selectFirst("a");
            String title = a.attr("title");
            pavModel.setTitle(title);
            String contentUrl = a.attr("href");
            pavModel.setContentUrl(contentUrl);
            Element img = element.selectFirst("img");
            String imgUrl = img.attr("src");
            int beginIndex = imgUrl.lastIndexOf("/");
            int endIndex = imgUrl.indexOf("-");
            String bigImg = StringUtils.subString(imgUrl, 0, endIndex);
            if (TextUtils.isEmpty(bigImg)) {
                pavModel.setImgUrl(imgUrl);
            } else {
                pavModel.setImgUrl(bigImg + ".jpg");
            }
            String pId = StringUtils.subString(imgUrl, beginIndex + 1, endIndex);
            Logger.t(TAG).d(pId);
            pavModel.setpId(pId);

            int imgWidth = Integer.parseInt(img.attr("width"));
            pavModel.setImgWidth(imgWidth);
            int imgHeight = Integer.parseInt(img.attr("height"));
            pavModel.setImgHeight(imgHeight);
            pavModelList.add(pavModel);
        }
        pavVideoParserJsonResult.setPavModelList(pavModelList);
        baseResult.setData(pavVideoParserJsonResult);
        return baseResult;
    }

    public static BaseResult<List<PavModel>> videoList(String html) {
        BaseResult<List<PavModel>> baseResult = new BaseResult<>();
        baseResult.setTotalPage(1);

        Document doc = Jsoup.parse(html);
        Elements items = doc.getElementsByClass("td-block-span4");
        List<PavModel> pavModelList = new ArrayList<>();
        for (Element element : items) {
            PavModel pavModel = new PavModel();
            Element a = element.selectFirst("a");
            String title = a.attr("title");
            pavModel.setTitle(title);
            String contentUrl = a.attr("href");
            pavModel.setContentUrl(contentUrl);
            Element img = element.selectFirst("img");
            String imgUrl = img.attr("src");
            int beginIndex = imgUrl.lastIndexOf("/");
            int endIndex = imgUrl.lastIndexOf("-");
            String bigImg = StringUtils.subString(imgUrl, 0, endIndex);
            if (TextUtils.isEmpty(bigImg)) {
                pavModel.setImgUrl(imgUrl);
            } else {
                pavModel.setImgUrl(bigImg + ".jpg");
            }
            String pId = StringUtils.subString(imgUrl, beginIndex + 1, endIndex);
            Logger.t(TAG).d(pId);
            pavModel.setpId(pId);

            int imgWidth = Integer.parseInt(img.attr("width"));
            pavModel.setImgWidth(imgWidth);
            int imgHeight = Integer.parseInt(img.attr("height"));
            pavModel.setImgHeight(imgHeight);
            pavModelList.add(pavModel);
        }
        baseResult.setData(pavModelList);
        return baseResult;
    }

    public static BaseResult<List<PavModel>> moreVideoList(String html) {
        BaseResult<List<PavModel>> baseResult = new BaseResult<>();
        baseResult.setTotalPage(1);

        Document doc = Jsoup.parse(html);
        Elements items = doc.getElementsByClass("td-block-span4");
        List<PavModel> pavModelList = new ArrayList<>();
        for (Element element : items) {
            PavModel pavModel = new PavModel();
            Element a = element.selectFirst("a");
            String title = a.attr("title");
            pavModel.setTitle(title);
            String contentUrl = a.attr("href");
            pavModel.setContentUrl(contentUrl);
            Element img = element.selectFirst("img");
            String imgUrl = img.attr("src");
            int beginIndex = imgUrl.lastIndexOf("/");
            int endIndex = imgUrl.lastIndexOf("-");
            String bigImg = StringUtils.subString(imgUrl, 0, endIndex);
            if (TextUtils.isEmpty(bigImg)) {
                pavModel.setImgUrl(imgUrl);
            } else {
                pavModel.setImgUrl(bigImg + ".jpg");
            }
            String pId = StringUtils.subString(imgUrl, beginIndex + 1, endIndex);
            Logger.t(TAG).d(pId);
            pavModel.setpId(pId);

            int imgWidth = Integer.parseInt(img.attr("width"));
            pavModel.setImgWidth(imgWidth);
            int imgHeight = Integer.parseInt(img.attr("height"));
            pavModel.setImgHeight(imgHeight);
            pavModelList.add(pavModel);
        }
        baseResult.setData(pavModelList);
        return baseResult;
    }
}
