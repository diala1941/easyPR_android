package com.airwen.plate.recognizer;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.airwen.plate.RecognizerActivity;

import java.util.concurrent.CountDownLatch;

/**
 * 描述: 识别线程
 */
final class RecognizerThread extends Thread {
	private final static String TAG = RecognizerThread.class.getSimpleName();
	private RecognizerActivity activity;
	private Handler handler;
	//synchronize thread and main thread. it is not be done until it is receiving right.
	private final CountDownLatch handlerInitLatch;

	RecognizerThread(RecognizerActivity activity) {
		this.activity = activity;
		handlerInitLatch = new CountDownLatch(1);
	}

	/**
	 * 获取handler，当前线程阻塞，进入等待模式
	 * @return
     */
	Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			// continue?
		}
		return handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		//handler bundled with current thread.
		handler = new RecognizerHandler(activity);
		handlerInitLatch.countDown();
		Looper.loop();
		Log.i(TAG, "RecognizerThread is run.");
	}

}
