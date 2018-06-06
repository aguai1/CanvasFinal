package com.aguai.canvaswrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.aguai.canvaswrap.shape.AbsShape;
import com.aguai.canvaswrap.shape.PathShape;
import com.aguai.canvaswrap.utils.BitmapUtils;
import com.aguai.canvaswrap.utils.L;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Aguai on 2016/11/20.
 * 电子白板控件多缓冲线程SurfaceView实现
 */
public class SfDisplayInfoView extends SurfaceView implements IDisplay, SurfaceHolder.Callback {

    private static final String TAG = "SfDisplayInfoView";
    /**
     * 操作模式
     * 1   画笔
     * 2  橡皮擦
     * 3  展示，可拖动
     * 4  展示，不可拖动
     */
    public static final int MODE_PAINT = 1;
    public static final int MODE_ERASER = 2;
    public static final int MODE_SHOW = 3;
    public static final int MODE_STATIC = 4;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    public static int CURRENT_MODE = MODE_SHOW;
    float x_down = 0;
    float y_down = 0;
    float oldDist = 1f;
    int mode = NONE;
    private Bitmap mBitmap;
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
    //    private Map<String, AbsShape> absShapeHashMap = new ConcurrentHashMap<>();
    private List<AbsShape> absShapeList = new ArrayList<>();
    private Canvas canvas = null; //定义画布

    private ShowThread showThread = null;

    private SurfaceHolder sfh = null;

    /**
     * 显示队列
     */
    private Queue<AbsShape> loadQueue = new ArrayDeque<>();

    private boolean isfinsh = false;
    private boolean isLoadHisSucceed = false;
    private Canvas mCanvas = null; //定义画布

    private int bitmapWidth = 2000;
    private int bitmapHeight = 1000;
    private PathShape tempShape;

    private OnDrawLineListener onDrawLineListener;

    public SfDisplayInfoView(Context context) {
        super(context);
        init(context);
    }

    public SfDisplayInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        showThread = new ShowThread();
        showThread.start();

        sfh = getHolder();
        sfh.addCallback(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        moveToCenter();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isfinsh = false;
        refresh();
        myInvalidate();
        L.e(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        L.e(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isfinsh = true;
        synchronized (showThread) {
            showThread.notifyAll();
        }
        L.e(TAG, "surfaceDestroyed");
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
        if (!loadQueue.isEmpty()) {
            final AbsShape shape = loadQueue.poll();
            if (mCanvas != null)
                shape.drawShape(mCanvas);
            if (CURRENT_MODE == MODE_SHOW)
                moveToVisiableArea(shape.getCenterPostion());
            myInvalidate();
        } else {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 绘制后直接显示
     */
    public void addShapesAndShowResult(List<AbsShape> shapes) {
        for (int i = 0; i < shapes.size(); ++i) {
            AbsShape absShape = shapes.get(i);
            absShapeList.add(absShape);
        }
        refresh();
    }


    @Override
    public void removeAllShape() {
        absShapeList.clear();
        refresh();
    }

    @Override
    public void moveShape(AbsShape absShape, final int x, final int y) {
        if (absShape != null) {
            if (absShape instanceof PathShape) {
                PathShape shape = (PathShape) absShape;
                shape.onLayout(absShape.getStartX() + x, absShape.getStartY() + y);
            } else {
                absShape.onLayout(absShape.getStartX() + x, absShape.getStartY() + y, absShape.getEndx() + x, absShape.getEndy() + y);
            }
            refresh();
        }

    }

    public void adjustShapeBunds(AbsShape absShape, final float startX, final float startY, final float endx, final float endy) {
        if (absShape != null) {
            absShape.onLayout(startX, startY, endx, endy);
            refresh();
        }
    }


    @Override
    public void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap temp = BitmapUtils.createBitmap(bitmapWidth, bitmapHeight);
                temp.eraseColor(Color.WHITE);
                Canvas tempCanvas = new Canvas(temp);

                for (int i = 0; i < absShapeList.size(); ++i) {
                    AbsShape absShape = absShapeList.get(i);
                    if (absShape != null)
                        absShape.drawShape(tempCanvas);
                }
                mBitmap = temp;
                mCanvas = new Canvas(mBitmap);
                isLoadHisSucceed = true;
                myInvalidate();
                moveToCenter();
            }
        }).start();

    }

    /**
     * SfDisplayInfoView异步绘制
     */
    private void myDraw() {
        try {
            if (mBitmap == null) {
                refresh();
            }
            //获取canvas实例
            canvas = sfh.lockCanvas();
            if (canvas != null) {
                //将屏幕设置为白色
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(mBitmap, matrix, null);
            }
        } catch (Exception e) {
            L.e(TAG, e.toString());
        } finally {
            if (canvas != null)
                //将画好的画布提交
                sfh.unlockCanvasAndPost(canvas);
        }
    }

    public void setSizes(int w, int h) {
        bitmapHeight = h;
        bitmapWidth = w;
        refresh();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isLoadHisSucceed) return false;
        if (CURRENT_MODE == MODE_STATIC) return false;
        if (CURRENT_MODE == MODE_PAINT || MODE_ERASER == CURRENT_MODE) {
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
        tempShape = new PathShape(CURRENT_MODE == MODE_PAINT ? brushColor : Color.WHITE, brushWidth, CURRENT_MODE == MODE_ERASER);
        tempShape.setShapeType(Constants.SHAPE_PEN);
        tempShape.onAddPoint(floats[0], floats[1]);
    }

    private void touchMove(float x, float y) {
        if (tempShape == null) return;
        float[] floats = mapPointFromInvertMatrix(x, y);
        tempShape.onAddPoint(floats[0], floats[1]);
        tempShape.drawShape(mCanvas);
        myInvalidate();
    }


    private void touchUp(float x, float y) {
        if (tempShape == null) return;
        if (mCanvas == null) return;
        if (onDrawLineListener != null) {
            onDrawLineListener.onNewLine(tempShape);
        }
        touchMove(x, y);
        absShapeList.add(tempShape);
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

    public void setCurrentMode(int currentMode) {
        CURRENT_MODE = currentMode;
    }

    public int getBrushColor() {
        return brushColor;
    }

    public void setBrushColor(int brushColor) {
        this.brushColor = brushColor;
        setCurrentMode(MODE_PAINT);
    }

    public int getBrushWidth() {
        return brushWidth;
    }

    public void setBrushWidth(int brushWidth) {
        this.brushWidth = brushWidth;
    }

    private void myInvalidate() {
        synchronized (showThread) {
            showThread.notify();
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


    /**
     * 画笔加速线程
     */
    private class ShowThread extends Thread {
        ShowThread() {
            super("ShowThread");
        }

        @Override
        public void run() {
            while (!isfinsh) {
                myDraw();
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
