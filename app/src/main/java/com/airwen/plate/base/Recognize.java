package com.airwen.plate.base;

import android.content.Context;

import com.airwen.plate.util.EasyPRAssetUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;

import static com.gtercn.recognizer.plate.PlateRecognizer.plateRecognize;

/**
 * Created by Administrator on 2017-8-28.
 */
public class Recognize {
    private Context mContext;

    public Recognize(Context context) {
        mContext = context;
        initAssetsFiles(context);
    }

    private void initAssetsFiles(Context context){
        EasyPRAssetUtil.CopyAssets(context, EasyPRAssetUtil.EasyPRDir, "/sdcard/" + EasyPRAssetUtil.EasyPRDir);
    }

    public String recognize(String imagePath) {
        //判断文件夹是否存在
        File imageFile = new File(imagePath);
        if (! imageFile.exists()) {
            return null;
        }

        byte[] retBytes = plateRecognize(imagePath);
        String result = null;
        try {
            result = new String(retBytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

}
