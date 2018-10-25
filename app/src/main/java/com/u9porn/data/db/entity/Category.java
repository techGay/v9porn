package com.u9porn.data.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;

/**
 * 类别
 *
 * @author flymegoc
 * @date 2018/1/19
 */
@Entity
public class Category implements Serializable {
    public static final String[] CATEGORY_DEFAULT_91PORN_VALUE = {"index", "watch", "hot", "rp", "long", "md", "tf", "mf", "rf", "top", "top1", "hd"};
    public static final String[] CATEGORY_DEFAULT_91PORN_NAME = {"主页", "最近更新", "当前最热", "最近得分", "10分钟以上", "本月讨论", "本月收藏", "收藏最多", "最近加精", "本月最热", "上月最热", "高清(会员)"};
    public static final String[] CATEGORY_DEFAULT_91PORN_FORUM_VALUE = {"index", "17", "19", "4", "21", "33", "34"};
    public static final String[] CATEGORY_DEFAULT_91PORN_FORUM_NAME = {"主页", "自拍达人原创区", "自拍达人原创申请", "原创自拍区", "我爱我妻", "X趣分享", "两X健康"};
    public static final String[] CATEGORY_DEFAULT_MEI_ZI_TU_VALUE = {"index", "hot", "best", "xinggan", "japan", "taiwan", "mm"};
    public static final String[] CATEGORY_DEFAULT_MEI_ZI_TU_NAME = {"主页", "最热", "推荐", "性感妹子", "日本妹子", "台湾妹子", "清纯妹子"};
    public static final String[] CATEGORY_DEFAULT_PIG_AV_VALUE = {"index", "熱門", "長片", "每日", "最新", "日韓", "精選"};
    public static final String[] CATEGORY_DEFAULT_PIG_AV_NAME = {"主页", "热门", "长片", "每日", "最新", "日韩", "精选"};
    public static final String[] CATEGORY_DEFAULT_99_MM_VALUE = {"index", "meitui", "xinggan", "qingchun", "hot"};
    public static final String[] CATEGORY_DEFAULT_99_MM_NAME = {"主页", "靓丽腿模", "性感美女", "清纯美女", "美女推荐"};
    public static final String[] CATEGORY_DEFAULT_HUA_BAN_NAME = {"造型美妆", "美食", "旅行", "手工布艺", "健身舞蹈", "儿童", "宠物", "美图", "明星", "美女", "礼物", "极客", "动漫", "建筑设计", "人文艺术", "数据图", "游戏", "汽车摩托", "电影图书", "生活百科", "教育", "运动", "搞笑"};

    public static final String[] CATEGORY_DEFAULT_AXGLE_VALUE={"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24"};
    public static final String[] CATEGORY_DEFAULT_AXGLE_NAME={"AV女優","日本AV","無修正","少女","素人","肛X","巨乳","Cosplay","女子校生","人妻","熟女","SM","中國","香港","日本","韓国","台湾","亞洲","金髪洋物","3D","VR","偶像","映画・電影","Anime"};

    public static final int TYPE_91PORN = 1;
    public static final int TYPE_91PORN_FORUM = 2;
    public static final int TYPE_MEI_ZI_TU = 3;
    public static final int TYPE_PIG_AV = 4;
    public static final int TYPE_99_MM = 5;
    public static final int TYPE_HUA_BAN = 6;
    public static final int TYPE_AXGLE=7;
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    public Long id;

    private int categoryType;

    private String categoryName;

    private String categoryValue;

    private String categoryUrl;

    private Integer sortId;

    private boolean isShow;

    @Generated(hash = 895463876)
    public Category(Long id, int categoryType, String categoryName,
                    String categoryValue, String categoryUrl, Integer sortId,
                    boolean isShow) {
        this.id = id;
        this.categoryType = categoryType;
        this.categoryName = categoryName;
        this.categoryValue = categoryValue;
        this.categoryUrl = categoryUrl;
        this.sortId = sortId;
        this.isShow = isShow;
    }

    @Generated(hash = 1150634039)
    public Category() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCategoryType() {
        return this.categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryValue() {
        return this.categoryValue;
    }

    public void setCategoryValue(String categoryValue) {
        this.categoryValue = categoryValue;
    }

    public String getCategoryUrl() {
        return this.categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }

    public Integer getSortId() {
        return this.sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    public boolean getIsShow() {
        return this.isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }
}
