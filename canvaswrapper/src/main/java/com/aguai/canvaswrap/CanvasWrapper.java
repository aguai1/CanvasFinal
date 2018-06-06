package com.aguai.canvaswrap;

/**
 * ================================================
 * 作    者：aguai（吴红斌）Github地址：https://github.com/aguai1
 * 版    本：1.0
 * 创建日期：18-6-6
 * 描    述：
 * ================================================
 */
public class CanvasWrapper {
    private static CanvasWrapper instance =new CanvasWrapper();
    private IImageLoader iImageLoader;
    public static void init(IImageLoader imageLoader){
        CanvasWrapper instance = getInstance();
        instance.iImageLoader = imageLoader;
    }
    public static CanvasWrapper getInstance() {
        return instance;
    }

    public IImageLoader getImageLoader() {
        return iImageLoader;
    }
}
