package com.camera.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestActivity extends AppCompatActivity implements CaptureListener, TypeListener, CameraKitEventListener {


    private CameraView cameraView;
    private ImageView DO_FLASH;
    private ImageView DO_REVARSE;
    private CaptureLayout capture;
    private VideoView video;
    private ImageView img;
    private RelativeLayout preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        cameraView = (CameraView) findViewById(R.id.camera);
        DO_FLASH = (ImageView) findViewById(R.id.DO_FLASH);
        DO_REVARSE = (ImageView) findViewById(R.id.DO_REVARSE);
        preview = (RelativeLayout) findViewById(R.id.rl_preview);

        capture = (CaptureLayout) findViewById(R.id.capture_layout);
        capture.setCaptureLisenter(this);
        capture.setTypeLisenter(this);
        capture.setDuration(15 * 1000);
        capture.setMinDuration(1 * 1000);
        capture.setIconSrc(R.drawable.ic_back, R.drawable.ic_photo);


        video = (VideoView) findViewById(R.id.video);
        img = (ImageView) findViewById(R.id.img);


        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });


        //前置摄像头 拍摄出来图片角度有问题
        cameraView.setFacing(CameraKit.Constants.FACING_BACK);
        cameraView.addCameraKitListener(this);

        DO_REVARSE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.toggleFacing();
            }
        });

        DO_FLASH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraView.getFlash() == CameraKit.Constants.FLASH_TORCH) {
                    cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
                    DO_FLASH.setImageResource(R.drawable.a0_close);
                } else {
                    cameraView.setFlash(CameraKit.Constants.FLASH_TORCH);
                    DO_FLASH.setImageResource(R.drawable.a0_open);
                }
            }
        });

        capture.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });

        capture.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(TestActivity.this, "RightClickListener", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.stop();
    }


    @Override
    public void takePictures() {
        cameraView.captureImage();
    }

    @Override
    public void recordShort(long time) {
        capture.setTextWithAnimation("录制时间过短");
        cameraView.stopVideo();
        capture.resetCaptureLayout();
    }

    @Override
    public void recordStart() {
        cameraView.captureVideo();
    }

    @Override
    public void recordEnd(long time) {
        cameraView.stopVideo();
        capture.resetCaptureLayout();
    }

    @Override
    public void recordZoom(float zoom) {

    }

    @Override
    public void recordError() {
        Toast.makeText(TestActivity.this, "录制失败", Toast.LENGTH_LONG).show();
        capture.resetCaptureLayout();
    }

    @Override
    public void cancel() {
        cameraView.start();
        DO_FLASH.setVisibility(View.VISIBLE);
        DO_REVARSE.setVisibility(View.VISIBLE);
        preview.setVisibility(View.GONE);
        capture.resetCaptureLayout();
        img.setVisibility(View.INVISIBLE);
        video.setVisibility(View.INVISIBLE);
        if(video!=null && video.isPlaying()){
            video.pause();
        }
    }

    @Override
    public void confirm() {
//        DO_FLASH.setVisibility(View.VISIBLE);
//        DO_REVARSE.setVisibility(View.VISIBLE);
//        preview.setVisibility(View.GONE);
//        capture.resetCaptureLayout();
//        img.setVisibility(View.INVISIBLE);
//        video.setVisibility(View.INVISIBLE);
//        if(video!=null && video.isPlaying()){
//            video.pause();
//        }
        Toast.makeText(TestActivity.this, "确认OK", Toast.LENGTH_LONG).show();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onImage(CameraKitImage cameraKitImage) {
        img.setVisibility(View.VISIBLE);
        preview.setVisibility(View.VISIBLE);
        DO_FLASH.setVisibility(View.GONE);
        DO_REVARSE.setVisibility(View.GONE);
        if (cameraView.isFacingFront()) {
            img.setImageBitmap(CommonUtil.loadBitmap(cameraKitImage.getJpeg()));
        } else {
            img.setImageBitmap(cameraKitImage.getBitmap());
        }
        cameraView.stop();
    }

    @Override
    public void onVideo(CameraKitVideo cameraKitVideo) {
        cameraView.stop();
        video.setVisibility(View.VISIBLE);
        DO_FLASH.setVisibility(View.GONE);
        DO_REVARSE.setVisibility(View.GONE);
        preview.setVisibility(View.VISIBLE);
        video.setVideoPath(cameraKitVideo.getVideoFile().getPath());
        cameraView.stop();
        cameraView.start();
    }

    @Override
    public void onEvent(CameraKitEvent cameraKitEvent) {

    }

    @Override
    public void onError(CameraKitError cameraKitError) {

    }
}