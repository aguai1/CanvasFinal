package com.aguai.canvaswrap.shape;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.aguai.canvaswrap.shape.path.SerializablePath;

public abstract class AbsShape implements IShape {

    protected boolean isFull = false;

    protected Paint mPaint;
    protected int cx;
    protected int cy;
    protected float startX;
    protected float startY;
    protected float endx;
    protected float endy;
    protected int color;
    protected int width;
    protected RectF mInvalidRect = new RectF();
    private int offsetY;
    private int offsetX;

    public AbsShape() {
        this(false,Color.BLACK,4);
    }

    public AbsShape(int color) {
        this(false,color,4);
    }

    public AbsShape(int color, int width) {
        this(false,color,width);
    }

    /**
     * @param isFull 是否空心
     * @param color  颜色
     */
    public AbsShape(boolean isFull, int color, int width) {
        this.isFull = isFull;
        this.width = width;
        this.color = color;
        // 去锯齿
        mPaint = new Paint();
        // 去锯齿
        mPaint.setAntiAlias(true);
        // 设置paint的 style 为STROKE：空心
        mPaint.setStyle(Paint.Style.STROKE);
        // 设置paint的外框宽度
        mPaint.setStrokeWidth(width);
        // 获取跟清晰的图像采样
        mPaint.setDither(true);
        // 接合处为圆弧
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        // 设置paint的颜色
        mPaint.setColor(color);
        // 设置paint的 style 为STROKE：空心
        mPaint.setStyle(isFull ? Paint.Style.FILL : Paint.Style.STROKE);
    }

    public float[] getCenterPostion() {
        return new float[]{cx, cy};
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

    public int getCx() {
        return cx;
    }

    public void setCx(int cx) {
        this.cx = cx;
    }

    public int getCy() {
        return cy;
    }

    public void setCy(int cy) {
        this.cy = cy;
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


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        mPaint.setStrokeWidth(width);
        this.width = width;
    }

    public RectF getmInvalidRect() {
        return mInvalidRect;
    }

    public void setmInvalidRect(RectF mInvalidRect) {
        this.mInvalidRect = mInvalidRect;
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