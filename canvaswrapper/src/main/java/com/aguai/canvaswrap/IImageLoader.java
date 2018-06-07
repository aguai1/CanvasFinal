package com.aguai.canvaswrap;

import android.graphics.Bitmap;

public interface IImageLoader {
    Bitmap getImageFromUrl(String url);
}