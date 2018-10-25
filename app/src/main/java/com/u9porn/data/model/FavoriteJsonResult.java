package com.u9porn.data.model;

import java.util.List;

/**
 * 收藏视频
 * json返回数据
 *
 * @author flymegoc
 * @date 2017/12/21
 */

public class FavoriteJsonResult {
    //收藏成功
    public final static int FAVORITE_SUCCESS = 2;
    //收藏失败
    public final static int FAVORITE_FAIL = 1;

    //不能收藏自己的视频
    public final static int FAVORITE_YOURSELF = 3;
    /**
     * attributes : {"id":""}
     * addFavMessage : [{"attributes":{"id":""},"data":0}]
     * data :
     */

    private AttributesBean attributes;
    private String data;
    private List<AddFavMessageBean> addFavMessage;

    public AttributesBean getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributesBean attributes) {
        this.attributes = attributes;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<AddFavMessageBean> getAddFavMessage() {
        return addFavMessage;
    }

    public void setAddFavMessage(List<AddFavMessageBean> addFavMessage) {
        this.addFavMessage = addFavMessage;
    }

    public static class AttributesBean {
        /**
         * id :
         */

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class AddFavMessageBean {
        /**
         * attributes : {"id":""}
         * data : 0
         */

        private AttributesBeanX attributes;
        private int data;

        public AttributesBeanX getAttributes() {
            return attributes;
        }

        public void setAttributes(AttributesBeanX attributes) {
            this.attributes = attributes;
        }

        public int getData() {
            return data;
        }

        public void setData(int data) {
            this.data = data;
        }

        public static class AttributesBeanX {
            /**
             * id :
             */

            private String id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }

    @Override
    public String toString() {
        return "FavoriteJsonResult{" +
                "attributes=" + attributes +
                ", data='" + data + '\'' +
                ", addFavMessage=" + addFavMessage +
                '}';
    }
}
