package com.aguai.canvaswrap.shape;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.aguai.canvaswrap.CanvasWrapper;

/**
 * 图片
 */
public class PicShape extends AbsShape {

    private String picUrl;
    public PicShape( String picPath) {
        super();
        this.picUrl = picPath;
    }

    public void onLayout(float mStartX, float mStartY, float x, float y) {
        this.startX = mStartX;
        this.startY = mStartY;
        this.endx = x;
        this.endy = y;

        int border = (int) mPaint.getStrokeWidth();
        mInvalidRect.set(mStartX - border, mStartY - border, mStartX + border, mStartY + border);
        mInvalidRect.union(x - border, y - border, x + border, y + border);
    }

    public void drawShape(final Canvas canvas) {
        Bitmap bitmap1 = CanvasWrapper.getInstance().getImageLoader().getImageFromUrl(picUrl);
        if (bitmap1 != null) {
            canvas.drawBitmap(bitmap1, startX, startY, null);
        }
    }
    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

}