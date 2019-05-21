package com.download.demo;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.apeng.permissions.EsayPermissions;
import com.apeng.permissions.OnPermission;
import com.apeng.permissions.Permission;
import com.camera.demo.R;
import java.util.List;
import qiu.niorgai.StatusBarCompat;

public class DownloadDemoActivity extends AppCompatActivity implements View.OnClickListener{


    private Button downloadBtn1, downloadBtn2, downloadBtn3;
    private Button cancelBtn1, cancelBtn2, cancelBtn3;
    private ProgressBar progress1, progress2, progress3;
    private String url1 = "http://imtt.dd.qq.com/16891/89E1C87A75EB3E1221F2CDE47A60824A.apk?fsname=com.snda.wifilocating_4.2.62_3192.apk";
    private String url2 = "http://192.168.31.169:8080/out/music.mp3";
    private String url3 = "http://192.168.31.169:8080/out/code.zip";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        requestPermission();


        downloadBtn1 = bindView(R.id.main_btn_down1);
        downloadBtn2 = bindView(R.id.main_btn_down2);
        downloadBtn3 = bindView(R.id.main_btn_down3);

        cancelBtn1 = bindView(R.id.main_btn_cancel1);
        cancelBtn2 = bindView(R.id.main_btn_cancel2);
        cancelBtn3 = bindView(R.id.main_btn_cancel3);

        progress1 = bindView(R.id.main_progress1);
        progress2 = bindView(R.id.main_progress2);
        progress3 = bindView(R.id.main_progress3);

        downloadBtn1.setOnClickListener(this);
        downloadBtn2.setOnClickListener(this);
        downloadBtn3.setOnClickListener(this);

        cancelBtn1.setOnClickListener(this);
        cancelBtn2.setOnClickListener(this);
        cancelBtn3.setOnClickListener(this);


    }


    private <T extends View> T bindView(@IdRes int id){
        View viewById = findViewById(id);
        return (T) viewById;
    }


    /**
     * 6.0 权限控制
     */
    public void requestPermission() {
        EsayPermissions.with(this)
                .constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
//                .permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            Toast.makeText(DownloadDemoActivity.this, "获取权限成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(DownloadDemoActivity.this, "取权限成功，部分权限未正常授予", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if (quick) {
                            Toast.makeText(DownloadDemoActivity.this, "被永久拒绝授权，请手动授予权限", Toast.LENGTH_LONG).show();
                            //如果是被永久拒绝就跳转到应用权限系统设置页面
                            EsayPermissions.gotoPermissionSettings(DownloadDemoActivity.this);
                        } else {
                            Toast.makeText(DownloadDemoActivity.this, "获取权限失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_down1:
                DownloadManager.getInstance().download(url1, new DownLoadObserver() {
                    @Override
                    public void onNext(DownloadInfo value) {
                        super.onNext(value);
                        progress1.setMax((int) value.getTotal());
                        progress1.setProgress((int) value.getProgress());
                    }

                    @Override
                    public void onComplete() {
                        if(downloadInfo != null){
                            Toast.makeText(DownloadDemoActivity.this,
                                    downloadInfo.getFileName() + "-DownloadComplete",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.main_btn_down2:
                DownloadManager.getInstance().download(url2, new DownLoadObserver() {
                    @Override
                    public void onNext(DownloadInfo value) {
                        super.onNext(value);
                        progress2.setMax((int) value.getTotal());
                        progress2.setProgress((int) value.getProgress());
                    }

                    @Override
                    public void onComplete() {
                        if(downloadInfo != null){
                            Toast.makeText(DownloadDemoActivity.this,
                                    downloadInfo.getFileName() + Uri.encode("下载完成"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.main_btn_down3:
                DownloadManager.getInstance().download(url3, new DownLoadObserver() {
                    @Override
                    public void onNext(DownloadInfo value) {
                        super.onNext(value);
                        progress3.setMax((int) value.getTotal());
                        progress3.setProgress((int) value.getProgress());
                    }

                    @Override
                    public void onComplete() {
                        if(downloadInfo != null){
                            Toast.makeText(DownloadDemoActivity.this,
                                    downloadInfo.getFileName() + "下载完成",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.main_btn_cancel1:
                DownloadManager.getInstance().cancel(url1);
                break;
            case R.id.main_btn_cancel2:
                DownloadManager.getInstance().cancel(url2);
                break;
            case R.id.main_btn_cancel3:
                DownloadManager.getInstance().cancel(url3);
                break;
        }

    }
}