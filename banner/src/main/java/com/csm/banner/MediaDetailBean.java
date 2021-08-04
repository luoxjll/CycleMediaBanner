package com.csm.banner;

import android.net.Uri;

/**
 * Copyright (C), 2020-2021
 * Description:
 *
 * @author xj.luo
 * Email: xj_luo@foxmail.com
 */
public class MediaDetailBean {

    //图片 item
    public static final int ITEM_TYPE_IMAGE = 0x10;
    // 视频item
    public static final int ITEM_TYPE_VIDEO = 0x11;

    private int type;
    private Uri uri;

    public MediaDetailBean(int type, Uri uri) {
        this.type = type;
        this.uri = uri;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
