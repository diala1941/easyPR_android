package com.airwen.plate.recognizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.airwen.plate.R;
import com.airwen.plate.RecognizerActivity;
import com.airwen.plate.base.Recognize;
import com.airwen.plate.util.BitmapUtil;
import com.airwen.plate.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 描述: 识别处理
 */
final class RecognizerHandler extends Handler {
	private final static String TAG = RecognizerHandler.class.getSimpleName();
	private RecognizerActivity activity = null;

	public RecognizerHandler(RecognizerActivity activity) {
		this.activity = activity;
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
		case R.id.recognizer:
			recognizer((byte[]) message.obj, message.arg1, message.arg2);
			break;
		case R.id.quit:
			//退出循环，结束调用线程
			Log.i(TAG, "RecognizerHandler quit.");
			Looper.myLooper().quit();
			break;
		}
	}

	private void recognizer(byte[] data, int width, int height) {
		Log.i(TAG," data length is : " + data.length);
		Log.i(TAG," width is  : " + width);
		Log.i(TAG," height is : " + height);
//		Camera.Size size = activity.getCameraManager().getPreviewSize();

		try {
			File originalFile = FileUtil.getOutputMediaFile(FileUtil.FILE_TYPE_IMAGE);
			if (originalFile == null) {
				Log.d(TAG, "Error creating media file, check storage permissions: ");
				return;
			}
			/**
			 * YuvImage image format of data by previewCallback.
			 */
//			final YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
//			// 转Bitmap
//			ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
//			if(! image.compressToJpeg(new Rect(0, 0, width, height), 100, os)) {
//				return;
//			}
//			byte[] bmpBytes = os.toByteArray();
//			Bitmap bitmap = BitmapFactory.decodeByteArray(bmpBytes, 0, bmpBytes.length);
//			os.close();

			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

			FileOutputStream fos = new FileOutputStream(originalFile);
			// 照片转方向
			Bitmap normalBitmap = BitmapUtil.createRotateBitmap(bitmap);
			normalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();

			cropBitmapAndRecognize(normalBitmap);
//			cropBitmapAndRecognize(normalBitmap, originalFile);
		} catch (FileNotFoundException e) {
			Log.d(TAG, "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, "Error accessing file: " + e.getMessage());
		}
	}

	public void cropBitmapAndRecognize(Bitmap originalBitmap) {

		if(originalBitmap != null){
			Log.i(TAG, "bitmap of originalBitmap is not null.");
		}else {
			Log.i(TAG, "bitmap of originalBitmap is null.");
			return;
		}

		int originalWidth = originalBitmap.getWidth();
		int originalHeight = originalBitmap.getHeight();
		Log.i(TAG, "originalBitmap : width:x-> " + originalWidth + ", height:y-> " + originalHeight);

		// 裁剪出关注区域
		DisplayMetrics metric = activity.getResources().getDisplayMetrics(); //.getMetrics(metric);
		int width = metric.widthPixels;  // 屏幕宽度（像素）
		int height = metric.heightPixels;  // 屏幕高度（像素）
//		Bitmap sizeBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true);
		//车牌区域
		ImageView cropLayout = activity.getCropLayout();
//		int rectWidth = cropLayout.getWidth();//(int)(mIvPlateRect.getWidth() * 1.5);
//		int rectHight = cropLayout.getHeight();//(int)(mIvPlateRect.getHeight() * 1.5);
		//图片区域宽和高放大1.5倍
		int rectWidth = (int)(cropLayout.getWidth() * 1.5);
		int rectHight = (int)(cropLayout.getHeight() * 2);
		Log.i(TAG, "cropLayout : width-> " + rectWidth + ", height-> " + rectHight);
		int[] location = new int[2];
		cropLayout.getLocationOnScreen(location);

		Log.i(TAG, "cropLayout locationOnScreen: x-> " + location[0] + ", y-> " + location[1]);
		//旋转之后的图片定位点向左移动宽的1/4.
		location[0] -= cropLayout.getWidth() * 0.25 ;
		//旋转之后的图片定位点上移3/4高
		location[1] -= cropLayout.getHeight() * 0.75;
		Log.i(TAG, "cropLayout location area ");
		Log.i(TAG, "cropLayout locationOnScreen: x-> " + location[0] + ", y-> " + location[1]);

		if(location[0] < 1){
			Log.i(TAG, " x is than 1 or 0 ");
			return;
		}
		Bitmap cropBitmap = Bitmap.createBitmap(originalBitmap, location[0], location[1], rectWidth, rectHight);

		try {
			// 保存图片并进行车牌识别
			File cropFile = FileUtil.getOutputMediaFile(FileUtil.FILE_TYPE_PLATE);
			if (cropFile == null) {
				Log.d(TAG, "Error creating media file, check storage permissions: ");
				return;
			}
//			Recognizer recognizer = new Recognizer(activity);
			Recognize recognizer = new Recognize(activity);

			Log.i(TAG, " pictureFile path is " + cropFile.getAbsolutePath());
			FileOutputStream fos = new FileOutputStream(cropFile);
			cropBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();

			Handler handler = activity.getHandler();
			if(handler != null){
				//正在识别...
				Message message = Message.obtain(handler, R.id.recognizer, "正在识别...");
				Bundle bundle = new Bundle();
				bundleThumbnail(cropBitmap, bundle);
				message.setData(bundle);
				message.sendToTarget();
			}

			// 进行车牌识别
			String plate = recognizer.recognize(cropFile.getAbsolutePath());
			if (plate != null) {
				// Don't log the barcode contents for security.
				if (handler != null) {
					if(!plate.equalsIgnoreCase("0")){
						//识别成功
						Log.i(TAG, "Plate is " + plate);
						Message message = Message.obtain(handler, R.id.recognizer_succeeded, plate);
						Bundle bundle = new Bundle();
						bundleThumbnail(cropBitmap, bundle);
						message.setData(bundle);
						message.sendToTarget();
					}else {
						//识别失败
						Message message = Message.obtain(handler, R.id.recognizer_failed, "请调整角度");
						Bundle bundle = new Bundle();
						bundleThumbnail(cropBitmap, bundle);
						message.setData(bundle);
						message.sendToTarget();
					}
				}
			} else {
				//识别出现错误
				if (handler != null) {
					Message message = Message.obtain(handler, R.id.recognizer_error, "不能识别");
					Bundle bundle = new Bundle();
					bundleThumbnail(cropBitmap, bundle);
					message.setData(bundle);
					message.sendToTarget();
				}
			}

		} catch (FileNotFoundException e) {
			Log.d(TAG, "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, "Error accessing file: " + e.getMessage());
		}
	}


	public void cropBitmapAndRecognize(Bitmap originalBitmap, File file) {

		if(originalBitmap != null){
			Log.i(TAG, "bitmap of originalBitmap is not null.");
		}else {
			Log.i(TAG, "bitmap of originalBitmap is null.");
			return;
		}

		// 裁剪出关注区域
		DisplayMetrics metric = activity.getResources().getDisplayMetrics(); //.getMetrics(metric);
		int width = metric.widthPixels;  // 屏幕宽度（像素）
		int height = metric.heightPixels;  // 屏幕高度（像素）
//		Bitmap sizeBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true);
		//车牌区域
		ImageView cropLayout = activity.getCropLayout();
//		int rectWidth = cropLayout.getWidth();//(int)(mIvPlateRect.getWidth() * 1.5);
//		int rectHight = cropLayout.getHeight();//(int)(mIvPlateRect.getHeight() * 1.5);
		//图片区域宽和高放大1.5倍
		int rectWidth = (int)(cropLayout.getWidth() * 1.5);
		int rectHight = (int)(cropLayout.getHeight() * 1.5);
		Log.i(TAG, "cropLayout : width-> " + rectWidth + ", height-> " + rectHight);
		int[] location = new int[2];
		cropLayout.getLocationOnScreen(location);

		Log.i(TAG, "cropLayout locationOnScreen: x-> " + location[0] + ", y-> " + location[1]);
		//旋转之后的图片定位点向左移动宽的1/4.
		location[0] -= cropLayout.getWidth() * 0.25 ;
		//旋转之后的图片定位点上移3/4高
		location[1] -= cropLayout.getHeight() * 0.75;
		Log.i(TAG, "cropLayout location area ");
		Log.i(TAG, "cropLayout locationOnScreen: x-> " + location[0] + ", y-> " + location[1]);

		if(location[0] < 1){
			Log.i(TAG, " x is than 1 or 0 ");
			return;
		}
		Bitmap cropBitmap = Bitmap.createBitmap(originalBitmap, location[0], location[1], rectWidth, rectHight);

		try {
			// 保存图片并进行车牌识别
			File cropFile = FileUtil.getOutputMediaFile(FileUtil.FILE_TYPE_PLATE);
			if (cropFile == null) {
				Log.d(TAG, "Error creating media file, check storage permissions: ");
				return;
			}
//			Recognizer recognizer = new Recognizer(activity);
			Recognize recognizer = new Recognize(activity);

			Log.i(TAG, " pictureFile path is " + cropFile.getAbsolutePath());
			FileOutputStream fos = new FileOutputStream(cropFile);
			cropBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.close();

			Handler handler = activity.getHandler();
			if(handler != null){
				//正在识别...
				Message message = Message.obtain(handler, R.id.recognizer, "正在识别...");
				Bundle bundle = new Bundle();
				bundleThumbnail(cropBitmap, bundle);
				message.setData(bundle);
				message.sendToTarget();
			}

			// 进行车牌识别
			String plate = recognizer.recognize(file.getAbsolutePath());
			if (plate != null) {
				// Don't log the barcode contents for security.
				if (handler != null) {
					if(!plate.equalsIgnoreCase("0")){
						//识别成功
						Log.i(TAG, "Plate is " + plate);
						Message message = Message.obtain(handler, R.id.recognizer_succeeded, plate);
						Bundle bundle = new Bundle();
						bundleThumbnail(cropBitmap, bundle);
						message.setData(bundle);
						message.sendToTarget();
					}else {
						//识别失败
						Message message = Message.obtain(handler, R.id.recognizer_failed, "请调整角度");
						Bundle bundle = new Bundle();
						bundleThumbnail(cropBitmap, bundle);
						message.setData(bundle);
						message.sendToTarget();
					}
				}
			} else {
				//识别出现错误
				if (handler != null) {
					Message message = Message.obtain(handler, R.id.recognizer_error, "不能识别");
					Bundle bundle = new Bundle();
					bundleThumbnail(cropBitmap, bundle);
					message.setData(bundle);
					message.sendToTarget();
				}
			}

		} catch (FileNotFoundException e) {
			Log.d(TAG, "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, "Error accessing file: " + e.getMessage());
		}
	}


	private static void bundleThumbnail(Bitmap bitmap, Bundle bundle) {
		try {
//		Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
			bundle.putByteArray("bmp", out.toByteArray());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
