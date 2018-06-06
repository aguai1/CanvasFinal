package com.aguai.stickerview.bean;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.aguai.stickerview.MultipleLayersView;
import com.aguai.stickerview.bean.config.StickerItem;
import com.ulsee.uups.api.ULSeeFileManager;
import com.ulsee.uups.api.filecache.interfaces.LoadCallBack;
import com.ulsee.uups.api.model.commen.face.FaceInfo;
import com.ulsee.uups.api.model.http.StickerItem;
import com.ulsee.uups.utils.imagecache.WBImageLoader;


/**
 * 贴纸bean
 */
public class Sticker {

    private MultipleLayersView multipleLayersView;
    private int bitmapRate = 3;
    private StickType stickerType = StickType.MODE_STICKER;
    private Matrix mMatrix;
    /**
     * 边框画笔
     */
    private Paint mBorderPaint;
    /**
     * icon_close ,9是中心点坐标
     */
    private float[] mapPointsSrc;
    private float[] mapPointsDst = new float[10];
    /**
     * 目前缩放倍数
     */
    private float scaleSize = 1.0f;
    /**
     * 图片url
     */
    private String url;

    private PointF mCenterPosition;

    /**
     * 贴纸
     */
    public Sticker(MultipleLayersView multipleLayersView, Bitmap bmp, int bgWidth, int bgHeight) {
        this.multipleLayersView = multipleLayersView;
        zoomInit(bmp, bgWidth, bgHeight, bitmapRate);
    }

    /**
     * 表情，拉伸
     */
    public Sticker(MultipleLayersView multipleLayersView, Bitmap bmp, int bgWidth, int bgHeight, StickType mode) {
        this.multipleLayersView = multipleLayersView;
        if (mode == StickType.MODE_TXT) {
            this.bitmapRate = 2;
            zoomInit(bmp, bgWidth, bgHeight, bitmapRate);
        } else {
            this.bitmapRate = 4;
            zoomInit(bmp, bgWidth, bgHeight, bitmapRate);
        }
        this.stickerType = mode;
    }


    public Sticker(MultipleLayersView multipleLayersView, StickerItem stickerItem, FaceInfo faceInfo, float viewBitmapRatio, int measuredWidth, int measuredHeight) {
        this.multipleLayersView = multipleLayersView;
        zoomInit(stickerItem, faceInfo, viewBitmapRatio, measuredWidth, measuredHeight, bitmapRate);
    }

    private void zoomInit(StickerItem stickerItem, FaceInfo faceInfo, float viewBitmapRatio, int screenWidth, int screenHeight, int rate) {
        ULSeeFileManager.getULSeeFileManager().getImage(stickerItem.getServerUrl(), new LoadCallBack() {
            @Override
            public void loadSucceed(Bitmap bmp) {
                if (bmp == null) {
                    return;
                }
                zoomInit(bmp, stickerItem, faceInfo, viewBitmapRatio, screenWidth, screenHeight, rate);
                multipleLayersView.postInvalidate();
            }

            @Override
            public void loadError(String s) {
            }
        });
    }

