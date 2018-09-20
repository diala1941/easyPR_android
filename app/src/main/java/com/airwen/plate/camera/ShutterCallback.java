package com.airwen.plate.camera;

import android.hardware.Camera;
import android.util.Log;

import com.airwen.plate.base.BeepManager;

/**
 * Created by Administrator on 2017-8-30.
 */
public class ShutterCallback implements Camera.ShutterCallback {
    private static final String TAG = ShutterCallback.class.getSimpleName();

    private Camera camera;
    private PreviewCallback previewCallback;
    private BeepManager beepManager;

    public ShutterCallback(BeepManager beepManager){
        this.beepManager = beepManager;
    }

    public void setParams(Camera camera, PreviewCallback previewCallback){
        this.camera = camera;
        this.previewCallback = previewCallback;
    }

    @Override
    public void onShutter() {
        Log.i(TAG, "ShutterCallback, onShutter is invoked.");
        if(beepManager != null){
            beepManager.playBeepSoundAndVibrate();
            if(camera != null && previewCallback != null){
                Log.i(TAG, "ShutterCallback, setOneShotPreviewCallback is invoked.");
                camera.setOneShotPreviewCallback(previewCallback);
            }
        }
    }
}
