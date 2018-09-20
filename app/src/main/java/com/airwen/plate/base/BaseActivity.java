package com.airwen.plate.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.airwen.plate.R;
import com.airwen.plate.util.TAppUtils;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BaseActivity extends AppCompatActivity {
    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        overridePendingTransition(R.anim.slide_left_in, R.anim.hold);
        versionMarshmallow();
    }

    /**
     * version >= 23 denied access external storage
     *
     * Permission denied processing.
     */
    private void versionMarshmallow(){
        if(Build.VERSION.SDK_INT >= 23){
            TAppUtils.versifyStoragePermission(this);
            TAppUtils.verifyCameraPermission(this);
        }
    }

    /** Check if this device has a camera */
    protected boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void showDialog() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("加载中");
        pDialog.show();
        pDialog.setCancelable(false);

    }


    public void showDialogWithTitle(String c) {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText(c);
        pDialog.show();
        pDialog.setCancelable(false);

    }


    public void dimissDialog() {
        pDialog.dismissWithAnimation();
    }


    public void getTopBar(String arg0) {
//        TextView tvMid = (TextView) findViewById(R.id.tvMid);
//        tvMid.setText(arg0);
//
//        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
//        ivBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });


    }
//
//    public void getTopBarNoBack(String arg0) {
//        TextView tvMid = (TextView) findViewById(R.id.tvMid);
//        tvMid.setText(arg0);
//
//        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
//        ivBack.setVisibility(View.GONE);
//
//
//    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
    }
}
