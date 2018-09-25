package com.u9porn.data.db.entity;

import android.text.TextUtils;

import com.u9porn.data.DataManager;
import com.u9porn.utils.SDCardUtils;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

import java.io.Serializable;
import java.util.Date;

/**
 * 视频
 *
 * @author flymegoc
 * @date 2017/11/14
 * @describe
 */
@Entity
public class V9PornItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    public Long id;
    @Index(unique = true)
    private String viewKey;
    private String title;
    private String imgUrl;
    private String duration;
    private String info;
    private long videoResultId;
    @ToOne(joinProperty = "videoResultId")
    private VideoResult videoResult;
    private int downloadId;

    private int progress;
    private long speed;
    private int soFarBytes;
    private int totalFarBytes;
    private int status;
    private Date addDownloadDate;
    private Date finishedDownloadDate;
    private Date viewHistoryDate;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 272415832)
    private transient V9PornItemDao myDao;


    @Generated(hash = 247502747)
    public V9PornItem(Long id, String viewKey, String title, String imgUrl, String duration, String info, long videoResultId, int downloadId, int progress, long speed,
                      int soFarBytes, int totalFarBytes, int status, Date addDownloadDate, Date finishedDownloadDate, Date viewHistoryDate) {
        this.id = id;
        this.viewKey = viewKey;
        this.title = title;
        this.imgUrl = imgUrl;
        this.duration = duration;
        this.info = info;
        this.videoResultId = videoResultId;
        this.downloadId = downloadId;
        this.progress = progress;
        this.speed = speed;
        this.soFarBytes = soFarBytes;
        this.totalFarBytes = totalFarBytes;
        this.status = status;
        this.addDownloadDate = addDownloadDate;
        this.finishedDownloadDate = finishedDownloadDate;
        this.viewHistoryDate = viewHistoryDate;
    }

    @Generated(hash = 2005431321)
    public V9PornItem() {
    }

    @Generated(hash = 430646239)
    private transient Long videoResult__resolvedKey;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        V9PornItem that = (V9PornItem) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getDownLoadPath(String customDownloadVideoDirPath) {
        //先读取自定义目录
        if (!TextUtils.isEmpty(customDownloadVideoDirPath)) {
            return customDownloadVideoDirPath + getTitle() + getViewKey() + ".mp4";
        }

        return SDCardUtils.DOWNLOAD_VIDEO_PATH + getTitle() + getViewKey() + ".mp4";
    }

    public Date getAddDownloadDate() {
        return addDownloadDate;
    }

    public void setAddDownloadDate(Date addDownloadDate) {
        this.addDownloadDate = addDownloadDate;
    }

    public Date getFinishedDownloadDate() {
        return finishedDownloadDate;
    }

    public void setFinishedDownloadDate(Date finishedDownloadDate) {
        this.finishedDownloadDate = finishedDownloadDate;
    }

    public Date getViewHistoryDate() {
        return viewHistoryDate;
    }

    public void setViewHistoryDate(Date viewHistoryDate) {
        this.viewHistoryDate = viewHistoryDate;
    }

    public String getTitleWithDuration() {
        return getTitle() + "  (" + getDuration() + ")";
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getViewKey() {
        return this.viewKey;
    }

    public void setViewKey(String viewKey) {
        this.viewKey = viewKey;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getDownloadId() {
        return this.downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getSpeed() {
        return this.speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public int getSoFarBytes() {
        return this.soFarBytes;
    }

    public void setSoFarBytes(int soFarBytes) {
        this.soFarBytes = soFarBytes;
    }

    public int getTotalFarBytes() {
        return this.totalFarBytes;
    }

    public void setTotalFarBytes(int totalFarBytes) {
        this.totalFarBytes = totalFarBytes;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 1066672592)
    public VideoResult getVideoResult() {
        long __key = this.videoResultId;
        if (videoResult__resolvedKey == null || !videoResult__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            VideoResultDao targetDao = daoSession.getVideoResultDao();
            VideoResult videoResultNew = targetDao.load(__key);
            synchronized (this) {
                videoResult = videoResultNew;
                videoResult__resolvedKey = __key;
            }
        }
        return videoResult;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1756630021)
    public void setVideoResult(@NotNull VideoResult videoResult) {
        if (videoResult == null) {
            throw new DaoException("To-one property 'videoResultId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.videoResult = videoResult;
            videoResultId = videoResult.getId();
            videoResult__resolvedKey = videoResultId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public long getVideoResultId() {
        return this.videoResultId;
    }

    public void setVideoResultId(long videoResultId) {
        this.videoResultId = videoResultId;
    }

    @Override
    public String toString() {
        return "V9PornItem{" +
                "id=" + id +
                ", viewKey='" + viewKey + '\'' +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", duration='" + duration + '\'' +
                ", info='" + info + '\'' +
                ", videoResultId=" + videoResultId +
                ", videoResult=" + videoResult +
                ", downloadId=" + downloadId +
                ", progress=" + progress +
                ", speed=" + speed +
                ", soFarBytes=" + soFarBytes +
                ", totalFarBytes=" + totalFarBytes +
                ", status=" + status +
                ", addDownloadDate=" + addDownloadDate +
                ", finishedDownloadDate=" + finishedDownloadDate +
                ", viewHistoryDate=" + viewHistoryDate +
                ", daoSession=" + daoSession +
                ", myDao=" + myDao +
                ", videoResult__resolvedKey=" + videoResult__resolvedKey +
                '}';
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1024181619)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getV9PornItemDao() : null;
    }
}
