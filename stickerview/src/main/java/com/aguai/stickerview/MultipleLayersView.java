package com.aguai.stickerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.aguai.stickerview.bean.config.FrontItem;
import com.aguai.stickerview.bean.config.FrontPicItem;
import com.aguai.stickerview.bean.Sticker;
import com.aguai.stickerview.bean.config.StickerItem;
import com.aguai.stickerview.bean.TextSticker;
import com.aguai.stickerview.imagecache.WBImageLoader;
import com.aguai.stickerview.utils.BitmapUtils;
import com.aguai.stickerview.utils.L;
import com.aguai.stickerview.utils.PointUtils;
import com.aguai.stickerview.utils.TxtBitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by whb on 17-7-28.
 * 多图层View
 * setBackBitmap设置背景图片
 * addSticker增加图层
 * getFocusSticker获取操作图层在继承类中修改样式
 */
public class MultipleLayersView extends BaseCPUView {
    /**
     * 操作模式
     * 1 贴纸
     * 2 默认
     * 3 画笔
     */
    public static final int MODE_STICKERS_SEL = 2;
    public static final int MODE_NONE = 3;
    private static final String TAG = "MultipleLayersView";
    /**
     * 图层最大，最小放大倍数
     */
    private static final float MAX_SCALE_SIZE = 7.0f;
    private static final float MIN_SCALE_SIZE = 0.3f;
    private String MultipleLayersView_FILE_URL = "MultipleLayersView";
    private boolean lBEnable = true;
    private boolean rBEnable = true;
    private boolean lTEnable = true;
    private boolean rTEnable = true;
    private int editCount = 0;
    /**
     * 最后一次点坐标
     */
    private float mLastPointX, mLastPointY;

    private int CURRENT_MODE = MODE_NONE;
    /**
     * 选中的图层的对角线距离
     */
    private float mDeviation;
    private Bitmap mControllerBitmap, mDeleteBitmap, mFlipBitmap, mDownBitmap;

    private float mControllerWidth, mControllerHeight, mDeleteWidth, mDeleteHeight;
    private float mDownWidth, mDownHeight, mFlipWidth, mFlipHeight;
    //在控制区域内
    private boolean mInController = false;
    private boolean mInDelete = false;
    private boolean mInFlip = false;
    private boolean mInDown = false;


    private OnClickStickListener onClickStickListener;
    private OnViewInitListener onViewInitListener;
    /**
     * 框架区域
     */
    private RectF mViewRect;

    //贴纸集合
    private List<Sticker> stickers = new ArrayList<>();
    /**
     * 焦点贴纸索引
     */
    private int focusStickerPosition = -1;

    private Context mContext;

    private boolean resetWidth = false;

    private int moveTime = 0;
    private int lastFocusePostion;
    private float mViewBitmapRatio = 1.0f;
    private OnBitmapRectMeasureSucceedListener rectMeasureSucceedListener;
    private boolean noCallRectCallBack = true;
    private boolean hasChangeViewWidth;

    public MultipleLayersView(Context context) {
        this(context, null);
    }

