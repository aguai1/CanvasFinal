package com.aguai.canvaswrap.shape;

import android.graphics.Canvas;

/**
 * 圆形
 */
public class CircleShape extends AbsShape {
    private int radius;
    public CircleShape(boolean isFull, int color, int width,float centerX, float centerY,int radius) {
        super(isFull, color, width);
        this.startX = centerX - radius;
        this.startY = centerY - radius;
        this.radius = radius;
        this.endx = centerX +radius;
        this.endy = centerY+radius;
    }

    public void drawShape(Canvas canvas) {
        int[] centerPostion = getCenterPostion();
        canvas.drawCircle(centerPostion[0], centerPostion[1], radius, mPaint);
        super.drawShape(canvas);
    }



}