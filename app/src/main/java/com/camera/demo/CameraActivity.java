package com.camera.demo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends AppCompatActivity implements RateArcView.RateViewClickListener {


    private CameraView cameraView;
    private Bitmap result;
    private ImageView DO_BACK;
    private RateArcView DO_PIC_OR_VIDEO;
    private ImageView DO_FLASH;
    private ImageView DO_REVARSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        cameraView = findViewById(R.id.camera);
        DO_BACK = (ImageView) findViewById(R.id.DO_BACK);
        DO_PIC_OR_VIDEO = (RateArcView) findViewById(R.id.DO_PIC_OR_VIDEO);
        DO_FLASH = (ImageView) findViewById(R.id.DO_FLASH);
        DO_REVARSE = (ImageView) findViewById(R.id.DO_REVARSE);

        cameraView.setPermissions(CameraKit.Constants.PERMISSIONS_STRICT);


        DO_PIC_OR_VIDEO.setRateViewClickListener(this);
        //前置摄像头 拍摄出来图片为倒的
        cameraView.setFacing(CameraKit.Constants.FACING_BACK);
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onImage(CameraKitImage cameraKitImage) {
               if(cameraView.isFacingFront()) {
                    CommonUtil.PIC = loadBitmap(cameraKitImage.getJpeg());
                } else {
                    CommonUtil.PIC = cameraKitImage.getBitmap();
                }

                //CommonUtil.PIC =  cameraKitImage.getBitmap();
                //TODO 跳转页面
                Intent intent = new Intent(CameraActivity.this, PreviewActivity.class);
                startActivity(intent);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {
                CommonUtil.VIDEO = cameraKitVideo.getVideoFile();
                //TODO 跳转页面
                Intent intent = new Intent(CameraActivity.this, VideoPreviewActivity.class);
                startActivity(intent);
            }
        });

        DO_REVARSE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (cameraView.getFacing() == CameraKit.Constants.FACING_BACK) {
                    cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
                } else {
                    cameraView.setFacing(CameraKit.Constants.FACING_BACK);
                }*/
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

        DO_BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * bitmap 180度旋转
     *
     * @param degree    角度
     * @param srcBitmap
     * @return
     */
    private Bitmap rotateBimap(float degree, Bitmap srcBitmap) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(degree);
        Bitmap bitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight()
                , matrix, true);
        return bitmap;
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
    public void onClick() {
        cameraView.captureImage();
    }

    @Override
    public void onLongClick() {
        cameraView.captureVideo();
    }

    @Override
    public void onLongClickEnd() {
        cameraView.stopVideo();
    }

    @Override
    public void onTimeOver() {
        cameraView.stopVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.stop();
        CommonUtil.PIC = null;
        CommonUtil.VIDEO = null;
    }

    /**
     * 从给定路径加载图片
     */
    public static Bitmap loadBitmap(String imgpath) {
        return BitmapFactory.decodeFile(imgpath);
    }


    /**
     * 从给定的路径加载图片，并指定是否自动旋转方向
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Bitmap loadBitmap(byte[] jpeg) {
        InputStream isBm = new ByteArrayInputStream(jpeg);
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(isBm);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null) {
            // 读取图片中相机方向信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            Log.i("tag", "读取角度-" + ori);
            // 计算旋转角度
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    digree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    digree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    digree = 180;
                    break;
                default:
                    digree = 0;
                    break;
            }
        }
        Bitmap bm = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
        if (digree != 0) {
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(digree);
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), m, true);
        }
        return bm;
    }

}