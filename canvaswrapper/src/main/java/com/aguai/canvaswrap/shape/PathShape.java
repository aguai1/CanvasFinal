package com.aguai.canvaswrap.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.aguai.canvaswrap.shape.path.SerializablePath;

import java.util.ArrayList;
import java.util.List;

/**
 * 曲线
 */
public class PathShape extends AbsShape {

    private SerializablePath mPath = new SerializablePath();

    public PathShape(int color, int width, boolean isEraser) {
        this(color, width, isEraser, null);
    }

    public PathShape(int color, int width, boolean isEraser, List<int[]> points) {
        super(color, width);
        mPath = new SerializablePath();
        if (points != null) {
            mPath.addPathPoints(points);
        }
        if (isEraser) {
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setStrokeWidth(width);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        }
    }

    public void onMove(float x, float y) {
        for (Point point : mPath.getPathPoints()) {
            point.x += x;
            point.y += y;
        }
        mPath.loadPathPointsAsQuadTo();
    }

    public void onAddPoint(float x, float y) {
        // 判断是不是down事件
        if (startX == 0 && startY == 0) {
            this.startX = x;
            this.startY = y;
            mPath.addPathPoint(new Point((int) startX, (int) startY));
        }
        int border = (int) mPaint.getStrokeWidth();
        float mCurveEndX = (x + startX) / 2;
        float mCurveEndY = (y + startY) / 2;
        mPath.addQuadPathPoint(new Point((int) mCurveEndX, (int) mCurveEndY));
        startX = x;
        startY = y;
    }

    public void drawShape(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
        super.drawShape(canvas);
    }

    public List<Point> getPoints() {
        return mPath.getPathPoints();
    }
}