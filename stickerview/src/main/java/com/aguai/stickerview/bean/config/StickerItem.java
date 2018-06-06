package com.aguai.stickerview.bean.config;

import android.graphics.PointF;

import com.aguai.stickerview.bean.config.BaseItem;
import com.ulsee.uups.api.model.commen.face.FaceInfo;

import java.util.List;

/**
 * ================================================
 * 作    者：aguai（吴红斌）Github地址：https://github.com/aguai1
 * 版    本：1.0
 * 创建日期：17-11-21
 * 描    述：
 * ================================================
 */
public class StickerItem extends BaseItem {
    private List<Integer> points;
    private float offsetX;
    private float offsetY;
    private float scale = 1.0f;
    private float rotate;

    public List<Integer> getPoints() {
        return points;
    }

    public void setPoints(List<Integer> points) {
        this.points = points;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getRotate() {
        return rotate;
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
    }

    public PointF getPosition(FaceInfo faceInfo) {
        if(points == null || points.size() == 0 || faceInfo == null) {
            return null;
        }
        float x, y;

        float xsum = 0f;
        float ysum = 0f;
        float[] point = faceInfo.getPoints();
        for(int index : points) {
            xsum += point[index*2];
            ysum += point[index*2 + 1];
        }
        x = xsum / points.size();
        y = ysum / points.size();

        // TODO 可以通过3d角度还原侧脸
        PointF baseVectorX = new PointF(point[32*2] - point[0*2], point[32*2 + 1] - point[0*2 + 1]);
        PointF baseVectorY = new PointF(point[16*2] - point[43*2], point[16*2 + 1] - point[43*2 + 1]);

        PointF vectorX = new PointF(baseVectorX.x * offsetX, baseVectorX.y * offsetX);
        PointF vectorY = new PointF(baseVectorY.x * offsetY, baseVectorY.y * offsetY);

        return new PointF(x + vectorX.x + vectorY.x, y + vectorX.y + vectorY.y);
    }
}
