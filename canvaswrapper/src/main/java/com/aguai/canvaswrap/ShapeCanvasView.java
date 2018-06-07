package com.aguai.canvaswrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.aguai.canvaswrap.shape.AbsShape;
import com.aguai.canvaswrap.shape.PathShape;
import com.aguai.canvaswrap.utils.BitmapUtils;
import com.aguai.canvaswrap.utils.L;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Aguai on 2016/11/20.
 * 电子白板控件多缓冲线程SurfaceView实现
 */
public class ShapeCanvasView extends View implements IDisplay{

    private static final String TAG = "SfDisplayInfoView";

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    public OpMode currentOpMode = OpMode.MODE_SHOW;
    private float x_down = 0;
    private float y_down = 0;
    private float oldDist = 1f;
    private Bitmap mBitmap;
    private Canvas bitmapCanvas;

    /**
     * 图片Matrix
     */
    private Matrix matrix = new Matrix();
    //零时Matrix
    private Matrix matrix1 = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private PointF mid = new PointF();
    /**
     * 画笔属性
     */
    private int brushWidth = 15;
    private int brushColor = Color.BLACK;


    private int backgroundColor = Color.WHITE;
    /**
     * shape集合
     */
    private List<AbsShape> absShapeList = new ArrayList<>();

    /**
     * 显示队列
     */
    private Queue<AbsShape> loadQueue = new ArrayDeque<>();

    private boolean isLoadHisSucceed = false;
    private int bitmapWidth = 2000;
    private int bitmapHeight = 1000;
    private PathShape tempPaintShape;

    private OnDrawLineListener onDrawLineListener;

    public ShapeCanvasView(Context context) {
        super(context);
        init(context);
    }

