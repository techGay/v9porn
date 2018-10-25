package com.u9porn.eventbus;

public class UrlRedirectEvent {
    private String oldUrl;
    private String newUrl;
    private String header;

    public UrlRedirectEvent(String oldUrl, String newUrl, String header) {
        this.oldUrl = oldUrl;
        this.newUrl = newUrl;
        this.header = header;
    }

    public String getOldUrl() {
        return oldUrl;
    }

    public void setOldUrl(String oldUrl) {
        this.oldUrl = oldUrl;
    }

    public String getNewUrl() {
        return newUrl;
    }

    public void setNewUrl(String newUrl) {
        this.newUrl = newUrl;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
