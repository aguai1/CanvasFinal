package com.aguai.canvaswrap.utils;

import android.os.Environment;
import java.io.File;

/**
 * Created by chong on 17-7-11.
 */

public class Constants {
    /**
     * 取SD卡路径
     **/
    public static String getFilePath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);  //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();  //获取根目录
        }
        if (sdDir != null) {
            return sdDir.toString() + File.separator + "CanvasWrapper";
        } else {
            String absolutePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            return absolutePath + File.separator + "CanvasWrapper";
        }
    }

    public static String getSupportPath() {
        return getFilePath() + File.separator + "feedback";
    }

    public static String getBitmapPath() {
        return getFilePath()+"bitmap";
    }

    public static String getLogPath() {
        return getSupportPath() + File.separator + "log";
    }

    public static String getCachePath() {
        return getFilePath() + File.separator + "cache";
    }

    public static String getApkPath() {
        String path =getFilePath()+File.separator+"apk";
        File file=new File(path);
        if (!file.exists()){
            file.mkdirs();
        }
        return path;
    }

    public static String getErrorDir() {
        return getSupportPath()+File.separator+"error";
    }

}
