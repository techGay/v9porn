package com.u9porn.utils;

import android.net.Uri;

import com.danikula.videocache.file.FileNameGenerator;

/**
 * @author flymegoc
 * @date 2017/11/23
 * @describe
 */

public class VideoCacheFileNameGenerator implements FileNameGenerator {
    // Urls contain mutable parts (parameter 'sessionToken') and stable video's id (parameter 'videoId').
    // e. g. http://example.com?videoId=abcqaz&sessionToken=xyz987
    //http://185.38.13.159//mp43/243907.mp4?st=Jsr4cwsuIoZ5aDVLckLamA&e=1511443397
    //"http://185.38.13.130//mp43/238248.mp4?st=Uwgj0IbndG0N7J5qQx1CuA&e=1511443750"
    @Override
    public String generate(String url) {
        int startIndex = url.lastIndexOf("/");
        int endIndex = url.indexOf(".mp4");
        try {
            return url.substring(startIndex, endIndex) + ".temp";
        } catch (Exception e) {
            e.printStackTrace();
            return url + ".temp";
        }
    }
}
