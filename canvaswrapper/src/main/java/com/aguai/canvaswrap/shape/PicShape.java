package com.aguai.canvaswrap.shape;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.aguai.canvaswrap.CanvasWrapper;

/**
 * 图片
 */
public class PicShape extends AbsShape {

    private String picUrl;
    public PicShape( String picPath,float mStartX, float mStartY, float x, float y) {
        super();
        this.picUrl = picPath;
        this.startX = mStartX;
        this.startY = mStartY;
        this.endx = x;
        this.endy = y;
    }

    public void drawShape(final Canvas canvas) {
        Bitmap bitmap = CanvasWrapper.getInstance().getImageLoader().getImageFromUrl(picUrl);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, startX, startY, null);
        }
        super.drawShape(canvas);
    }
    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

}