package com.aguai.canvaswrap.shape;

import android.graphics.Canvas;
import android.graphics.RectF;

public class OvalShape extends AbsShape {
    private RectF mDrawRect;

    public OvalShape(boolean isFull, int color, int width,float startX, float startY, float endX, float endY) {
        super(isFull, color, width);
        mDrawRect = new RectF();
        this.startX = startX;
        this.startY = startY;
        this.endx = endX;
        this.endy = endY;
        mDrawRect.set(startX, startY, endX, endY);
    }


    public void drawShape(Canvas canvas) {
        canvas.drawOval(mDrawRect, mPaint);
        super.drawShape(canvas);
    }

}