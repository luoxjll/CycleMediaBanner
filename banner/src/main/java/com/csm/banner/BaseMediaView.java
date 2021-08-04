package com.csm.banner;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * Copyright (C), 2020-2021
 * Description:
 *
 * @author xj.luo
 * Email: xj_luo@foxmail.com
 */
public abstract class BaseMediaView extends ConstraintLayout {

    protected OnMediaBannerCallback mediaBannerCallback;

    public BaseMediaView(@NonNull Context context) {
        super(context);
    }

    public BaseMediaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseMediaView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void start();

    public abstract void initDataSource(Uri uri, boolean loop);

    public void setOnMediaViewCallback(OnMediaBannerCallback callback) {
        this.mediaBannerCallback = callback;
    }
}
