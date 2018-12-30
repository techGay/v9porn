package com.u9porn.data.model.pxgav;

/**
 *
 * @author flymegoc
 * @date 2018/2/1
 */

public class PavLoadMoreResponse {

    private String td_data;
    private String td_block_id;
    private boolean td_hide_prev;
    private boolean td_hide_next;

    public String getTd_data() {
        return td_data;
    }

    public void setTd_data(String td_data) {
        this.td_data = td_data;
    }

    public String getTd_block_id() {
        return td_block_id;
    }

    public void setTd_block_id(String td_block_id) {
        this.td_block_id = td_block_id;
    }

    public boolean isTd_hide_prev() {
        return td_hide_prev;
    }

    public void setTd_hide_prev(boolean td_hide_prev) {
        this.td_hide_prev = td_hide_prev;
    }

    public boolean isTd_hide_next() {
        return td_hide_next;
    }

    public void setTd_hide_next(boolean td_hide_next) {
        this.td_hide_next = td_hide_next;
    }
}