    private void zoomInit(Bitmap bmp, StickerItem stickerItem, FaceInfo faceInfo, float viewBitmapRatio, int screenWidth, int screenHeight, int rate) {
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setFilterBitmap(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(3.0f);
        mBorderPaint.setColor(Color.WHITE);
        mMatrix = new Matrix();
        float bmpWidth = bmp.getWidth();
        float bmpHeight = bmp.getHeight();

        float w = screenWidth / rate;
        float h = screenHeight / rate;

        float wR = w / bmpWidth;
        float hR = h / bmpHeight;

        mapPointsSrc = new float[]{0, 0, bmpWidth, 0, bmpWidth, bmpHeight, 0, bmpHeight, bmpWidth / 2, bmpHeight / 2};
        getmMatrix().mapPoints(getMapPointsDst(), getMapPointsSrc());

        postScale(Math.min(wR, hR), Math.min(wR, hR));

        float configScale = stickerItem.getScale();
        float faceScale = 1.0f;
        float faceRotate = 0f;
        if(faceInfo != null) {
            faceScale = faceInfo.getFaceWidth() * viewBitmapRatio / screenWidth * 2.0f;
            faceRotate = (float) Math.toDegrees(faceInfo.getEuler().roll);
        }
        postScale(configScale * faceScale, configScale * faceScale);

        float configRotate = stickerItem.getRotate();
        postRotate(configRotate + faceRotate);

        PointF position = stickerItem.getPosition(faceInfo);
        if(position == null) {
            position = new PointF((screenWidth - bmpWidth) / 2, (screenHeight - bmpHeight) / 2);
            mCenterPosition = new PointF(position.x + bmpWidth / 2, position.y + bmpHeight / 2);
        } else {
            mCenterPosition = position;
            mCenterPosition.x *= viewBitmapRatio;
            mCenterPosition.y *= viewBitmapRatio;
            position = new PointF(mCenterPosition.x - bmpWidth / 2, mCenterPosition.y - bmpHeight / 2);
        }
        mMatrix.postTranslate(position.x, position.y);

        getmMatrix().mapPoints(getMapPointsDst(), getMapPointsSrc());

        url = stickerItem.getServerUrl();
        WBImageLoader.getInstance().saveBitmap(bmp, url, Bitmap.CompressFormat.PNG);
    }

    /**
     * @param bmp
     * @param screenWidth  屏幕宽度
     * @param screenHeight 屏幕高度
     * @param rate         缩小到屏幕的比例
     */
    private void zoomInit(Bitmap bmp, String bitmapUrl, int screenWidth, int screenHeight, int rate) {

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setFilterBitmap(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(3.0f);
        mBorderPaint.setColor(Color.WHITE);
        mMatrix = new Matrix();
        float bmpWidth = bmp.getWidth();
        float bmpHeight = bmp.getHeight();

        float w = screenWidth / rate;
        float h = screenHeight / rate;

        float wR = w / bmpWidth;
        float hR = h / bmpHeight;

        mapPointsSrc = new float[]{0, 0, bmpWidth, 0, bmpWidth, bmpHeight, 0, bmpHeight, bmpWidth / 2, bmpHeight / 2};
        getmMatrix().mapPoints(getMapPointsDst(), getMapPointsSrc());

        postScale(Math.min(wR, hR), Math.min(wR, hR));

        PointF position = new PointF((screenWidth - bmpWidth) / 2, (screenHeight - bmpHeight) / 2);
        mCenterPosition = new PointF(position.x + bmpWidth / 2, position.y + bmpHeight / 2);
        mMatrix.postTranslate(position.x, position.y);

        getmMatrix().mapPoints(getMapPointsDst(), getMapPointsSrc());

        url = bitmapUrl;
        WBImageLoader.getInstance().saveBitmap(bmp, url, Bitmap.CompressFormat.PNG);
    }

    private void zoomInit(Bitmap bmp, int screenWidth, int screenHeight, int rate) {
        String bitmapUrl = System.currentTimeMillis() + "sticker";
        zoomInit(bmp, bitmapUrl, screenWidth, screenHeight, rate);
    }

    public float[] getMapPointsDst() {
        return mapPointsDst;
    }

    public float getScaleSize() {
        return scaleSize;
    }

    public void setScaleSize(float scaleSize) {
        this.scaleSize = scaleSize;
    }

    public Bitmap getBitmap() {
        return WBImageLoader.getInstance().loadBitmap(url);
    }

    public void setBitmap(Bitmap bitmap) {
        Bitmap currentBmp = getBitmap();
        float px = bitmap.getWidth();
        float py = bitmap.getHeight();

        int currentBmpWidth = currentBmp.getWidth();
        int currentBmpHeight = currentBmp.getHeight();

        if (currentBmpWidth != px || currentBmpHeight != py) {

            mapPointsSrc = new float[]{0, 0, px, 0, px, py, 0, py, px / 2, py / 2};
            float wLength = currentBmpWidth - px;
            float hLength = currentBmpHeight - py;
            mMatrix.preTranslate(wLength / 2, hLength / 2);
            mMatrix.mapPoints(mapPointsDst, mapPointsSrc);
        }
        WBImageLoader.getInstance().saveBitmap(bitmap, url, Bitmap.CompressFormat.PNG);
        multipleLayersView.postInvalidate();
    }

    public Matrix getmMatrix() {
        return mMatrix;
    }

    public void setmMatrix(Matrix mMatrix) {
        this.mMatrix = mMatrix;
        getmMatrix().mapPoints(getMapPointsDst(), getMapPointsSrc());
    }

    public Paint getmBorderPaint() {
        return mBorderPaint;
    }

    public float[] getMapPointsSrc() {
        return mapPointsSrc;
    }

    public void getMoveTo(Rect rc) {
        getmMatrix().mapPoints(getMapPointsDst(), getMapPointsSrc());

        float transX = rc.left - mapPointsDst[0];

        float transY = rc.top - mapPointsDst[1];

        float scalX = (rc.right - rc.left) / (mapPointsDst[4] - mapPointsDst[0]);

        float scalY = (rc.bottom - rc.top) / (mapPointsDst[5] - mapPointsDst[1]);

        mMatrix.postTranslate(transX, transY);
        mMatrix.postScale(scalX, scalY);

    }

    public void postTranslate(float cX, float cY) {
        mMatrix.postTranslate(cX, cY);
        mCenterPosition.offset(cX, cY);
        mMatrix.mapPoints(mapPointsDst, mapPointsSrc);
    }

    public void postScale(float scale, float scale1) {
        mMatrix.postScale(scale, scale1, mapPointsDst[8], mapPointsDst[9]);
        mMatrix.mapPoints(mapPointsDst, mapPointsSrc);
    }

    public StickType getStickerType() {
        return stickerType;
    }

    public void postRotate(float rotation) {
        mMatrix.postRotate(rotation, mapPointsDst[8], mapPointsDst[9]);
        mMatrix.mapPoints(mapPointsDst, mapPointsSrc);
    }

    public void drawDebug(Canvas canvas) {
        Paint debugPaint = new Paint();
        debugPaint.setStyle(Paint.Style.FILL);
        debugPaint.setColor(Color.RED);
        canvas.drawCircle(mCenterPosition.x, mCenterPosition.y, 10, debugPaint);
    }

    public enum StickType {
        MODE_STICKER,
        MODE_TXT
    }

}
