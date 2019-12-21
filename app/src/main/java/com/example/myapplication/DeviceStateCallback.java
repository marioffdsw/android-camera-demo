package com.example.myapplication;

import android.hardware.camera2.CameraDevice;
import android.support.annotation.NonNull;

public class DeviceStateCallback extends CameraDevice.StateCallback {

    // candidate parameters

    @Override
    public void onOpened(@NonNull CameraDevice camera) {

    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {

    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {

    }

    @Override
    public void onClosed(@NonNull CameraDevice camera) {
        super.onClosed(camera);
    }
}
