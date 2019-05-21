package com.camera.demo;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;

import qiu.niorgai.StatusBarCompat;

public class VideoPreviewActivity extends AppCompatActivity {

    private ImageView DO_BACK;
    private VideoView video;
    private ImageView DO_OK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        video = (VideoView) findViewById(R.id.video);
        DO_BACK = (ImageView) findViewById(R.id.DO_BACK);
        DO_OK = (ImageView) findViewById(R.id.DO_OK);

        video.setVideoPath(CommonUtil.VIDEO.getPath());
        video.start();


        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            mp.start();
                                            mp.setLooping(true);
                                        }
                                    });


                DO_BACK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        DO_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    protected void onDestroy() {
        CommonUtil.VIDEO = null;
        super.onDestroy();
    }
}