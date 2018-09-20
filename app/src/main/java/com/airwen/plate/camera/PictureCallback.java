package com.airwen.plate.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * Created by Administrator on 2017-8-30.
 */
public class PictureCallback implements Camera.PictureCallback {
    private static final String TAG = PictureCallback.class.getSimpleName();

    private final CameraConfigurationManager configManager;
    private Handler pictureHandler;
    private int pictureMessageWhat;

    public PictureCallback(CameraConfigurationManager configManager){
        this.configManager = configManager;
    }

    public void setHandler(Handler handler, int pictureMessageWhat){
        this.pictureHandler = handler;
        this.pictureMessageWhat = pictureMessageWhat;
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        camera.startPreview();
        Log.i(TAG, "PictureCallback. onPictureTaken is invoked.");
        Point cameraResolution = configManager.getCameraResolution();
        Handler handler = pictureHandler;
        if (cameraResolution != null && pictureHandler != null) {
            Message message = handler.obtainMessage(pictureMessageWhat, cameraResolution.x,
                    cameraResolution.y, bytes);
            message.sendToTarget();
            pictureHandler = null;
        } else {
            Log.d(TAG, "Got picture callback, but no handler or resolution available");
        }
    }
}
