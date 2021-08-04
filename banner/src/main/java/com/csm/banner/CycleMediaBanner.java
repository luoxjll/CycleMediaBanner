package com.csm.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2020-2021
 * Description: 图片视频混合轮播控件，无缝循环播放
 *
 * @author xj.luo
 * Email: xj_luo@foxmail.com
 */
public class CycleMediaBanner extends ConstraintLayout implements OnMediaBannerCallback {

    // 图片是否铺满
    // 视频显示是否铺满
    private ViewPager2 viewPager2;
    private CycleMediaAdapter cycleMediaAdapter;
    private List<MediaDetailBean> mediaDetailBeans = new ArrayList<>();

    public CycleMediaBanner(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public CycleMediaBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CycleMediaBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        viewPager2 = new ViewPager2(context);
        viewPager2.setId(ViewCompat.generateViewId());
        viewPager2.setUserInputEnabled(false);

        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager2.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        viewPager2.setOffscreenPageLimit(1);
        this.addView(viewPager2);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.i("MEIDA", "onPageSelected " + position);

                RecyclerView recyclerView = (RecyclerView) viewPager2.getChildAt(0);

                BaseMediaViewHolder viewHolderForLayoutPosition =
                        (BaseMediaViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                Log.i("MEIDA", "onPageSelected child view is  " + viewHolderForLayoutPosition);

                if (viewHolderForLayoutPosition != null) {
                    viewHolderForLayoutPosition.start();
                }

            }

        });

        cycleMediaAdapter = new CycleMediaAdapter(mediaDetailBeans);
        viewPager2.setAdapter(cycleMediaAdapter);
        cycleMediaAdapter.setMediaBannerCallback(this);

    }

    public void notifyWithDataSetChanged(List<MediaDetailBean> uris) {
        if (uris != null) {

            if (cycleMediaAdapter != null) {
                mediaDetailBeans.clear();
                mediaDetailBeans.addAll(uris);
                cycleMediaAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 停止
     */
    public void stop() {
        if (mediaDetailBeans != null) {
            mediaDetailBeans.clear();
        }
        if (cycleMediaAdapter != null){
            cycleMediaAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCompletion() {
        int currentItem = viewPager2.getCurrentItem();
        viewPager2.setCurrentItem(++currentItem, false);
    }

    @Override
    public void onError() {

    }

    private static class CycleMediaAdapter extends RecyclerView.Adapter<BaseMediaViewHolder> {

        private List<MediaDetailBean> mediaDetailBeans;
        private OnMediaBannerCallback mediaBannerCallback;

        public CycleMediaAdapter(List<MediaDetailBean> ms) {
            this.mediaDetailBeans = ms;
        }

        public void setMediaBannerCallback(OnMediaBannerCallback mediaBannerCallback) {
            this.mediaBannerCallback = mediaBannerCallback;
        }

        @NonNull
        @Override
        public BaseMediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == MediaDetailBean.ITEM_TYPE_VIDEO) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_holder_view,
                        parent, false);
                CycleMediaVideoViewHolder cycleMediaVideoViewHolder = new CycleMediaVideoViewHolder(view);
                cycleMediaVideoViewHolder.mediaView.setOnMediaViewCallback(new OnMediaBannerCallback() {
                    @Override
                    public void onCompletion() {
                        if (mediaBannerCallback != null) {
                            mediaBannerCallback.onCompletion();
                        }
                    }

                    @Override
                    public void onError() {
                        if (mediaBannerCallback != null) {
                            mediaBannerCallback.onError();
                        }
                    }
                });
                return cycleMediaVideoViewHolder;
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_holder_view,
                        parent, false);
                CycleMediaImageViewHolder cycleMediaImageViewHolder = new CycleMediaImageViewHolder(view);
                cycleMediaImageViewHolder.mediaView.setOnMediaViewCallback(new OnMediaBannerCallback() {
                    @Override
                    public void onCompletion() {
                        if (mediaBannerCallback != null) {
                            mediaBannerCallback.onCompletion();
                        }
                    }

                    @Override
                    public void onError() {
                        if (mediaBannerCallback != null) {
                            mediaBannerCallback.onError();
                        }
                    }
                });
                return cycleMediaImageViewHolder;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull BaseMediaViewHolder holder, int position) {

            boolean loop = false;
            if (mediaDetailBeans.size() <= 1) {
                loop = true;
            }
            int index = position % mediaDetailBeans.size();
            holder.mediaView.initDataSource(mediaDetailBeans.get(index).getUri(), loop);
        }


        @Override
        public int getItemCount() {
            if (mediaDetailBeans != null) {
                if (mediaDetailBeans.size() > 0) {
                    return Integer.MAX_VALUE;
                }
            }
            return 0;
        }


        @Override
        public int getItemViewType(int position) {

            int index = position % mediaDetailBeans.size();

            MediaDetailBean mediaDetailBean = mediaDetailBeans.get(index);
            return mediaDetailBean.getType();
        }
    }


    private abstract static class BaseMediaViewHolder extends RecyclerView.ViewHolder {

        protected BaseMediaView mediaView;

        public BaseMediaViewHolder(@NonNull View itemView) {
            super(itemView);

            mediaView = itemLayout(itemView);
        }

        /**
         * item layout
         *
         * @param itemView item view
         * @return BaseMediaView
         */
        public abstract BaseMediaView itemLayout(View itemView);

        public void start() {
            if (mediaView != null) {
                mediaView.start();
            }
        }
    }

    /**
     * image view holder
     */
    private static class CycleMediaImageViewHolder extends BaseMediaViewHolder {

        public CycleMediaImageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public BaseMediaView itemLayout(View itemView) {
            return itemView.findViewById(R.id.id_media_image_view);
        }
    }

    /**
     * video view holder
     */
    private static class CycleMediaVideoViewHolder extends BaseMediaViewHolder {

        public CycleMediaVideoViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public BaseMediaView itemLayout(View itemView) {
            return itemView.findViewById(R.id.id_media_video_view);
        }
    }
}
