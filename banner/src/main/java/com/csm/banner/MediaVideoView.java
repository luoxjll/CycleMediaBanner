package com.csm.banner;

import android.content.Context;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * Copyright (C), 2020-2021
 * Description: 显示图片和视频的自定义view
 *
 * @author xj.luo
 * Email: xj_luo@foxmail.com
 */
public class MediaVideoView extends BaseMediaView implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnInfoListener {

    private static final int START_PLAY_ACTION = 0x30;
    private static final int VIDEO_THUMB_COMPLETE = 0x31;
    private static final int VIDEO_PLAY_COMPLETE = 0x32;

    private VideoView mVideoView;
    private Uri uri;

    // 资源是否准备好
    private boolean prepared = false;
    private boolean isPreparing = false;

    private MediaVideoHandler videoHandler;
    private boolean loop = false;

    private static class MediaVideoHandler extends Handler {

        private final WeakReference<MediaVideoView> mediaVideoViewWeakReference;

        public MediaVideoHandler(@NonNull Looper looper, MediaVideoView mediaVideoView) {
            super(looper);
            this.mediaVideoViewWeakReference = new WeakReference<>(mediaVideoView);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg.what == START_PLAY_ACTION) {
                if (mediaVideoViewWeakReference.get().prepared) {
                    this.removeMessages(START_PLAY_ACTION);
                    if (mediaVideoViewWeakReference.get().mVideoView != null) {
                        try {
                            // mediaVideoViewWeakReference.get().mVideoView.setBackgroundColor(Color.TRANSPARENT);
                            mediaVideoViewWeakReference.get().mVideoView.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("MEDIA", " start play " + mediaVideoViewWeakReference.get().uri);
                    }
                } else {
                    Log.i("MEDIA", "video is not prepared." + mediaVideoViewWeakReference.get().uri);
                    if (mediaVideoViewWeakReference.get().isPreparing) {
                        Log.i("MEDIA", "video isPreparing." + mediaVideoViewWeakReference.get().uri);
                    } else {
                        mediaVideoViewWeakReference.get().initDataSource(mediaVideoViewWeakReference.get().uri,
                                mediaVideoViewWeakReference.get().loop);
                    }

                    this.sendEmptyMessage(START_PLAY_ACTION);

                }
            } else if (msg.what == VIDEO_THUMB_COMPLETE) {
//                Bitmap bitmap = (Bitmap) msg.obj;
//                Drawable defaultDrawable = new BitmapDrawable(mediaVideoViewWeakReference.get().getContext().getResources(), bitmap);
//                mediaVideoViewWeakReference.get().mVideoView.setBackground(defaultDrawable);

            } else if (msg.what == VIDEO_PLAY_COMPLETE) {
                if (mediaVideoViewWeakReference.get().mediaBannerCallback != null) {
                    mediaVideoViewWeakReference.get().mediaBannerCallback.onCompletion();
                }
            }
        }
    }


    public MediaVideoView(Context context) {
        super(context);
        initView(context);
    }

    public MediaVideoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MediaVideoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //layoutParams.gravity = Gravity.CENTER;

        mVideoView = new VideoView(context);
        mVideoView.setLayoutParams(layoutParams);
        this.addView(mVideoView);

        videoHandler = new MediaVideoHandler(Looper.getMainLooper(), this);
    }

    @Override
    public void initDataSource(Uri uri, boolean loop) {
        this.loop = loop;
        if (uri != null) {
            this.uri = uri;

//            ThreadPool.getInstance().post(() -> {
//                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                retriever.setDataSource(urlString);
//                Bitmap thumbnail = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//                videoHandler.sendMessage(videoHandler.obtainMessage(VIDEO_THUMB_COMPLETE, thumbnail));
//            });

            isPreparing = true;
            mVideoView.setVideoURI(uri);
            mVideoView.setOnPreparedListener(this);
            mVideoView.setOnErrorListener(this);
            mVideoView.setOnInfoListener(this);
            mVideoView.setOnCompletionListener(this);
        }
    }

    @Override
    public void start() {

        if (videoHandler != null) {
            videoHandler.sendEmptyMessage(START_PLAY_ACTION);
        }
    }

    /**
     * 在任何状态下释放媒体播放器
     */
    public void suspend() {
        if (mVideoView != null) {
            mVideoView.suspend();
        }
    }

    /**
     *
     */
    public void stopPlayback() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    /**
     * 如果videoview 不可见，不会调用此方法，可见时，则会调用此方法
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        prepared = true;
        isPreparing = false;
        if (loop) {
            mp.setLooping(true);
        }
        Log.d("MEDIA", "video is onPrepared，wait start play " + uri.toString());
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {


//        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
//            Log.d("MEDIA", "video onInfo " + "MEDIA_INFO_VIDEO_RENDERING_START");
//            mVideoView.setBackgroundColor(Color.TRANSPARENT);
//        }

        return true;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        if (videoHandler != null) {
            videoHandler.removeMessages(START_PLAY_ACTION);
        }

        if (mediaBannerCallback != null) {
            mediaBannerCallback.onError();
        }
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i("MEDIA", "video view is play completion.");

        if (!loop) {
            mVideoView.suspend();

            isPreparing = false;
            prepared = false;

            if (videoHandler != null) {
                videoHandler.sendEmptyMessageDelayed(VIDEO_PLAY_COMPLETE, 200);
            }
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d("MEDIA", "MEDIA Video View onAttachedToWindow ");
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        Log.d("MEDIA", "MEDIA Video View onWindowFocusChanged " + hasWindowFocus);

    }

    /**
     * 获取屏幕宽度和高度，单位为px
     *
     * @param context
     * @return
     */
    public static Point getScreenMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        return new Point(w_screen, h_screen);
    }
}
