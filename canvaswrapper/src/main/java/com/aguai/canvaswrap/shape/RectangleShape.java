package com.aguai.canvaswrap.shape;

import android.graphics.Canvas;

public class RectangleShape extends AbsShape {

    public RectangleShape(boolean isFull, int color, int width,float startX, float startY, float x, float y) {
        super(isFull, color, width);
        this.startX = startX;
        this.startY = startY;
        this.endx = x;
        this.endy = y;
    }

    public void drawShape(Canvas canvas) {
        canvas.drawRect(startX, startY, endx, endy, mPaint);
        super.drawShape(canvas);
    }


}