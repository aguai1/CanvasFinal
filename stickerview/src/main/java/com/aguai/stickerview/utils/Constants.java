package com.aguai.stickerview.utils;

import android.os.Environment;


import java.io.File;

public class Constants {

    /**
     * 取SD卡路径
     **/
    private static String getFilePath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);  //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();  //获取根目录
        }
        if (sdDir != null) {
            return sdDir.toString() + File.separator + "stickerview/";
        } else {
            return "";
        }
    }

    public static String getBitmapPath() {
        String absolutePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        return absolutePath + File.separator +"stickerview/";
    }

    public static String getLogPath() {
        return getFilePath() + File.separator + "log";
    }

    public static String getCachePath() {
        return getFilePath() + File.separator + "cache";
    }

    public static String getFrontDir() {
        return getFilePath() + File.separator + "Front" + File.separator;
    }

    public static String getFileCacheDir() {
        return getFilePath() + File.separator + "Files" + File.separator;
    }
}
