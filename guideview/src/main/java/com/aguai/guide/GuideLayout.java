package com.aguai.guide;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.aguai.canvaswrap.OpMode;
import com.aguai.canvaswrap.SfDisplayInfoView;
import com.aguai.canvaswrap.shape.CircleShape;

/**
 * ================================================
 * 作    者：aguai（吴红斌）Github地址：https://github.com/aguai1
 * 版    本：1.0
 * 创建日期：2018/6/7
 * 描    述：
 * ================================================
 */
public class GuideLayout extends FrameLayout{
    private SfDisplayInfoView sfDisplayInfoView;

    public GuideLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }
    public GuideLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        sfDisplayInfoView =new SfDisplayInfoView(context);
        sfDisplayInfoView.setBgColor(Color.parseColor("#33000000"));
        FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        sfDisplayInfoView.setLayoutParams(layoutParams);
        sfDisplayInfoView.setCurrentMode(OpMode.MODE_STATIC);
        addView(sfDisplayInfoView);

        CircleShape circleShape = new CircleShape(false, Color.GREEN, 10,300,300,200);
        sfDisplayInfoView.addShape(circleShape);
    }


}
