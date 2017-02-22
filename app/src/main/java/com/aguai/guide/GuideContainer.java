package com.aguai.guide;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsoluteLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 阿怪 on 2017/2/19.
 */

public class GuideContainer extends AbsoluteLayout {


    private GuideView guideView;
    private List<View> viewsList;
    private AlphaAnimation animation;

    private boolean isShow = false;

    public GuideContainer(Context context) {
        super(context);
        init();
    }

    public GuideContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GuideContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setVisibility(INVISIBLE);
        viewsList = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        guideView = (GuideView) getChildAt(0);
    }

    /**
     * 增加在指定id前面
     *
     * @param view
     * @param id
     */
    public void addAboveToId(View view, int id) {
        RectF rectById = guideView.getRectById(id);
        AbsoluteLayout.LayoutParams lap = new AbsoluteLayout.LayoutParams(getLayoutParams());
        lap.width = getWidth();
        lap.height = (int) getContext().getResources().getDimension(R.dimen.item_height);
        lap.x = 0;
        lap.y = (int) rectById.top - lap.height;
        addView(view, lap);
    }

    /**
     * 增加在指定控件后面
     *
     * @param view
     * @param id
     */
    public void addBelowToId(View view, int id) {
        RectF rectById = guideView.getRectById(id);
        AbsoluteLayout.LayoutParams lap = new AbsoluteLayout.LayoutParams(getLayoutParams());
        lap.x = 0;
        lap.y = (int) rectById.bottom;
        lap.width = getWidth();
        lap.height = (int) getContext().getResources().getDimension(R.dimen.item_height);
        addView(view, lap);
    }

    /**
     * 增加水平方向控件
     *
     * @param view  控件
     * @param above 控件上面的试图
     * @param below 控件下面的试图
     */
    public void addBlackRect(View view, View above, View below, int border) {
        int[] loc = new int[2];
        viewsList.add(null);
        view.getLocationOnScreen(loc);
        RectF rectF = new RectF(loc[0], loc[1], loc[0] + view.getWidth(), loc[1] + view.getHeight());
        guideView.addRect(rectF, view.getId(), border);
        if (above != null) {
            addAboveToId(above, view.getId());
            viewsList.add(above);
        }
        if (below != null) {
            addBelowToId(below, view.getId());
            viewsList.add(below);
        }

    }

    /**
     * 获取viewGroup内的普通控件列表
     *
     * @param viewGroup
     * @return
     */
    private List<View> getViewList(ViewGroup viewGroup) {
        List<View> views = new ArrayList<>();
        for (int i = 0; i < viewGroup.getChildCount(); ++i) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                ViewGroup viewGroup1 = (ViewGroup) childAt;
                views.addAll(getViewList(viewGroup1));
            } else {
                views.add(childAt);
            }
        }
        return views;
    }

    /**
     * 动态显示
     */
    public void animIn(int time) {
        isShow = true;
        setVisibility(VISIBLE);
        int blackCount = 0;
        int allCount = 0;
        for (int i = 0; i < viewsList.size(); ++i) {
            final View view = viewsList.get(i);
            //为空则显示 black区域
            if (view == null) {
                allCount++;
                blackCount++;
                final int finalBlackCount = blackCount - 1;
                guideView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        guideView.showRectByIndex(finalBlackCount);
                    }
                }, allCount * time);
            } else {
                //  依次显示控件
                if(view instanceof ViewGroup) {
                    final List<View> views = getViewList((ViewGroup) view);
                    for (int j = 0; j < views.size(); ++j) {
                        allCount++;
                        final int finalJ = j;
                        views.get(j).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                views.get(finalJ).setVisibility(VISIBLE);
                            }
                        }, allCount * time);
                    }
                }else {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setVisibility(VISIBLE);
                        }
                    }, allCount * time);
                }
            }
        }
        guideView.postDelayed(new Runnable() {
            @Override
            public void run() {
                guideView.setHasShowAll(true);
            }
        }, allCount * time);
    }

    /**
     * 淡出
     */
    public void animOut() {
        viewsList.clear();
        guideView.clearRect();
        guideView.setHasShowAll(false);
        animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        this.startAnimation(animation);
        isShow = false;
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isShow) return;
                clearAllView();
                setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 清空所有控件
     */
    public void clearAllView() {
        viewsList.clear();
        for (int i = 1; i < getChildCount(); ++i) {
            removeViewAt(i);
            i--;
        }
    }

    /**
     * 动态设置GuideView
     *
     * @param guideView
     */
    public void setGuideView(GuideView guideView) {
        this.guideView = guideView;
    }

    public void noAnimRefresh() {
        isShow=true;
        setVisibility(VISIBLE);
        guideView.setHasShowAll(true);
        guideView.showAllRect();
        List<View> viewList = getViewList(this);
        for (View view:viewList){
            view.setVisibility(VISIBLE);
        }
        requestLayout();
    }

    public boolean isShow() {
        return isShow;
    }
}