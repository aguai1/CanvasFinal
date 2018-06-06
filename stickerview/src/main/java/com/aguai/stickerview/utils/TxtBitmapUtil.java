package com.aguai.stickerview.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;

import com.aguai.stickerview.bean.config.FrontItem;
import com.aguai.stickerview.bean.config.FrontPicItem;
import com.aguai.stickerview.utils.BitmapUtils;
import com.aguai.stickerview.utils.Constants;

import java.io.File;
import java.util.List;

/**
 * Created by whb on 17-10-16.
 */

public class TxtBitmapUtil {

    public static void getTextBitmap(String str, FrontPicItem frontPicItem, LoadCallBack loadCallBack) {
        File file = new File(Constants.getFrontDir() + DownLoadUtils.convertUrlToCompleteUrl(frontPicItem.getFrontUrlName()));
        if (!file.exists()) {
            loadCallBack.loadError("front not exists");
            return;
        }
        try {
            Typeface font = Typeface.createFromFile(file);
            final Bitmap txtBitmap = BitmapUtils.createTxtBitmap(str, font, Color.parseColor(frontPicItem.getColor()));
            if (frontPicItem.getServerUrl().equals("")) {
                loadCallBack.loadSucceed(txtBitmap);
                return;
            }
            ULSeeFileManager.getULSeeFileManager().getImage(frontPicItem.getServerUrl(), new LoadCallBack() {
                @Override
                public void loadSucceed(Bitmap bitmap) {
                    if (bitmap == null) {
                        loadCallBack.loadSucceed(txtBitmap);
                    }
                    List<FrontPicItem.Rect> bitmapRect = frontPicItem.getBitmapRect();
                    Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888); //图象大小要根据文字大小算下,以和文本长度对应
                    Canvas canvasTemp = new Canvas(bmp);
                    canvasTemp.drawColor(Color.TRANSPARENT);
                    canvasTemp.drawBitmap(bitmap, 0, 0, null);

                    for (int i = 0; i < bitmapRect.size(); ++i) {
                        FrontPicItem.Rect rect = bitmapRect.get(i);

                        int dstWidth = (int) (rect.getRight() - rect.getLeft()) * bitmap.getWidth() / 100;
                        int dstHeight = (int) (rect.getBottom() - rect.getTop()) * bitmap.getHeight() / 100;
                        Bitmap smallBitmapByXY = BitmapUtils.getSmallBitmapByXY(txtBitmap, dstWidth, dstHeight, true);

                        int left = (int) (rect.getLeft() * bitmap.getWidth() / 100);
                        int right = (int) (rect.getRight() * bitmap.getWidth() / 100);
                        int top = (int) (rect.getTop() * bitmap.getHeight() / 100);
                        int bottom = (int) (rect.getBottom() * bitmap.getHeight() / 100);

                        int drawX = left + (right - left - smallBitmapByXY.getWidth()) / 2;
                        int drawY = top + (bottom - top - smallBitmapByXY.getHeight()) / 2;

                        canvasTemp.drawBitmap(smallBitmapByXY, drawX, drawY, null);
                    }
                    loadCallBack.loadSucceed(bmp);
                }

                @Override
                public void loadError(String s) {

                }
            });
        } catch (Exception e) {
            loadCallBack.loadError(e.getMessage());
        }
    }

    public static void getFrontTextBitmap(String string, FrontItem frontItem, LoadCallBack loadCallBack) {
        FrontPicItem frontPicItem = new FrontPicItem();
        frontPicItem.setSize(120);
        frontPicItem.setFrontUrl(frontItem.getFrontUrl());
        frontPicItem.setColor("#ffffff");
        getTextBitmap(string, frontPicItem, loadCallBack);
    }
}
