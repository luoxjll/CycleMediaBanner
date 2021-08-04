package com.csm.banner;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * Copyright (C), 2020-2021
 * Description:
 *
 * @author xj.luo
 * Email: xj_luo@foxmail.com
 */
public class MediaImageView extends BaseMediaView {

    private static final int COMPLETION_ACTION = 0x20;
    private static final int ERROR_ACTION = 0x21;
    private ImageView mImageView;
    private MediaImageHandler mHandler;
    private boolean loop = false;

    private static class MediaImageHandler extends Handler {
        private WeakReference<MediaImageView> mediaImageViewWeakReference;


        public MediaImageHandler(@NonNull Looper looper, MediaImageView mediaImageView) {
            super(looper);
            mediaImageViewWeakReference = new WeakReference<>(mediaImageView);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            OnMediaBannerCallback mediaBannerCallback = mediaImageViewWeakReference.get().mediaBannerCallback;

            if (msg.what == COMPLETION_ACTION) {

                this.removeMessages(COMPLETION_ACTION);
                this.removeMessages(ERROR_ACTION);
                this.sendEmptyMessage(ERROR_ACTION);

                if (mediaBannerCallback != null) {
                    mediaBannerCallback.onCompletion();
                }

            } else if (msg.what == ERROR_ACTION) {

                if (mediaBannerCallback != null) {
                    mediaBannerCallback.onError();
                }
            }
        }
    }

    public MediaImageView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public MediaImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MediaImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    public void start() {
        Log.i("MEIDA", "image media is start.");
        if (!loop) {
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(COMPLETION_ACTION, 10 * 1000);
            }
        }
    }

    private void initView(Context context) {
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        mImageView = new ImageView(context);
        mImageView.setLayoutParams(layoutParams);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        this.addView(mImageView);

        mHandler = new MediaImageHandler(Looper.getMainLooper(), this);
    }

    @Override
    public void initDataSource(Uri uri, boolean loop) {
        this.loop = loop;
        if (uri != null) {
            mImageView.setImageURI(uri);
        }
    }
}