    public ShapeCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setSizes(getMeasuredWidth(), getMeasuredHeight());
        moveToCenter();
    }

    @Override
    public void addShape(final AbsShape shape) {
        loadQueue.add(shape);
        absShapeList.add(shape);
        drawNewShapes();
    }


    @Override
    public void addShapes(List<AbsShape> shapes) {
        loadQueue.addAll(shapes);
        absShapeList.addAll(shapes);
        drawNewShapes();
    }

    private void drawNewShapes() {
        while (!loadQueue.isEmpty()) {
            AbsShape shape = loadQueue.poll();
            if (bitmapCanvas != null) {
                shape.drawShape(bitmapCanvas);
            }
            myInvalidate();
        }
    }

    /**
     * 绘制后直接显示
     */
    public void addShapesAndShowResult(List<AbsShape> shapes) {
        absShapeList.addAll(shapes);
        refresh();
    }


    @Override
    public void removeAllShape() {
        absShapeList.clear();
        refresh();
    }

    @Override
    public void moveShape(AbsShape absShape, final int dx, final int dy) {

    }

    @Override
    public void refresh() {
        if (mBitmap == null) {
            mBitmap = BitmapUtils.createBitmap(bitmapWidth, bitmapHeight);
            bitmapCanvas = new Canvas(mBitmap);
        }
//        mBitmap.eraseColor(backgroundColor);
        for (int i = 0; i < absShapeList.size(); ++i) {
            AbsShape absShape = absShapeList.get(i);
            if (absShape != null)
                absShape.drawShape(bitmapCanvas);
        }
        isLoadHisSucceed = true;
        myInvalidate();
        moveToCenter();
    }


    public void setSizes(int w, int h) {
        bitmapHeight = h;
        bitmapWidth = w;
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        mBitmap = null;
        refresh();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isLoadHisSucceed) return false;
        if (currentOpMode == OpMode.MODE_STATIC) return false;
        if (currentOpMode == OpMode.MODE_PAINT || OpMode.MODE_ERASER == currentOpMode) {
            return paintUtils(event);
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                x_down = event.getX();
                y_down = event.getY();
                savedMatrix.set(matrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                oldDist = spacing(event);
                savedMatrix.set(matrix);
                midPoint(mid, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    matrix1.set(savedMatrix);
                    float newDist = spacing(event);
                    float scale = newDist / oldDist;
                    matrix1.postScale(scale, scale, mid.x, mid.y);// 縮放
                    matrix.set(matrix1);
                    myInvalidate();
                } else if (mode == DRAG) {
                    matrix1.set(savedMatrix);
                    matrix1.postTranslate(event.getX() - x_down, event.getY()
                            - y_down);// 平移

                    matrix.set(matrix1);
                    myInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        return true;
    }

    /**
     * 触碰两点间距离
     */
    private float spacing(MotionEvent event) {
        double x = event.getX(0) - event.getX(1);
        double y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 取手势中心点
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * 将新加入的点放在中间
     */
    public void moveToVisiableArea(final float[] src) {
        float moveX = 0, moveY = 0;
        float[] f2 = new float[2];
        matrix.mapPoints(f2, src);
        if (f2[0] < 0 || f2[0] > getMeasuredWidth()) {
            moveX = getMeasuredWidth() / 2 - f2[0];
        }
        if (f2[1] < 0 || f2[1] > getMeasuredHeight()) {
            moveY = getMeasuredHeight() / 2 - f2[1];
        }
        if (moveX != 0 || moveY != 0) {
            matrix.postTranslate(moveX, moveY);
            myInvalidate();
        }
    }

    /**
     * 将新加入的点放在中间
     */
    public void moveToCenter() {
        if (getMeasuredHeight() <= 0) return;
        float v = (float) getMeasuredHeight() / bitmapHeight;
        float v1 = (float) getMeasuredWidth() / bitmapWidth;
        float scale = v < v1 ? v : v1;
        matrix = new Matrix();
        matrix.postScale(scale, scale);
        float moveX = getMeasuredWidth() / 2 - bitmapWidth * scale / 2;
        float moveY = getMeasuredHeight() / 2 - bitmapHeight * scale / 2;
        matrix.postTranslate(moveX, moveY);
        myInvalidate();
    }

    /**
     * 零时画笔事件
     */
    private void touchStart(float x, float y) {
        float[] floats = mapPointFromInvertMatrix(x, y);
        tempPaintShape = new PathShape(currentOpMode == OpMode.MODE_PAINT ? brushColor : Color.WHITE, brushWidth, currentOpMode == OpMode.MODE_ERASER);
        tempPaintShape.onAddPoint(floats[0], floats[1]);
    }

    private void touchMove(float x, float y) {
        if (tempPaintShape == null) return;
        float[] floats = mapPointFromInvertMatrix(x, y);
        tempPaintShape.onAddPoint(floats[0], floats[1]);
        tempPaintShape.drawShape(bitmapCanvas);
        myInvalidate();
    }


    private void touchUp(float x, float y) {
        if (tempPaintShape == null) return;
        if (bitmapCanvas == null) return;
        if (onDrawLineListener != null) {
            onDrawLineListener.onNewLine(tempPaintShape);
        }
        touchMove(x, y);
        absShapeList.add(tempPaintShape);
    }

    /**
     * 根据逆矩阵求出映射点
     */
    private float[] mapPointFromInvertMatrix(float x, float y) {
        float[] f = {x, y};
        float[] floats = new float[2];
        Matrix inverMatrix = new Matrix();
        matrix.invert(inverMatrix);
        inverMatrix.mapPoints(floats, f);
        return floats;
    }

    /**
     * 根据矩阵求出映射点
     */
    private float[] mapPointFromMatrix(float x, float y) {
        float[] f = {x, y};
        float[] floats = new float[2];
        matrix.mapPoints(floats, f);
        return floats;
    }

    /**
     * 处理画笔
     */
    private boolean paintUtils(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(x, y);
                break;
        }

        return true;
    }

    public void setCurrentMode(OpMode currentMode) {
        this.currentOpMode = currentMode;
    }

    public void setBgColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBrushColor() {
        return brushColor;
    }

    public void setBrushColor(int brushColor) {
        this.brushColor = brushColor;
    }

    public int getBrushWidth() {
        return brushWidth;
    }

    public void setBrushWidth(int brushWidth) {
        this.brushWidth = brushWidth;
    }

    /**
     * 刷新数据
     */
    private void myInvalidate() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (mBitmap == null) {
                refresh();
            }
            //获取canvas实例
            canvas.drawColor(backgroundColor);
            //将屏幕设置为白色
            canvas.drawBitmap(mBitmap, matrix, null);
        } catch (Exception e) {
            L.e(TAG, e.toString());
        }
    }

    public void setOnDrawLineListener(OnDrawLineListener onDrawLineListener) {
        this.onDrawLineListener = onDrawLineListener;
    }

    /**
     * 划线接口
     */
    public interface OnDrawLineListener {
        void onNewLine(PathShape lineShape);
    }
}
