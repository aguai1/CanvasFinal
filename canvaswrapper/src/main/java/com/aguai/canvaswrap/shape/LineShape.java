package com.aguai.canvaswrap.shape;

import android.graphics.Canvas;

import com.aguai.canvaswrap.shape.path.SerializablePath;

/**
 * 直线
 */
public class LineShape extends AbsShape {
    protected SerializablePath mPath;

    public LineShape(int color, int width) {
        super(color, width);
        mPath = new SerializablePath();
    }

    public void onLayout(float startX, float startY, float endx, float endy) {
        this.startX = startX;
        this.startY = startY;
        this.endx = endx;
        this.endy = endy;
        cx = (int) ((endx + startX) / 2);
        cy = (int) ((endy + startY) / 2);
        mPath.reset();
        mPath.moveTo(startX, startY);
        mPath.lineTo(endx, endy);

        int border = (int) mPaint.getStrokeWidth() * 2;
        mInvalidRect.set((int) startX - border, (int) startY - border, (int) startX + border, (int) startY + border);
        mInvalidRect.union((int) endx - border, (int) endy - border, (int) endx + border, (int) endy + border);
    }

    public void drawShape(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }
}