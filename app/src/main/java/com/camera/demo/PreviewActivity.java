package com.camera.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.apeng.permissions.EsayPermissions;
import com.apeng.permissions.OnPermission;
import com.apeng.permissions.Permission;
import com.wonderkiln.camerakit.CameraKitImage;

import java.util.List;

import qiu.niorgai.StatusBarCompat;

public class PreviewActivity extends AppCompatActivity {

    private ImageView DO_BACK;
    private ImageView img;
    private ImageView DO_OK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        img = (ImageView) findViewById(R.id.img);
        DO_BACK = (ImageView) findViewById(R.id.DO_BACK);
        DO_OK = (ImageView) findViewById(R.id.DO_OK);

        if (CommonUtil.PIC != null)
            img.setImageBitmap(CommonUtil.PIC);

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
        CommonUtil.PIC.recycle();
        CommonUtil.PIC = null ;
        super.onDestroy();
    }
}