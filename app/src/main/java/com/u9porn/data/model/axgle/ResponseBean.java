package com.u9porn.data.model.axgle;

import java.util.List;

class ResponseBean {

    private boolean has_more;
    private int total_collections;
    private int current_offset;
    private int limit;
    private List<CollectionsBean> collections;

    public boolean isHas_more() {
        return has_more;
    }

    public void setHas_more(boolean has_more) {
        this.has_more = has_more;
    }

    public int getTotal_collections() {
        return total_collections;
    }

    public void setTotal_collections(int total_collections) {
        this.total_collections = total_collections;
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

    public List<CollectionsBean> getCollections() {
        return collections;
    }

    public void setCollections(List<CollectionsBean> collections) {
        this.collections = collections;
    }
}
