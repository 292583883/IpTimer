package com.bowsky.utils;

import java.net.URLConnection;

/**
 * Created by xiaoyee on 7/31/15. In project yundong_android
 */
public enum FileTypeUtil {
    PNG(".png", "image/png"),
    JPG(".jpg", "image/jpeg"),
    MP4(".mp4", "video/mp4"),
    JPEG(".jpeg", "image/jpeg");
    //如果有其他的mime类型，则在此处继续添加即可

    /**
     * 后缀名
     */
    final String mSuffix;
    final String mMIME;

    FileTypeUtil(String suffix, String mime) {
        this.mSuffix = suffix;
        this.mMIME = mime;
    }

    public static String getSuffixFromUrl(String url) {
        String contentType = getMIMETypeFromUrl(url);
        return mimeMapingSuffix(contentType);
    }

    public static String getMIMETypeFromUrl(String url) {
        if (url==null || url.equals("")) {
            return "";
        }
        return URLConnection.guessContentTypeFromName(url);
    }

    /**
     * mime类型对应的后缀名
     */
    public static String mimeMapingSuffix(String mime) {
        FileTypeUtil[] fileTypes = FileTypeUtil.values();
        for (FileTypeUtil fileType : fileTypes) {
            if (fileType.mime().equals(mime)) {
                return fileType.suffix();
            }
        }
        return "";
    }

    public String mime() {
        return mMIME;
    }

    /**
     * 获取后缀名
     *
     * @return 指定类型的后缀名，如'.mp4'
     */
    public String suffix() {
        return this.mSuffix;
    }
}
