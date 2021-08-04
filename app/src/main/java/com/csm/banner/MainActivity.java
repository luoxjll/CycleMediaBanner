package com.csm.banner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CycleMediaBanner cycleMediaBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideBottomUiMenu();
        setContentView(R.layout.activity_main);

        cycleMediaBanner = findViewById(R.id.cycleMediaBanner);
        Button startButton = findViewById(R.id.id_start_btn);
        Button stopButton = findViewById(R.id.id_stop_btn);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test(getApplicationContext());
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cycleMediaBanner != null) {
                    cycleMediaBanner.stop();
                }
            }
        });


    }

    private void test(Context context) {

        String path1 = "android.resource://" + context.getPackageName() + "/" + R.raw.video1;
        String path2 = "android.resource://" + context.getPackageName() + "/" + R.raw.video2;

        List<MediaDetailBean> strings = new ArrayList<>();
        MediaDetailBean mediaDetailBean1 = new MediaDetailBean(MediaDetailBean.ITEM_TYPE_VIDEO, Uri.parse(path1));
        MediaDetailBean mediaDetailBean2 = new MediaDetailBean(MediaDetailBean.ITEM_TYPE_VIDEO, Uri.parse(path2));

        strings.add(mediaDetailBean1);
        strings.add(mediaDetailBean2);

        cycleMediaBanner.notifyWithDataSetChanged(strings);
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUiMenu() {

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}