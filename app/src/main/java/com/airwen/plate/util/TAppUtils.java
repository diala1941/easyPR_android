package com.airwen.plate.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Administrator on 2016-5-31.
 * 应用通用程序
 */
public class TAppUtils {

    /**
     * 拨打电话号码
     * @param context
     * @param number
     */
    public  static void dialphone(Context context, String number){
        Uri uri = Uri.parse("tel:" + number);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(intent);
    }

    /**
     * 发送短信到指定电话号码（手机号码）
     * @param context
     * @param smsBody
     * @param tel
     */
    public static void sendSMS(Context context, String smsBody, String tel) {
        Uri uri = Uri.parse("smsto:" + tel);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", smsBody);
        context.startActivity(it);
    }

    /**
     * 应用平分
     * @param context
     */
    public static void scoreApp(Context context){
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent intentScore = new Intent(Intent.ACTION_VIEW, uri);
        intentScore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intentScore.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intentScore);
        }
    }


    /**
     * 点击 窗口其他区域时 关闭窗口
     * @param activity
     */
    public  static void hideSoftKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

    }

    //storage permission
    private static final int RECORD_PERMISSION_REQUEST_CODE = 1;

    private static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;

    private static final int CAMERA_PERMISSION_REQUES_CODE = 3;

    private static final int REQUEST_READ_SMS_PERMISSION = 4;


    private static String[] PERMISSION_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static String[] PERMISSION_CAMERA = {
            Manifest.permission.CAMERA
    };

    private static String[] PERMISSION_RECORD = {
            Manifest.permission.RECORD_AUDIO
    };

    private static String[] PERMISSION_READ_SMS = {
            Manifest.permission.READ_SMS
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * if the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void versifyStoragePermission(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, PERMISSION_STORAGE, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }


    public static void verifyCameraPermission(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, PERMISSION_CAMERA, CAMERA_PERMISSION_REQUES_CODE);
        }
    }

    public static void verifyRecordPermission(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, PERMISSION_RECORD, RECORD_PERMISSION_REQUEST_CODE);
        }
    }

    public static void verifyReadSMSPermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS);
        if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSION_READ_SMS, REQUEST_READ_SMS_PERMISSION);
        }
    }

    public static String formatUrl(String s) {
        String str = null;
        if (s != null && s.contains("\\")) {
            str = s.replace("\\", "/");
            return str;
        }else {
            return s;
        }
    }
}
