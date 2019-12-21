package com.example.myapplication;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Surface;

public class SessionStateCallback extends CameraCaptureSession.StateCallback {

    // Posible parameters
    CameraDevice cameraDevice;
    Surface surface;
    Handler handler;

    public SessionStateCallback(CameraDevice cameraDevice, Surface surface, Handler handler) {
        this.cameraDevice = cameraDevice;
        this.surface = surface;
        this.handler = handler;
    }

    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        try {
            CaptureRequest.Builder b =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            b.addTarget(surface);
            b.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            b.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);


            CaptureRequest previewRequest = b.build();

            session.setRepeatingRequest(previewRequest, null, handler);

        } catch (CameraAccessException ex){
            ex.printStackTrace();
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
    }
}