    public MultipleLayersView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleLayersView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        MultipleLayersView_FILE_URL = System.currentTimeMillis()+"MultipleLayersView";
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MultipleLayersView, defStyle, 0);
        int lBRes = ta.getResourceId(R.styleable.MultipleLayersView_LB_Button, 0);
        int rBRes = ta.getResourceId(R.styleable.MultipleLayersView_RB_Button, 0);
        int lTRes = ta.getResourceId(R.styleable.MultipleLayersView_LT_Button, 0);
        int rTRes = ta.getResourceId(R.styleable.MultipleLayersView_RT_Button, 0);


        lBEnable = ta.getBoolean(R.styleable.MultipleLayersView_LB_Button_Enable, true);
        rBEnable = ta.getBoolean(R.styleable.MultipleLayersView_RB_Button_Enable, true);
        lTEnable = ta.getBoolean(R.styleable.MultipleLayersView_LT_Button_Enable, true);
        rTEnable = ta.getBoolean(R.styleable.MultipleLayersView_RT_Button_Enable, true);
        ta.recycle();
        init(lBRes, rBRes, lTRes, rTRes);

    }

    private void init(int lBRes, int rBRes, int lTRes, int rTRes) {
        L.e(TAG, "init");
//        mControllerBitmap = ThemeIconUtils.getControlIcon(mContext);
        if (lBRes != 0) {
            L.d(TAG, "lBRes is not set");
//            mDownBitmap = ThemeIconUtils.getDownIcon(mContext);
            mDownBitmap = BitmapUtils.readBitMap(getContext(), lBRes);
            mDownWidth = mDownBitmap.getWidth();
            mDownHeight = mDownBitmap.getHeight();
        }
        if (rBRes != 0) {
            L.d(TAG, "rBRes is not set");

            mControllerBitmap = BitmapUtils.readBitMap(getContext(), rBRes);
            mControllerWidth = mControllerBitmap.getWidth();
            mControllerHeight = mControllerBitmap.getHeight();
        }
        if (lTRes != 0) {
            L.d(TAG, "lTRes is not set");
//        mDeleteBitmap = ThemeIconUtils.getDeleteIcon(mContext);
            mDeleteBitmap = BitmapUtils.readBitMap(getContext(), lTRes);
            mDeleteWidth = mDeleteBitmap.getWidth();
            mDeleteHeight = mDeleteBitmap.getHeight();
        }
        if (rTRes != 0) {
            L.d(TAG, "rTRes is not set");
//        mFlipBitmap = ThemeIconUtils.getSymmetryIcon(mContext);
            mFlipBitmap = BitmapUtils.readBitMap(getContext(), rTRes);
            mFlipWidth = mFlipBitmap.getWidth();
            mFlipHeight = mFlipBitmap.getHeight();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Bitmap bgBitmap = WBImageLoader.getInstance().loadBitmap(MultipleLayersView_FILE_URL);
        if (!resetWidth) {
            if (bgBitmap != null && !bgBitmap.isRecycled()) {
                resetWidth = true;
                int oldWidth = bgBitmap.getWidth();
                //调整控件大小和bitmap大小等大。
                bgBitmap = BitmapUtils.adjustViewAndBitmap(bgBitmap, this, true);
                WBImageLoader.getInstance().saveBitmap(bgBitmap, MultipleLayersView_FILE_URL, Bitmap.CompressFormat.PNG);
                mViewBitmapRatio = 1f * bgBitmap.getWidth() / oldWidth;
            }
        } else {
            hasChangeViewWidth = true;
        }



        if (onViewInitListener!=null&&bgBitmap!=null&&bgBitmap.getWidth()/bgBitmap.getHeight() ==getMeasuredWidth()/getMeasuredHeight()){
            onViewInitListener.onInitEd(getMeasuredWidth(),getMeasuredHeight());
            onViewInitListener=null;
        }
        if (mViewRect == null) {
            mViewRect = new RectF(0f, 0f, getMeasuredWidth(), getMeasuredHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (hasChangeViewWidth && noCallRectCallBack && rectMeasureSucceedListener != null) {
            noCallRectCallBack = false;
            Rect rect = new Rect(getLeft(), getTop(), getLeft() + getMeasuredWidth(), getTop() + getMeasuredHeight());
            rectMeasureSucceedListener.onSucceed(rect);
        }
    }

    public void setRectMeasureSucceedListener(OnBitmapRectMeasureSucceedListener rectMeasureSucceedListener) {
        this.rectMeasureSucceedListener = rectMeasureSucceedListener;
    }

    public void addLayer(String str, FrontPicItem frontPicItem, OnStickerAddListener onStickerAddListener) {
        TxtBitmapUtil.getTextBitmap(str, frontPicItem, new LoadCallBack() {
            @Override
            public void loadSucceed(Bitmap bitmap) {

                ++editCount;
                TextSticker textSticker = new TextSticker(MultipleLayersView.this, str, frontPicItem, bitmap, getMeasuredWidth(), getMeasuredHeight());
                stickers.add(textSticker);
                setFocusStickerIndex(stickers.size() - 1);
                setCurrentMode(MODE_STICKERS_SEL);
                postInvalidate();
                onStickerAddListener.onStickerAddCallBack(textSticker);
            }

            @Override
            public void loadError(String s) {

            }
        });

    }

    public void replaceTextStickerTxt(TextSticker oldSticker, String str) {
        oldSticker.replaceText(str);
        postInvalidate();
    }

    /**
     * 增加图层
     * @param index
     * @param bitmap
     */
    public Sticker addLayer(int index, Bitmap bitmap) {
        ++editCount;
        Bitmap originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Sticker sticker = new Sticker(this, originalBitmap, getMeasuredWidth(), getMeasuredHeight());
        stickers.add(index, sticker);
        setFocusStickerIndex(index);
        postInvalidate();
        setCurrentMode(MODE_STICKERS_SEL);
        return sticker;

    }

    /**
     * 增加贴纸图册
     */
    public Sticker addLayer(Bitmap bitmap) {
        ++editCount;
        Bitmap originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Sticker sticker = new Sticker(this, originalBitmap, getMeasuredWidth(), getMeasuredHeight());
        stickers.add(sticker);
        setFocusStickerIndex(stickers.size() - 1);
        postInvalidate();
        setCurrentMode(MODE_STICKERS_SEL);
        return sticker;
    }

    public void addLayer(StickerItem stickerItem) {
        ++editCount;
        Sticker sticker = null;
        try {
            sticker = new Sticker(this, stickerItem, mFaceInfo, mViewBitmapRatio, getMeasuredWidth(), getMeasuredHeight());
            stickers.add(sticker);
            setFocusStickerIndex(stickers.size() - 1);
            postInvalidate();
            setCurrentMode(MODE_STICKERS_SEL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置背景图片
     *
     * @param bmp
     */
    public void setBackGroundBitmap(Bitmap bmp) {
        Bitmap bgBitmap = BitmapUtils.copyBitmap(bmp);

        if (getWidth() != 0) {
            resetWidth = true;
            bgBitmap = BitmapUtils.adjustViewAndBitmap(bgBitmap, this, true);
        }
        WBImageLoader.getInstance().saveBitmap(bgBitmap, MultipleLayersView_FILE_URL, Bitmap.CompressFormat.PNG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bgBitmap = WBImageLoader.getInstance().loadBitmap(MultipleLayersView_FILE_URL);
        if (bgBitmap != null) {
            canvas.drawBitmap(bgBitmap, 0, 0, null);
        }
        if (displayOriginPic) {
            return;
        }

//        L.i("mem","0系统剩余内存:"+ getMem() +"k, stickers.size=" + stickers.size());

        try {
            for (int i = 0; i < stickers.size(); i++) {
                float[] mapPointsDst = stickers.get(i).getMapPointsDst();
                Bitmap bitmap = stickers.get(i).getBitmap();
                if (bitmap == null) {
                    continue;
                }
                canvas.drawBitmap(bitmap, stickers.get(i).getmMatrix(), null);
                if (CURRENT_MODE == MODE_STICKERS_SEL && i == focusStickerPosition) {
                    canvas.drawLine(mapPointsDst[0], mapPointsDst[1], mapPointsDst[2], mapPointsDst[3], stickers.get(i).getmBorderPaint());
                    canvas.drawLine(mapPointsDst[2], mapPointsDst[3], mapPointsDst[4], mapPointsDst[5], stickers.get(i).getmBorderPaint());
                    canvas.drawLine(mapPointsDst[4], mapPointsDst[5], mapPointsDst[6], mapPointsDst[7], stickers.get(i).getmBorderPaint());
                    canvas.drawLine(mapPointsDst[6], mapPointsDst[7], mapPointsDst[0], mapPointsDst[1], stickers.get(i).getmBorderPaint());
                    if (mControllerBitmap != null) {
                        canvas.drawBitmap(mControllerBitmap, mapPointsDst[4] - mControllerWidth / 2, mapPointsDst[5] - mControllerHeight / 2, null);
                    }
                    if (mDeleteBitmap != null) {
                        canvas.drawBitmap(mDeleteBitmap, mapPointsDst[0] - mDeleteWidth / 2, mapPointsDst[1] - mDeleteHeight / 2, null);
                    }
                    if (mFlipBitmap != null) {
                        canvas.drawBitmap(mFlipBitmap, mapPointsDst[2] - mFlipWidth / 2, mapPointsDst[3] - mFlipHeight / 2, null);
                    }
                    if (mDownBitmap != null) {
                        canvas.drawBitmap(mDownBitmap, mapPointsDst[6] - mDownWidth / 2, mapPointsDst[7] - mDownHeight / 2, null);
                    }
                }
                if (false) {
                    stickers.get(i).drawDebug(canvas);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        L.i("mem","1系统剩余内存:"+ getMem() +"k");
    }

//    long getMem() {
//        final ActivityManager activityManager = (ActivityManager) getContext().getSystemService(ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
//        activityManager.getMemoryInfo(info);
//
//        return (info.availMem >> 10);
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {


        try {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return downAction(motionEvent);
                case MotionEvent.ACTION_MOVE:
                    return moveAction(motionEvent);


                case MotionEvent.ACTION_UP:
                    return upAction(motionEvent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;

    }

    private boolean downAction(MotionEvent motionEvent) {
        L.e(TAG, "onDragPointBegin");
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        moveTime = 0;
        mLastPointX = x;
        mLastPointY = y;
        if (isInController(x, y)) {
            mInController = true;
            if (onClickStickListener != null) {
                onClickStickListener.onClickRBButton();
            }
            float nowLenght = caculateLength(stickers.get(focusStickerPosition).getMapPointsDst()[0], stickers.get(focusStickerPosition).getMapPointsDst()[1]);
            float touchLenght = caculateLength(x, y);
            mDeviation = touchLenght - nowLenght;
            return true;
        }

        if (isInDelete(x, y)) {
            mInDelete = true;
            return true;
        }

        if (isInfilp(x, y)) {
            mInFlip = true;
            return true;
        }


        if (isIndown(x, y)) {
            mInDown = true;
            return true;
        }
        return true;
    }

    private boolean moveAction(MotionEvent motionEvent) {
        L.e(TAG, "onScroll");
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        moveTime++;
        if (focusStickerPosition == -1 || MODE_STICKERS_SEL == MODE_NONE) {
            return true;
        }
        if (mInController) {
            stickers.get(focusStickerPosition).postRotate(rotation(motionEvent));
            float nowLenght = caculateLength(stickers.get(focusStickerPosition).getMapPointsDst()[0], stickers.get(focusStickerPosition).getMapPointsDst()[1]);
            float touchLenght = caculateLength(x, y) - mDeviation;
            if (Math.sqrt((nowLenght - touchLenght) * (nowLenght - touchLenght)) > 0.0f) {
                float scale = touchLenght / nowLenght;
                float nowsc = stickers.get(focusStickerPosition).getScaleSize() * scale;
                if (nowsc >= MIN_SCALE_SIZE && nowsc <= MAX_SCALE_SIZE) {
                    stickers.get(focusStickerPosition).postScale(scale, scale);
                    stickers.get(focusStickerPosition).setScaleSize(nowsc);
                }
            }
            postInvalidate();
            mLastPointX = x;
            mLastPointY = y;
            return true;
        }
        float cX = x - mLastPointX;
        float cY = y - mLastPointY;
        mInController = false;

        if (Math.sqrt(cX * cX + cY * cY) > 2.0f && canStickerMove(cX, cY)) {
            stickers.get(focusStickerPosition).postTranslate(cX, cY);
            postInvalidate();
            mLastPointX = x;
            mLastPointY = y;
        }
        return true;
    }

    private boolean upAction(MotionEvent motionEvent) {
        L.e(TAG, "onSingleTapUp");
        if(CURRENT_MODE != MODE_NONE) {
            ++editCount;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        //单击消息
        if (moveTime < 10) {
            if (isInDelete(x, y) && mInDelete) {
                doDeleteSticker();
            } else if (isInfilp(x, y) && mInFlip) {
                doflipSticker();
            } else if (isIndown(x, y) && mInDown) {
                doDownSticker();
            } else {
                int inStickerArea = isInStickerArea(x, y);
                if (inStickerArea != -1) {
                    setFocusStickerIndex(inStickerArea);
                    if (lastFocusePostion == inStickerArea) {
                        if (onClickStickListener != null) {
                            onClickStickListener.onClickSticker(stickers.get(inStickerArea), inStickerArea);
                        }
                    }
                    lastFocusePostion = inStickerArea;
                    setCurrentMode(MODE_STICKERS_SEL);
                } else {
                    lastFocusePostion = -1;
                    if (onClickStickListener != null) {
                        onClickStickListener.onClickOtherPostion();
                    }
                    setCurrentMode(MODE_NONE);
                    setFocusStickerIndex(-1);
                }
            }
        }
        mLastPointX = 0;
        mLastPointY = 0;
        mInController = false;
        mInDelete = false;
        mInDown = false;
        mInFlip = false;
        postInvalidate();
        return true;
    }

    private void doDownSticker() {
        if (onClickStickListener != null) {
            if (onClickStickListener.onClickLBButton()) {
                return;
            }
        }
        if (focusStickerPosition > 0) {
            Sticker sticker = stickers.get(focusStickerPosition);
            stickers.set(focusStickerPosition, stickers.get(focusStickerPosition - 1));
            stickers.set(focusStickerPosition - 1, sticker);
            focusStickerPosition = focusStickerPosition - 1;
            postInvalidate();
        } else {
            Sticker sticker = stickers.get(focusStickerPosition);
            stickers.remove(focusStickerPosition);
            stickers.add(sticker);
            focusStickerPosition = stickers.size() - 1;
            postInvalidate();
        }
    }

    private void doflipSticker() {
        if (onClickStickListener != null) {
            if (onClickStickListener.onClickRTButton()) {
                return;
            }
        }
        Bitmap bitmap = BitmapUtils.getFilpBitmap(stickers.get(focusStickerPosition).getBitmap(), false);
        stickers.get(focusStickerPosition).setBitmap(bitmap);
        postInvalidate();
    }

    /**
     * 是否在控制点区域
     */
    private boolean isInController(float x, float y) {
        if (focusStickerPosition < 0 || !rBEnable) {
            return false;
        }
        int position = 4;
        float rx = stickers.get(focusStickerPosition).getMapPointsDst()[position];
        float ry = stickers.get(focusStickerPosition).getMapPointsDst()[position + 1];
        RectF rectF = new RectF(rx - mControllerWidth / 2 - 15,
                ry - mControllerHeight / 2 - 15,
                rx + mControllerWidth / 2 + 15,
                ry + mControllerHeight / 2 + 15);
        return rectF.contains(x, y);

    }

    /**
     * 是否在控制点区域
     */
    private boolean isIndown(float x, float y) {
        if (focusStickerPosition < 0 || !lBEnable) {
            return false;
        }
        int position = 6;
        float rx = stickers.get(focusStickerPosition).getMapPointsDst()[position];
        float ry = stickers.get(focusStickerPosition).getMapPointsDst()[position + 1];
        RectF rectF = new RectF(rx - mDownWidth / 2 - 15,
                ry - mDownHeight / 2 - 15,
                rx + mDownWidth / 2 + 15,
                ry + mDownHeight / 2 + 15);
        return rectF.contains(x, y);

    }

    /**
     * 是否在控制点区域
     */
    private boolean isInfilp(float x, float y) {
        if (focusStickerPosition < 0 || !rTEnable) {
            return false;
        }
        int position = 2;
        float rx = stickers.get(focusStickerPosition).getMapPointsDst()[position];
        float ry = stickers.get(focusStickerPosition).getMapPointsDst()[position + 1];
        RectF rectF = new RectF(rx - mFlipWidth / 2 - 20,
                ry - mFlipHeight / 2 - 20,
                rx + mFlipWidth / 2 + 20,
                ry + mFlipHeight / 2 + 20);
        return rectF.contains(x, y);

    }

    /**
     * 是否在删除点区域
     */
    private boolean isInDelete(float x, float y) {
        if (focusStickerPosition < 0 || !lTEnable) {
            return false;
        }
        int position = 0;
        float rx = stickers.get(focusStickerPosition).getMapPointsDst()[position];
        float ry = stickers.get(focusStickerPosition).getMapPointsDst()[position + 1];
        RectF rectF = new RectF(rx - mDeleteWidth / 2 - 20,
                ry - mDeleteHeight / 2 - 20,
                rx + mDeleteWidth / 2 + 20,
                ry + mDeleteHeight / 2 + 20);
        return rectF.contains(x, y);

    }

    /**
     * 删除所有贴纸
     */
    private void doDeleteSticker() {

        if (onClickStickListener != null) {
            if (onClickStickListener.onClickLTButton()) {
                return;
            }
        }
        stickers.remove(focusStickerPosition);
        focusStickerPosition = stickers.size() - 1;

        postInvalidate();
    }

    public void clear() {
        stickers.clear();
        postInvalidate();
    }

    /**
     * 判断贴纸是否可以移动，如果贴纸中点在边界则不能移动
     */
    private boolean canStickerMove(float cx, float cy) {
//        float px = cx + stickers.get(focusStickerPosition).getMapPointsDst()[icon_close];
//        float py = cy + stickers.get(focusStickerPosition).getMapPointsDst()[9];
//        if (mViewRect.contains(px, py)) {
//            return true;
//        } else {
//            return false;
//        }
        return true;
    }

    /**
     * 计算x，y距离中点的距离
     */
    private float caculateLength(float x, float y) {
        return (float) PointUtils.lineSpace(x, y, stickers.get(focusStickerPosition).getMapPointsDst()[8], stickers.get(focusStickerPosition).getMapPointsDst()[9]);
    }

    private float rotation(MotionEvent event) {
        float originDegree = calculateDegree(mLastPointX, mLastPointY);
        float nowDegree = calculateDegree(event.getX(), event.getY());
        return nowDegree - originDegree;
    }

    /***
     * 计算斜率
     */
    private float calculateDegree(float x, float y) {
        double delta_x = x - stickers.get(focusStickerPosition).getMapPointsDst()[8];
        double delta_y = y - stickers.get(focusStickerPosition).getMapPointsDst()[9];
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 是否点击在贴纸区域
     */
    private int isInStickerArea(double x, double y) {
        for (int i = stickers.size() - 1; i >= 0; i--) {
            Sticker sticker = stickers.get(i);
            if (isInContent(x, y, sticker)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 判断点是否在指定区域内
     */
    private boolean isInContent(double x, double y, Sticker currentSticker) {
        float[] pointsDst = currentSticker.getMapPointsDst();
        PointD p1 = new PointD(pointsDst[0], pointsDst[1]);
        PointD p2 = new PointD(pointsDst[2], pointsDst[3]);
        PointD p3 = new PointD(pointsDst[4], pointsDst[5]);
        PointD p4 = new PointD(pointsDst[6], pointsDst[7]);
        List<PointD> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        return PointUtils.IsPtInPoly(new PointD(x, y), list);
    }

    /**
     * 获取操作图层进行相应操作。
     */
    public Sticker getFocusSticker() {
        if (focusStickerPosition == -1) {
            return null;
        }
        return stickers.get(focusStickerPosition);

    }

    /**
     * 获取操作图层进行相应操作。
     */
    public int getFocusStickerIndex() {
        return focusStickerPosition;

    }

    /**
     * 设置焦点贴纸
     */
    private void setFocusStickerIndex(int position) {
        focusStickerPosition = position;
    }

    /**
     * 获取操作图层进行相应操作。
     */
    public Sticker getSticker(int index) {
        if (stickers.size() <= index) {
            return null;
        }
        return stickers.get(index);

    }

    /**
     * 将当前选中sticker移动到指定位置
     *
     * @param rc
     */
    public void setCurrentStickerRect(Rect rc) {
        if (stickers.size() > 0) {
            Sticker sticker = getFocusSticker();
            if (sticker != null) {
                sticker.getMoveTo(rc);
                postInvalidate();
            }
        }
    }

    public Bitmap getBitmapWithStickerApplied() {
        Bitmap bgBitmap = WBImageLoader.getInstance().loadBitmap(MultipleLayersView_FILE_URL);
        int bgWidth = bgBitmap.getWidth();
        int bgHeight = bgBitmap.getHeight();
        Bitmap newBmp = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newBmp);
        cv.drawBitmap(bgBitmap, 0, 0, null);

        for (int i = 0; i < stickers.size(); i++) {

            cv.drawBitmap(stickers.get(i).getBitmap(), stickers.get(i).getmMatrix(), null);
        }
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
        return newBmp;
    }

    @Override
    protected void onDetachedFromWindow() {
        L.e(TAG, "onDetachedFromWindow");
        super.onDetachedFromWindow();
        BitmapUtils.recycleBitmap(mDeleteBitmap);
        BitmapUtils.recycleBitmap(mDownBitmap);
        BitmapUtils.recycleBitmap(mFlipBitmap);
        BitmapUtils.recycleBitmap(mControllerBitmap);
        mDownBitmap = null;
        mFlipBitmap = null;
        mDeleteBitmap = null;
        mControllerBitmap = null;
    }

    public int getCurrentMode() {
        return CURRENT_MODE;
    }

    public void setCurrentMode(int currentMode) {
        CURRENT_MODE = currentMode;
    }

    public void setOnClickStickListener(OnClickStickListener onClickStickListener) {
        this.onClickStickListener = onClickStickListener;
    }

    public void replacePicStyleInfo(TextSticker textSticker, FrontPicItem imageTxtInfo) {
        if (textSticker != null) {
            ++editCount;
            textSticker.replacePicStyleInfo(imageTxtInfo);
            postInvalidate();
        }
    }

    public void replaceFront(TextSticker currentSticker, FrontItem frontUrl) {
        if (currentSticker != null) {
            currentSticker.replaceFront(frontUrl);
            postInvalidate();
        }
    }

    public void setOnViewInitListener(OnViewInitListener onViewInitListener) {
        this.onViewInitListener = onViewInitListener;
    }

    public int getStickerSize() {
        return stickers.size();
    }



    public int getEditCount() {
        return editCount;
    }

    public interface OnBitmapRectMeasureSucceedListener {
        void onSucceed(Rect rect);
    }


    public interface OnViewInitListener{
        void onInitEd(float width, float height);
    }

    public interface OnStickerAddListener {
        void onStickerAddCallBack(Sticker sticker);
    }
    public interface OnClickStickListener {
        void onClickSticker(Sticker sticker, int pos);

        void onClickOtherPostion();

        boolean onClickLTButton();

        boolean onClickLBButton();

        boolean onClickRTButton();

        boolean onClickRBButton();
    }
}
