package com.aguai.stickerview.imagecache;

import android.content.Context;
import android.graphics.Bitmap;

import com.aguai.stickerview.utils.L;


/**
 * Created by Aguai on 2016/11/24.
 */

public class WBImageLoader {
    private static final String TAG = "WBImageLoader";
    private static WBImageLoader instance;
    private ImageMemoryCache memoryCache;
    private ImageFileCache fileCache;

    private WBImageLoader(Context context) {
        memoryCache = new ImageMemoryCache(context);
        fileCache = new ImageFileCache();
    }

    public static void initLoader(Context context) {
        instance = new WBImageLoader(context);
    }

    public static WBImageLoader getInstance() {
        return instance;
    }

    public Bitmap loadBitmap(String url) {
        Bitmap result = null;

        if (url != null) {
            // 从内存缓存中获取图片
            result = memoryCache.getBitmapFromCache(url);
            if (result == null) {
                // 文件缓存中获取
                result = fileCache.getImage(url);
                L.e(TAG, "loadBitmap fromFile: " + url);
                if (result != null) {
                    // 添加到内存缓存
                    memoryCache.addBitmapToCache(url, result);
                }
            } else {
                L.e(TAG, "loadBitmap memoryCache: " + url);
            }
        }
        if (result == null) {
            L.e(TAG, "load bitmap ==null url:" + url);
        } else if (result.isRecycled()) {
            L.e(TAG, "load bitmap is recycled    url:" + url + "   id:" + result.getGenerationId());
        } else {
            L.e(TAG, "load bitmap:" + url + " id:" + result.getGenerationId());
        }
        return result;
    }

    public void saveBitmap(Bitmap bitmap, String path) {
        saveBitmap(bitmap, path, Bitmap.CompressFormat.JPEG);
    }

    public void saveBitmap(Bitmap bitmap, String path, Bitmap.CompressFormat compressFormat) {
        if (bitmap == null) {
            L.e(TAG, "saveBitmap bitmap ==null" + path);
        } else if (bitmap.isRecycled()) {
            L.e(TAG, "saveBitmap bitmap is recycled:" + path + "id:" + bitmap.getGenerationId());
        } else {
            L.e(TAG, "saveBitmap bitmap:" + path + "bitmapid:" + bitmap.getGenerationId());
        }
        // 添加到内存缓存
        memoryCache.addBitmapToCache(path, bitmap);
        fileCache.saveBitmap(bitmap, path, compressFormat);

    }

    public void onDestory() {
        fileCache.onDestory();
    }
}
