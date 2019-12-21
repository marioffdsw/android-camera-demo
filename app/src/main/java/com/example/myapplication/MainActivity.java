package com.example.myapplication;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private final HandlerThread handlerThread = new HandlerThread("cameraHadlerThread",
            Process.THREAD_PRIORITY_BACKGROUND);
    Handler handler;
    CameraManager manager;
    String cameraId;
    CameraDevice cameraDevice;

    CameraDevice.StateCallback deviceStateCallback;
    Size pictureSize;
    ImageReader reader;

    TextureView textureView;
    Surface surface;
    CameraCaptureSession.StateCallback stateCallback;

    private final Semaphore lock = new Semaphore(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            textureView = findViewById(R.id.textureView);

            setContentView(R.layout.activity_main);
            manager = (CameraManager) getSystemService(CAMERA_SERVICE);
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
            cameraId = getCameraId();


            if(!lock.tryAcquire(2500, TimeUnit.MILLISECONDS)){
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }

            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    try {
                        cameraDevice = camera;
                        CameraCharacteristics cc = null;
                        cc = manager.getCameraCharacteristics(cameraId);
                        StreamConfigurationMap map = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                        Size[] outputSizes = map.getOutputSizes(ImageFormat.JPEG);
                        pictureSize = outputSizes[0];

                        reader = ImageReader.newInstance(
                                pictureSize.getWidth(),
                                pictureSize.getHeight(),
                                ImageFormat.JPEG,
                                2);

                        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
                        surface = new Surface(surfaceTexture);

                        stateCallback = new SessionStateCallback(
                                cameraDevice,
                                surface,
                                handler
                        );

                        cameraDevice.createCaptureSession(Arrays.asList(surface, reader.getSurface()),stateCallback, handler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {

                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {

                }
            }, handler);


        } catch(SecurityException ex) {
            ex.printStackTrace();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private String getCameraId() throws CameraAccessException {
        return manager.getCameraIdList()[0];
    }

    private void releaseLock(){
        try {
            lock.acquire();
            if(cameraDevice != null)
                cameraDevice.close();
            cameraDevice = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
