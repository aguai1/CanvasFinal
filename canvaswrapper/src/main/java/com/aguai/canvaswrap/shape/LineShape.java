package com.aguai.canvaswrap.shape;

import android.graphics.Canvas;

import com.aguai.canvaswrap.shape.path.SerializablePath;

/**
 * 直线
 */
public class LineShape extends AbsShape {
    protected SerializablePath mPath;

    public LineShape(int color, int width,float startX, float startY, float endx, float endy) {
        super(color, width);
        mPath = new SerializablePath();
        this.startX = startX;
        this.startY = startY;
        this.endx = endx;
        this.endy = endy;
        mPath.reset();
        mPath.moveTo(startX, startY);
        mPath.lineTo(endx, endy);
    }


    public void drawShape(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
        super.drawShape(canvas);
    }
}