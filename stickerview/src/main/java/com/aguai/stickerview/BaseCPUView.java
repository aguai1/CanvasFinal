
package com.aguai.stickerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageButton;

/**
 * Created by whb on 17-10-12.
 */

public abstract class BaseCPUView extends FrameLayout {
    protected boolean displayOriginPic = false;

    public BaseCPUView(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public BaseCPUView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public BaseCPUView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setCompareImageButton(ImageButton compare) {
        compare.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    displayOriginPic = true;
                    postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    displayOriginPic = false;
                    postInvalidate();
                    break;
            }
            return false;
        });
    }

}
