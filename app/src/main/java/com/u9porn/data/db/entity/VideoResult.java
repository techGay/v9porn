package com.u9porn.data.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * 视频信息
 *
 * @author flymegoc
 * @date 2017/12/20
 */
@Entity
public class VideoResult implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 游客超过每天观看次数
     */
    @Transient
    public static final Long OUT_OF_WATCH_TIMES = -1L;

    /**
     * 视频不存在,可能已经被删除或者被举报为不良内容!
     */
    @Transient
    public static final Long VIDEO_NOT_EXIST_OR_DELETE = -2L;

    @Id
    public Long id;
    private String videoUrl;

    @Index
    private String videoId;

    /**
     * 同authorId，形式不一样，变换过的用于查看用户其他视频
     */
    private String ownerId;

    /**
     * ownerId，形式不一样，原始id，用于收藏视频
     */
    private String authorId;

    private String thumbImgUrl;
    private String videoName;
    private String ownerName;
    private String addDate;
    private String userOtherInfo;

    @Generated(hash = 1158187111)
    public VideoResult(Long id, String videoUrl, String videoId, String ownerId, String authorId,
            String thumbImgUrl, String videoName, String ownerName, String addDate,
            String userOtherInfo) {
        this.id = id;
        this.videoUrl = videoUrl;
        this.videoId = videoId;
        this.ownerId = ownerId;
        this.authorId = authorId;
        this.thumbImgUrl = thumbImgUrl;
        this.videoName = videoName;
        this.ownerName = ownerName;
        this.addDate = addDate;
        this.userOtherInfo = userOtherInfo;
    }

    @Generated(hash = 121136283)
    public VideoResult() {
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getThumbImgUrl() {
        return thumbImgUrl;
    }

    public void setThumbImgUrl(String thumbImgUrl) {
        this.thumbImgUrl = thumbImgUrl;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public String getUserOtherInfo() {
        return userOtherInfo;
    }

    public void setUserOtherInfo(String userOtherInfo) {
        this.userOtherInfo = userOtherInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VideoResult that = (VideoResult) o;

        return videoId != null ? videoId.equals(that.videoId) : that.videoId == null;
    }

    @Override
    public int hashCode() {
        return videoId != null ? videoId.hashCode() : 0;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVideoName() {
        return this.videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    @Override
    public String toString() {
        return "VideoResult{" +
                "id=" + id +
                ", videoUrl='" + videoUrl + '\'' +
                ", videoId='" + videoId + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", authorId='" + authorId + '\'' +
                ", thumbImgUrl='" + thumbImgUrl + '\'' +
                ", videoName='" + videoName + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", addDate='" + addDate + '\'' +
                ", userOtherInfo='" + userOtherInfo + '\'' +
                '}';
    }
}
