package com.u9porn.data.model.pxgav;

import java.util.List;

/**pa视频解析json结果
 * @author flymegoc
 * @date 2018/1/28
 */

public class PavVideoParserJsonResult {

    private transient List<PavModel> pavModelList;

    /**
     * aspectratio : 8:5
     * width : 100%
     * skin : glow
     * fallback : false
     * primary : html5
     * image : https://img.pigav.com/2015/10/iii723.jpg
     * file : https://v7.wuso.tv/wp-content/uploads/2016/08/iii723.mp4
     */

    private String aspectratio;
    private String width;
    private String skin;
    private boolean fallback;
    private String primary;
    private String image;
    private String file;

    public String getAspectratio() {
        return aspectratio;
    }

    public void setAspectratio(String aspectratio) {
        this.aspectratio = aspectratio;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public boolean isFallback() {
        return fallback;
    }

    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<PavModel> getPavModelList() {
        return pavModelList;
    }

    public void setPavModelList(List<PavModel> pavModelList) {
        this.pavModelList = pavModelList;
    }
}
