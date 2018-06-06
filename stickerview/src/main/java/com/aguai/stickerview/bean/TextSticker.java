package com.aguai.stickerview.bean;

import android.graphics.Bitmap;

import com.aguai.stickerview.MultipleLayersView;
import com.aguai.stickerview.bean.config.FrontItem;
import com.aguai.stickerview.bean.config.FrontPicItem;
import com.ulsee.uups.api.filecache.interfaces.LoadCallBack;
import com.ulsee.uups.api.model.http.FrontItem;
import com.ulsee.uups.api.model.http.FrontPicItem;
import com.ulsee.uups.moudles.sticker.text.TxtBitmapUtil;

/**
 * 文字
 */
public class TextSticker extends Sticker {

    private String txt;
    private FrontPicItem fontPicInfo;

    public TextSticker(MultipleLayersView multipleLayersView, String txt, FrontPicItem frontPicItem, Bitmap bmp, int bgWidth, int bgHeight) {
        super(multipleLayersView, bmp, bgWidth, bgHeight, StickType.MODE_TXT);
        this.fontPicInfo = frontPicItem;
        this.txt = txt;
    }

    public void replaceFront(FrontItem frontItem) {
        fontPicInfo.setFrontUrl(frontItem.getFrontUrl());
        TxtBitmapUtil.getTextBitmap(txt, fontPicInfo, new LoadCallBack() {
            @Override
            public void loadSucceed(Bitmap bitmap) {
                setBitmap(bitmap);
            }

            @Override
            public void loadError(String s) {

            }
        });
    }

    public void replacePicStyleInfo(FrontPicItem frontPicItem) {
        fontPicInfo = frontPicItem;
        replaceText(fontPicInfo.getName());
    }

    public void replaceText(String str) {
        this.txt = str;
        TxtBitmapUtil.getTextBitmap(str, fontPicInfo, new LoadCallBack() {
            @Override
            public void loadSucceed(Bitmap bitmap) {
                setBitmap(bitmap);
            }

            @Override
            public void loadError(String s) {

            }
        });
    }

    public String getTxt() {
        return txt;
    }

    public FrontPicItem getFontPicInfo() {
        return fontPicInfo;
    }
}
