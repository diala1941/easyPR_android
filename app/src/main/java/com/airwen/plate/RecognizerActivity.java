package com.airwen.plate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airwen.plate.base.BaseActivity;
import com.airwen.plate.camera.CameraManager;
import com.airwen.plate.recognizer.RecognizerActivityHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Plate recognition.
 * on/off flashlight.
 * recognizer.
 * save image.
 * show result.
 * cancel operation and return back.
 */
public class RecognizerActivity extends BaseActivity implements SurfaceHolder.Callback{
    private final static String TAG = RecognizerActivity.class.getSimpleName();

    private CameraManager mCameraManager;

    private FrameLayout mContainer;

    private SurfaceView mSurfaceView;

    private ImageView mTop;

    private ImageView mElectricTorch;

    private ImageView mRecognizerWindow, mCropLayout;

    private TextView mRecognizerResult;

    private ImageView mRecognizerBtn;

    private boolean isHasSurface = false;

    private boolean isFlashlight = false;

    private RecognizerActivityHandler mHandler;

    //show img, test.
    ImageView mShowImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler = null;
        mCameraManager = new CameraManager(getApplication());
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (isHasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mHandler != null){
            mHandler.quitSynchronously();
            mHandler = null;
        }

        mCameraManager.closeDriver();

        if(!isHasSurface){
            mSurfaceView.getHolder().removeCallback(this);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 识别结果处理？
     * @param msg
     */
    public void handlerRecognizer(Message msg){
        Log.i(TAG,"result is recognized. It is very good.");

        Drawable drawable = mShowImg.getDrawable();
        if(true/*drawable == null*/){
            Log.i(TAG,"showImg is set bitmap.");
            Bundle bundle = msg.getData();
            byte[] value = bundle.getByteArray("bmp");
            if(value != null){
                ByteArrayInputStream bi = new ByteArrayInputStream(value);
                Bitmap bp = BitmapFactory.decodeStream(bi);
                mShowImg.setImageBitmap(bp);
            }
        }

        switch (msg.what) {
            case R.id.recognizer:
                mRecognizerResult.setText((String)msg.obj);
                break;
            case R.id.recognizer_succeeded:
                mRecognizerResult.setText((String)msg.obj);
                break;
            case R.id.recognizer_failed:
                mRecognizerResult.setText((String)msg.obj);
                break;

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if(!isHasSurface){
            isHasSurface = true;
            initCamera(surfaceHolder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isHasSurface = false;
    }

    private void initView(){
        mContainer = (FrameLayout) findViewById(R.id.pr_framelayout);
        mSurfaceView = (SurfaceView) findViewById(R.id.pr_surfaceView);
        mTop = (ImageView) findViewById(R.id.pr_return_top);
        mElectricTorch = (ImageView) findViewById(R.id.pr_flashlight);
        mRecognizerWindow = (ImageView) findViewById(R.id.pr_recognizer_window);
        mCropLayout = mRecognizerWindow;
        mRecognizerResult = (TextView) findViewById(R.id.pr_recognizer_result);
        mRecognizerBtn = (ImageView) findViewById(R.id.pr_capture_btn);

        mShowImg = (ImageView) findViewById(R.id.pr_show_img);

    }

    private void initData(){

        /**
         * close ui and clear activity resources of used by recognizer.
         */
        mTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //force off light;
                mCameraManager.closeFlashLight();
                finish();
            }
        });

        /**
         * open torch or flashlight
         */
        mElectricTorch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                torchSwitch();
            }
        });

        /**
         * execute recognizer
         */
        mRecognizerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mHandler != null){
                    mHandler.RecognizerTask();
                }
            }
        });

    }

    public Handler getHandler(){
        return mHandler;
    }

    public  CameraManager getCameraManager(){
        return mCameraManager;
    }

    public ImageView getCropLayout(){
        return mCropLayout;
    }

    /**
     * electric torch.
     */
    private void torchSwitch(){
        if(isFlashlight){
            isFlashlight = false;
            mCameraManager.closeFlashLight();
            mElectricTorch.setImageResource(R.drawable.ic_flash_off_white);
        }else {
            isFlashlight = true;
            mCameraManager.openFlashLight();
            mElectricTorch.setImageResource(R.drawable.ic_flash_on_white);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder){
        if(surfaceHolder == null){
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (mCameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }

        try {
            mCameraManager.openDriver(surfaceHolder);
            if (mHandler == null) {
                mHandler = new RecognizerActivityHandler(RecognizerActivity.this, mCameraManager);
            }

        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();;
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();;
        }

    }

    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("Camera error");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }
}
