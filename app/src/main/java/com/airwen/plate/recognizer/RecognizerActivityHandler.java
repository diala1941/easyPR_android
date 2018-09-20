package com.airwen.plate.recognizer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.airwen.plate.R;
import com.airwen.plate.RecognizerActivity;
import com.airwen.plate.base.BeepManager;
import com.airwen.plate.camera.CameraManager;
import com.airwen.plate.camera.PictureCallback;
import com.airwen.plate.camera.PreviewCallback;
import com.airwen.plate.camera.ShutterCallback;


/**
 * 描述: 识别结果处理
 */
public final class RecognizerActivityHandler extends Handler {
	private final static String TAG = RecognizerActivityHandler.class.getSimpleName();
	private RecognizerActivity activity = null;
	private RecognizerThread recognizerThread = null;
	private CameraManager cameraManager;
	private State state;

	private ShutterCallback shutterCallback;
	private BeepManager beepManager;
	private PreviewCallback previewCallback;
	private PictureCallback pictureCallback;


	private enum State {
		PREVIEW, SUCCESS, DONE
	}

	public RecognizerActivityHandler(RecognizerActivity activity, CameraManager cameraManager) {
		this.activity = activity;
		recognizerThread = new RecognizerThread(activity);
		recognizerThread.start();
		this.cameraManager = cameraManager;
		cameraManager.startPreview();
		beepManager = new BeepManager(activity);
		previewCallback = cameraManager.getPreviewCallback();
		shutterCallback = new ShutterCallback(beepManager);
		state = State.SUCCESS;
	}

	/**
	 * 识别开始一次调用
	 */
	public void RecognizerTask(){
		Log.i(TAG, "RecognizerTask is invoked.");
		if(state == State.SUCCESS){
			restartPreviewAndRecognizer();
			Log.i(TAG, "RecognizerTask is executed.");
		}

//		pictureCallback = cameraManager.getPictureCallback();
//		pictureCallback.setHandler(recognizerThread.getHandler(), R.id.recognizer);
//		cameraManager.getCamera().takePicture(shutterCallback, null, pictureCallback);
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case R.id.recognizer:
			if (state == State.PREVIEW) {
			activity.handlerRecognizer(message);
			}
			break;
		case R.id.restart_preview:
			restartPreviewAndRecognizer();
			break;
		case R.id.recognizer_succeeded:
			// 解析成功，回调
			state = State.SUCCESS;
			activity.handlerRecognizer(message);
			break;

		case R.id.recognizer_failed:
			state = State.SUCCESS;
			activity.handlerRecognizer(message);
			break;
		case R.id.recognizer_error:
			state = State.SUCCESS;
			activity.handlerRecognizer(message);
			break;
		}

	}

	public void quitSynchronously() {
		state= State.DONE;
		cameraManager.stopPreview();

		//end recognizerThread.
		Message quit = Message.obtain(recognizerThread.getHandler(), R.id.quit);
		quit.sendToTarget();

		beepManager.close();

		removeMessages(R.id.recognizer_succeeded);
		removeMessages(R.id.recognizer_failed);

		removeMessages(R.id.recognizer);
		removeMessages(R.id.auto_focus);
	}

//	private void restartPreviewAndRecognizer() {
//		if (state == State.SUCCESS) {
//			state = State.PREVIEW;
//			cameraManager.requestPreviewFrame(recognizerThread.getHandler(), R.id.recognizer);
//		}
//	}

	private void restartPreviewAndRecognizer() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			cameraManager.requestPreviewFrame(shutterCallback, recognizerThread.getHandler(), R.id.recognizer);
		}
	}

}
