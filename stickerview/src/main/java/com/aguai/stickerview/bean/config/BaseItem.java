package com.aguai.stickerview.bean.config;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.aguai.stickerview.imagecache.WBImageLoader;

import java.io.Serializable;

/**
 * Created by zhangchao on 17-9-29.
 */

public class BaseItem implements Serializable {

    public static final String URL_NO_RES = "";
    private int index;
    private String name = URL_NO_RES;
    /**
     * 本地处理后的图片
     */
    private String localDelUrl = URL_NO_RES;
    private String serverUrl = URL_NO_RES;
    private boolean adjustable;
    private boolean extern;

    public String getName() {
            return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isAdjustable() {
        return adjustable;
    }

    public void setAdjustable(boolean adjustable) {
        this.adjustable = adjustable;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void getWbUrl(boolean scaleByDevice, OnWbLoadCallBack onSaveCallBack) {
        getWbUrl(serverUrl, scaleByDevice, onSaveCallBack);
    }

    private void getWbUrl(String url, boolean scaleByDevice, OnWbLoadCallBack onSaveCallBack) {
        Bitmap serverUrlBmp = WBImageLoader.getInstance().loadBitmap(url);
        if (serverUrlBmp != null) {
            onSaveCallBack.loadSucceed(url);
        } else {
            ULSeeFileManager.getULSeeFileManager().getImage(url, scaleByDevice, new LoadCallBack() {
                @Override
                public void loadSucceed(Bitmap bitmap) {
                    WBImageLoader.getInstance().saveBitmap(bitmap, url);
                    onSaveCallBack.loadSucceed(url);
                }
                @Override
                public void loadError(String s) {

                }
            });
        }
    }

    public String getLocalDelUrl() {
        return localDelUrl;
    }

    public void setLocalDelUrl(String localDelUrl) {
        this.localDelUrl = localDelUrl;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public void setLocalDelBitmap(Bitmap bitmap) {
        localDelUrl = System.currentTimeMillis() + "BaseItem";
        WBImageLoader.getInstance().saveBitmap(bitmap, localDelUrl);
    }

    public boolean isExtern() {
        return extern;
    }

    public void setExtern(boolean extern) {
        this.extern = extern;
    }
}
