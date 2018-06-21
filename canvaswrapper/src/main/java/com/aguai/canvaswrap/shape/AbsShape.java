package com.aguai.canvaswrap.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public abstract class AbsShape implements IShape {

    protected static final int DEFAULT_PAINT_WIDTH=4;
    protected boolean isFull = false;
    protected Paint mPaint;
    private Paint debugPaint;
    protected float startX;
    protected float startY;
    protected float endx;
    protected float endy;
    protected int color;
    protected int paintWidth;
    protected RectF frameRect = new RectF();
    private int offsetY;
    private int offsetX;

    public AbsShape() {
        this(false, Color.BLACK, DEFAULT_PAINT_WIDTH);
    }

    public AbsShape(int color) {
        this(false, color, DEFAULT_PAINT_WIDTH);
    }

    public AbsShape(int color, int paintWidth) {
        this(false, color, paintWidth);
    }

    /**
     * @param isFull 是否空心
     * @param color  颜色
     */
    public AbsShape(boolean isFull, int color, int paintWidth) {
        this.isFull = isFull;
        this.paintWidth = paintWidth;
        this.color = color;
        // 去锯齿
        mPaint = new Paint();
        // 去锯齿
        mPaint.setAntiAlias(true);
        // 设置paint的 style 为STROKE：空心
        mPaint.setStyle(Paint.Style.STROKE);
        // 设置paint的外框宽度
        mPaint.setStrokeWidth(paintWidth);
        // 获取跟清晰的图像采样
        mPaint.setDither(true);
        // 接合处为圆弧
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        // 设置paint的颜色
        mPaint.setColor(color);
        // 设置paint的 style 为STROKE：空心
        mPaint.setStyle(isFull ? Paint.Style.FILL : Paint.Style.STROKE);
        debugPaint = new Paint();
        debugPaint.setStyle(Paint.Style.STROKE);
        debugPaint.setColor(Color.RED);
    }


    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }


    public Paint getmPaint() {
        return mPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }


    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getEndx() {
        return endx;
    }

    public void setEndx(float endx) {
        this.endx = endx;
    }

    public float getEndy() {
        return endy;
    }

    public void setEndy(float endy) {
        this.endy = endy;
    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        // 设置paint的颜色
        mPaint.setColor(color);
    }


    public int getPaintWidth() {
        return paintWidth;
    }

    public void setPaintWidth(int paintWidth) {
        mPaint.setStrokeWidth(paintWidth);
        this.paintWidth = paintWidth;
    }

    public int[] getCenterPostion() {
        int[] centerPos = new int[2];
        centerPos[0] = (int) ((startX + endx) / 2);
        centerPos[1] = (int) ((startY + endy) / 2);
        return centerPos;
    }

    public void drawShape(Canvas canvas) {
//        if (isFull){
//            frameRect.set(startX, startY , endx, endy);
//        }else {
//            frameRect.set(startX - paintWidth / 2, startY - paintWidth / 2, endx + paintWidth / 2, endy + paintWidth / 2);
//        }
//        canvas.drawRect(frameRect, debugPaint);

    }

    public RectF getFrameRect() {
        return frameRect;
    }

    public void setFrameRect(RectF frameRect) {
        this.frameRect = frameRect;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }
}