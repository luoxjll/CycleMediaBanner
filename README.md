# CycleMediaBanner

#### 介绍
基于viewpage2、videoview实现的视频、图片循环混合播放控件，预加载视频，无缝切换播放,
适用于基于视频图片的广告轮播。

#### 使用

```
String path1 = "android.resource://" + context.getPackageName() + "/" + R.raw.video1;
String path2 = "android.resource://" + context.getPackageName() + "/" + R.raw.video2;

List<MediaDetailBean> strings = new ArrayList<>();
MediaDetailBean mediaDetailBean1 = new MediaDetailBean(MediaDetailBean.ITEM_TYPE_VIDEO, Uri.parse(path1));
MediaDetailBean mediaDetailBean2 = new MediaDetailBean(MediaDetailBean.ITEM_TYPE_VIDEO, Uri.parse(path2));

strings.add(mediaDetailBean1);
strings.add(mediaDetailBean2);

cycleMediaBanner.notifyWithDataSetChanged(strings);
```