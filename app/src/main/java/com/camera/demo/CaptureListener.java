package com.camera.demo;

/**
 * 拍照录像事件回调
 */

public interface CaptureListener {
    void takePictures();

    void recordShort(long time);

    void recordStart();

    void recordEnd(long time);

    void recordZoom(float zoom);

    void recordError();
}
