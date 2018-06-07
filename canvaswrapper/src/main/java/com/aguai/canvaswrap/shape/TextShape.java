package com.aguai.canvaswrap.shape;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;

public class TextShape extends AbsShape {
    private final int imageWidth;
    private final int imageHeight;
    private final int baseLine;
    private final String[] split;
    private final TextPaint textPaint;
    private RectF mDrawRect;
    private String text;

    public TextShape(String str, int color,int x,int y) {
        super(true,color,DEFAULT_PAINT_WIDTH);
        text = str;

        this.startX = x;
        this.startY = y;


        mDrawRect = new RectF();

        textPaint = new TextPaint();
        textPaint.setColor(color);
        textPaint.setTextSize(46);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFakeBoldText(true);
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();

        split = str.split("\n");

        int maxIndex = 0;
        for (int i = 0; i < split.length; ++i) {
            if (split[maxIndex].length() < split[i].length()) {
                maxIndex = i;
            }
        }
        //measureText = bound.right - bound.left + 字符两边的留白宽度
//        宽度获取方法：Paint.measureText(text)
//        高度获取方法：descent+Math.abs(ascent)
        imageWidth = (int) textPaint.measureText(split[maxIndex]);
        imageHeight = fontMetrics.descent + Math.abs(fontMetrics.ascent);
        baseLine = 0 - fontMetrics.ascent;
        this.endx = x+imageWidth;
        this.endy = y+imageHeight;


    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void onLayout(float startX, float startY, float x, float y) {

        mDrawRect.set(startX, startY, x, y);
    }

    public void drawShape(Canvas canvas) {
        for (int i=0;i<split.length;++i){
            canvas.drawText(split[i], startX, startY+imageHeight * i + baseLine, textPaint);
        }
        super.drawShape(canvas);
    }

}