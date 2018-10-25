package com.u9porn.data.model.axgle;

import java.util.List;

public class AxgleResponse {

    private boolean has_more;
    private int total_videos;
    private int current_offset;
    private int limit;
    private List<AxgleVideo> videos;

    public boolean isHas_more() {
        return has_more;
    }

    public void setHas_more(boolean has_more) {
        this.has_more = has_more;
    }

    public int getTotal_videos() {
        return total_videos;
    }

    public void setTotal_videos(int total_videos) {
        this.total_videos = total_videos;
    }

    public int getCurrent_offset() {
        return current_offset;
    }

    public void setCurrent_offset(int current_offset) {
        this.current_offset = current_offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<AxgleVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<AxgleVideo> videos) {
        this.videos = videos;
    }
}
