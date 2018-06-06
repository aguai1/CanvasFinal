package com.aguai.stickerview.bean.config;


import java.util.List;

/**
 * Created by zhangchao on 17-9-29.
 */
public class FrontPicItem extends BaseItem {
    private String frontUrl;
    private String displayUrl;
    private String color;
    private int size;
    private List<Rect> bitmapRect;

    public List<Rect> getBitmapRect() {
        return bitmapRect;
    }

    public void setBitmapRect(List<Rect> bitmapRect) {
        this.bitmapRect = bitmapRect;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getFrontUrl() {
        return frontUrl;
    }

    public void setFrontUrl(String frontUrl) {
        this.frontUrl = frontUrl;
    }

    public String getFrontUrlName() {
        String[] split = frontUrl.split("/");
        return split[split.length - 1];
    }

    public static class Rect {
        private float left;
        private float right;
        private float top;
        private float bottom;

        public float getLeft() {
            return left;
        }

        public void setLeft(float left) {
            this.left = left;
        }

        public float getRight() {
            return right;
        }

        public void setRight(float right) {
            this.right = right;
        }

        public float getTop() {
            return top;
        }

        public void setTop(float top) {
            this.top = top;
        }

        public float getBottom() {
            return bottom;
        }

        public void setBottom(float bottom) {
            this.bottom = bottom;
        }
    }

}
