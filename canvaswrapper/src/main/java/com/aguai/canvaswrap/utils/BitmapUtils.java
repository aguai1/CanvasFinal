package com.aguai.canvaswrap.utils;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by whb on 2017/7/4.
 */
public class BitmapUtils {

    public static Bitmap getFitSampleBitmap(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(imagePath, options);
        options.inSampleSize = getFitInSampleSize(width, height, options);
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        if(bitmap != null) {
            bitmap = BitmapUtils.getSmallImage(bitmap, width, height, true);
            int bitmapDegree = BitmapUtils.getBitmapDegree(imagePath);
            bitmap = BitmapUtils.roateBitmap(bitmap, bitmapDegree, true);
        }
        return bitmap;
    }
    public static Bitmap getOriginBitmap(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if(bitmap != null) {
            int bitmapDegree = BitmapUtils.getBitmapDegree(imagePath);
            bitmap = BitmapUtils.roateBitmap(bitmap, bitmapDegree, true);
        }
        return bitmap;
    }

    /**
     * 高度，宽度不超过 指定宽度，高度
     * @param bitmap
     * @param mWidth
     * @param mHight
     * @param isRecycle
     * @return
     */
    public static Bitmap getSmallImage(Bitmap bitmap, int mWidth, int mHight, boolean isRecycle) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float scaleH = (float) mHight / h;
        float scaleW = (float) mWidth / w;
        Matrix matrix = new Matrix();
        float min = Math.min(scaleH, scaleW);
        if (min >= 1){
            return bitmap;
        }
        matrix.postScale(min, min);
        return createBitmapByMatrix(bitmap, matrix, true, isRecycle);
    }


    public static int getFitInSampleSize(int reqWidth, int reqHeight, BitmapFactory.Options options) {
        int w = options.outWidth;
        int h = options.outHeight;
        int zoomRatio = 1;
        if (w > h && w > reqWidth) {
            zoomRatio = w / reqWidth;
        } else if (w < h && h > reqHeight) {
            zoomRatio = h / reqHeight;
        }
        return zoomRatio;
    }

    public static Bitmap createBitmap(int w, int h) {
        return Bitmap.createBitmap(w, h,
                Bitmap.Config.ARGB_8888);
    }

    public static Bitmap createBitmapByMatrix(Bitmap source,
                                              Matrix m, boolean filter, boolean isRecycle) {
        Bitmap result = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), m, filter);
        // createBitmapByMatrix 有可能会直接返回source出来，所以此处需要判断result和source是否相同
        if (isRecycle && (result != source)) {
            recycleBitmap(source);
        }
        return result;
    }

    /**
     * 通过id读取图片
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(resId);
        return drawable.getBitmap();
    }

    public static Bitmap copyBitmap(Bitmap srcBmp) {
        return srcBmp.copy(srcBmp.getConfig(), true);
    }


    public static String saveBitmap(Context context, Bitmap bmp) throws IOException {
        long dataTake = System.currentTimeMillis();
        String filePath = Constants.getBitmapPath();

        String fileName = "IMG_" + dataTake + ".jpg";
        File photoFile;
       photoFile = savePhotoToSDCard(bmp, filePath, fileName);
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), filePath, fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(photoFile);
        intent.setData(uri);
        context.sendBroadcast(intent);
        assert photoFile != null;
        return photoFile.getAbsolutePath();
    }

    public static String saveBitmap(Context context, Bitmap b, String name) throws IOException {
        return saveBitmap(context, b, name, Bitmap.CompressFormat.JPEG);
    }

    public static String saveBitmap(Context context, Bitmap b, String name, Bitmap.CompressFormat format) {
        File file = new File(name);
        File photoFile = savePhotoToSDCard(b, file.getParent(), file.getName(), format);
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getParent(), file.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(photoFile);
        intent.setData(uri);
        context.sendBroadcast(intent);
        assert photoFile != null;
        return photoFile.getAbsolutePath();
    }
    /**
     * Save image to the SD card
     *
     * @param photoBitmap 相片文件本身
     * @param photoName   储存的相片名字
     * @param path        储存的相片路径
     */
    public static File savePhotoToSDCard(Bitmap photoBitmap, String path, String photoName) {
        return savePhotoToSDCard(photoBitmap, path, photoName, Bitmap.CompressFormat.JPEG);
    }

    private static File savePhotoToSDCard(Bitmap photoBitmap, String path, String photoName, Bitmap.CompressFormat format) {
        if (checkSDCardAvailable()) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File photoFile = new File(path, photoName);

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap.compress(format, 100, fileOutputStream)) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                return photoFile;
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    /**
     * Check the SD card
     *
     * @return 是否获能获取到SD卡
     */
    public static boolean checkSDCardAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }




    public static Bitmap getScaleBitmap(Bitmap bitmap, float rate, boolean isRecycle) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(rate, rate);
        return createBitmapByMatrix(bitmap, matrix, true, isRecycle);
    }



    public static Bitmap getFilpBitmap(Bitmap bitmap, boolean isRecycle) {
        Matrix matrix = new Matrix();
        // sw sh的绝对值为绽放宽高的比例，sw为负数表示X方向翻转，sh为负数表示Y方向翻转
        matrix.postScale(-1, 1);
        return createBitmapByMatrix(bitmap, matrix, true, isRecycle);
    }


    public static Bitmap getFilpBitmap(Bitmap bitmap, float angle, boolean isRecycle) {
        Matrix matrix = new Matrix();
        // sw sh的绝对值为绽放宽高的比例，sw为负数表示X方向翻转，sh为负数表示Y方向翻转
        matrix.postScale(-1, 1);
        matrix.postRotate(angle);
        return createBitmapByMatrix(bitmap, matrix, true, isRecycle);
    }

    public static Bitmap getTBFilpBitmap(Bitmap bitmap, float mAngle, boolean isRecycle) {
        Matrix matrix = new Matrix();
        // sw sh的绝对值为绽放宽高的比例，sw为负数表示X方向翻转，sh为负数表示Y方向翻转
        matrix.postScale(1, -1);
        matrix.postRotate(mAngle);
        return createBitmapByMatrix(bitmap, matrix, true, isRecycle);
    }

    public static Bitmap roateBitmap(Bitmap currentSelBitmap, float angle, boolean isRecycle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return createBitmapByMatrix(currentSelBitmap, matrix, false, isRecycle);
    }

    public static Bitmap getSmallBitmapByX(Bitmap bitmap, int mWidth, boolean isRecycle) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale2 = (float) mWidth / w;
        matrix.postScale(scale2, scale2);
        return createBitmapByMatrix(bitmap, matrix, true, isRecycle);
    }


    /**
     * 根据高度压缩
     *
     * @param bitmap
     * @param mHight
     * @param isRecycle
     * @return
     */
    public static Bitmap getSmallBitmapByY(Bitmap bitmap, int mHight, boolean isRecycle) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale2 = (float) mHight / h;
        matrix.postScale(scale2, scale2);
        return createBitmapByMatrix(bitmap, matrix, true, isRecycle);
    }

    public static Bitmap getSmallBitmapByXY(Bitmap bitmap, int mWidth, int mHight, boolean isRecycle) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float scaleH = (float) mHight / h;
        float scaleW = (float) mWidth / w;
        Matrix matrix = new Matrix();
        float min = Math.min(scaleH, scaleW);
        matrix.postScale(min, min);
        return createBitmapByMatrix(bitmap, matrix, true, isRecycle);
    }

    public static Bitmap adjustViewAndBitmap(Bitmap bmp, View multipleLayersView, boolean isRecycle) {
        Bitmap result;
        ViewGroup.LayoutParams layoutParams = multipleLayersView.getLayoutParams();
        int width = multipleLayersView.getMeasuredWidth();
        int height = multipleLayersView.getMeasuredHeight();

        int bitmapW = bmp.getWidth();
        int bitmapH = bmp.getHeight();
        float f1 = (float) width / bitmapW;
        float f2 = (float) height / bitmapH;

        if (f2 > f1) {
            result = getSmallBitmapByX(bmp, width, isRecycle);
            layoutParams.height = result.getHeight();
            layoutParams.width = result.getWidth();

        } else {
            result = getSmallBitmapByY(bmp, height, isRecycle);
            layoutParams.height = result.getHeight();
            layoutParams.width = result.getWidth();
        }
        multipleLayersView.setLayoutParams(layoutParams);
        return result;
    }

    public static Bitmap createTxtBitmap(String str, Typeface font, int color) {
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(color);
        textPaint.setTypeface(font);
        textPaint.setTextSize(300);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFakeBoldText(true);
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();

        String[] split = str.split("\n");

        int maxIndex = 0;
        for (int i = 0; i < split.length; ++i) {
            if (split[maxIndex].length() < split[i].length()) {
                maxIndex = i;
            }
        }
        //measureText = bound.right - bound.left + 字符两边的留白宽度
//        宽度获取方法：Paint.measureText(text)
//        高度获取方法：descent+Math.abs(ascent)
        int imageWidth = (int) textPaint.measureText(split[maxIndex]);
        int imageHeight = fontMetrics.descent + Math.abs(fontMetrics.ascent);
        int baseLine = 0 - fontMetrics.ascent;


        Bitmap bmp = Bitmap.createBitmap(imageWidth, imageHeight * split.length, Bitmap.Config.ARGB_8888); //图象大小要根据文字大小算下,以和文本长度对应
        Canvas canvasTemp = new Canvas(bmp);
        canvasTemp.drawColor(Color.TRANSPARENT);
        if (str.equals("")){
            return bmp;
        }
        for (int i=0;i<split.length;++i){

            canvasTemp.drawText(split[i], 0, imageHeight * i + baseLine, textPaint);
        }
        return bmp;
    }

    public static Bitmap getWaterMask(Bitmap bitmap,Bitmap maskBitmap,boolean recycle) {
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888); //图象大小要根据文字大小算下,以和文本长度对应
        Canvas canvasTemp = new Canvas(bmp);
        canvasTemp.drawColor(Color.TRANSPARENT);
        int pading = (int) (bitmap.getWidth() * 0.025);

        int newWidth = bitmap.getWidth() / 8;
        float rate = newWidth/(float)maskBitmap.getWidth();
        int left = bitmap.getWidth() -newWidth- pading;
        int right = bitmap.getWidth() - pading;
        int top = bitmap.getHeight() -(int)(maskBitmap.getHeight()*rate)-pading;
        int bottom = bitmap.getHeight() -pading;

        canvasTemp.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        canvasTemp.drawBitmap(bitmap,0,0,null);
        canvasTemp.drawBitmap(maskBitmap,new Rect(0,0,maskBitmap.getWidth(),maskBitmap.getHeight()),new Rect(left,top,right,bottom),null);
        if (recycle){
            recycleBitmap(bitmap);
        }
        return bmp;
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);//NORMAL 为0，即不旋转
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90://右旋90度
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 获取Bitmap在ImageView中的偏移量数组,其中第0个值表示在水平方向上的偏移值,第1个值表示在垂直方向上的偏移值
     *
     * @param imageView
     * @param includeLayout 在计算偏移的时候是否要考虑到布局的因素,如果要考虑该因素则为true,否则为false
     * @return the offsets of the bitmap inside the imageview, offset[0] means horizontal offset, offset[1] means vertical offset
     */
    public static int[] getBitmapOffset(ImageView imageView, boolean includeLayout) {
        int[] offset = new int[2];
        float[] values = new float[9];

        Matrix matrix = imageView.getImageMatrix();
        matrix.getValues(values);

        // x方向上的偏移量(单位px)
        offset[0] = (int) values[2];
        // y方向上的偏移量(单位px)
        offset[1] = (int) values[5];

        if (includeLayout) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            int paddingTop = imageView.getPaddingTop();
            int paddingLeft = imageView.getPaddingLeft();

            offset[0] += paddingLeft + params.leftMargin;
            offset[1] += paddingTop + params.topMargin;
        }
        return offset;
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private static boolean colorEquals(int color1, int color2) {
        return color1 == color2;
    }

    private static int mix(int crBack, int crForeground) {
        int rBack = Color.red(crBack);
        int gBack = Color.green(crBack);
        int bBack = Color.blue(crBack);
        int aForeground = Color.alpha(crForeground);
        int rForeground = Color.red(crForeground);
        int gForeground = Color.green(crForeground);
        int bForeground = Color.blue(crForeground);

        return Color.rgb((int)(rForeground * (aForeground / 255f) + rBack * (1 - aForeground / 255f)),
                (int)(gForeground * (aForeground / 255f) + gBack * (1 - aForeground / 255f)),
                (int)(bForeground * (aForeground / 255f) + bBack * (1 - aForeground / 255f)));
    }

    public static Point getPathBitmapSize(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        return new Point(options.outWidth, options.outHeight);
    }

    public static void compare2bitmap() {
        Bitmap bmp1 = BitmapFactory.decodeFile("/sdcard/compare1.jpg");
        Bitmap bmp2 = BitmapFactory.decodeFile("/sdcard/compare2.jpg");

        if(bmp1.getWidth() == bmp2.getWidth() && bmp1.getHeight() == bmp2.getHeight()) {
            Bitmap result1 = createBitmap(bmp1.getWidth(), bmp1.getHeight());
            for(int i = 0; i < bmp1.getWidth(); i++) {
                for(int j = 0; j < bmp1.getHeight(); j++) {
                    int color1 = bmp1.getPixel(i, j);
                    int color2 = bmp2.getPixel(i, j);
                    if(colorEquals(color1, color2)) {
                        result1.setPixel(i, j, color1);
                    } else {
                        result1.setPixel(i, j, mix(color1, Color.argb(128, 255, 0, 0)));
                    }
                }
            }

            savePhotoToSDCard(bmp1, "/sdcard", "test0.jpg");
            savePhotoToSDCard(bmp2, "/sdcard", "test1.jpg");
            savePhotoToSDCard(result1, "/sdcard", "test2.jpg");
        }
    }


    public static Bitmap loadBitmapFromView(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bm = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bm;
    }
}