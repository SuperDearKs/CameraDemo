package com.camera.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CommonUtil {


    //图片文件引用
    public static Bitmap PIC = null;
    public static File VIDEO = null;


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
