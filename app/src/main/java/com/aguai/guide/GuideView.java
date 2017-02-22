package com.aguai.guide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsoluteLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 阿怪 on 2017/2/19.
 */
public class GuideView extends View {

    private Map<RectF, Integer> rectIntegerMap = new ConcurrentHashMap<>();
    private Map<Integer, RectF> integerRectMap = new ConcurrentHashMap<>();
    //    映射后的矩形列表
    private List<RectF> rectList = new ArrayList<>();
    //   映射前的矩形列表
    private List<RectF> orgRectList = new ArrayList<>();
    private Bitmap mFgBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private boolean hasShowAll;

    public GuideView(Context context) {
        super(context);
        init();
    }

    public GuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GuideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAlpha(0);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initBg();
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void initBg() {
        mFgBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mFgBitmap);
        mCanvas.drawColor(Color.parseColor("#cc000000"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFgBitmap != null) {
            canvas.drawBitmap(mFgBitmap, 0, 0, null);
        }
    }

    public void addRect(RectF rectF, int id, int border) {
//        计算屏幕位置
        int[] loc = new int[2];
        getLocationOnScreen(loc);
        rectF.left -= loc[0];
        rectF.top -= loc[1];
        rectF.right -= loc[0];
        rectF.bottom -= loc[1];
        orgRectList.add(0,rectF);
        RectF rect = new RectF(rectF.left - border > 10 ? rectF.left - border : 10,
                rectF.top - border > 10 ? rectF.top - border : 10,
                rectF.right + border < getWidth() ? rectF.right + border : getWidth() - 10,
                rectF.bottom + border < getHeight() ? rectF.bottom + border : getHeight() - 10);
        rectIntegerMap.put(rect, id);
        integerRectMap.put(id, rect);
        rectList.add(0, rect);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!hasShowAll) return true;
            for (int i = 0; i < rectList.size(); ++i) {
                if (rectList.get(i).contains(x, y)) {
                    GuideContainer parent = (GuideContainer) getParent();
                    parent.animOut();
                    return false;
                }
            }
        }
        return true;
    }

    public RectF getRectById(int id) {
        return integerRectMap.get(id);
    }

    public void clearRect() {
        rectIntegerMap.clear();
        integerRectMap.clear();
        orgRectList.clear();
        rectList.clear();
        initBg();
        invalidate();
    }

    public void showAllRect() {
        initBg();
        for (int i = 0; i < rectList.size(); ++i) {
            mCanvas.drawRoundRect(rectList.get(i), 10, 10, mPaint);
        }
        invalidate();
    }

    public void showRectByIndex(int i) {
        if (mCanvas == null) return;
        int i1 = rectList.size() - i - 1;
        if (i1 >= rectList.size() || i1 < 0) return;
        mCanvas.drawRoundRect(rectList.get(i1), 10, 10, mPaint);
        invalidate();
    }

    /**
     * 设置是否动画结束 控件是否完全展示
     *
     * @param hasShowAll
     */
    public void setHasShowAll(boolean hasShowAll) {
        this.hasShowAll = hasShowAll;
    }

    public Integer getIdByIndex(int i) {
        int i1 = orgRectList.size() - i-1;
        if (i1>orgRectList.size()) return null;
        return rectIntegerMap.get(rectList.get(i1));

    }

    public RectF getRectByIndex(int i) {
        int i1 = orgRectList.size() - i-1;
        if (i1>orgRectList.size()) return null;
        return orgRectList.get(i1);
    }
}
