package com.aguai.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.aguai.canvaswrap.OpMode;
import com.aguai.canvaswrap.SfDisplayInfoView;
import com.aguai.canvaswrap.shape.CircleShape;
import com.aguai.canvaswrap.shape.OvalShape;
import com.aguai.canvaswrap.shape.RectangleShape;
import com.aguai.canvaswrap.shape.TextShape;
import com.aguai.demo.widget.arcmenu.ArcMenu;
import com.aguai.guide.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.displayview)
    SfDisplayInfoView displayview;
    @Bind(R.id.arc_menu)
    ArcMenu arcMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_canvaswrapper);
        ButterKnife.bind(this);
        displayview.setBackgroundColor(Color.GRAY);
        CircleShape circleShape = new CircleShape(true, Color.GREEN, 10);
        circleShape.onLayout(0, 0, 500, 800);
        displayview.addShape(circleShape);
        arcMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                switch (view.getId()) {
                    case R.id.iv_pen:
                        displayview.setCurrentMode(OpMode.MODE_PAINT);
                        break;
                    case R.id.iv_eraser:
                        displayview.setCurrentMode(OpMode.MODE_ERASER);
                        break;
                    case R.id.iv_nomal:
                        displayview.setCurrentMode(OpMode.MODE_SHOW);
                        break;
                }
            }
        });
    }

    @OnClick({R.id.oval, R.id.txt, R.id.rect, R.id.circle,R.id.moveCenter})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.rect:
                RectangleShape rectangleShape = new RectangleShape(true, Color.RED, 10);
                rectangleShape.onLayout(0, 0, 500, 800);
                displayview.addShape(rectangleShape);
                break;
            case R.id.circle:
                CircleShape circleShape = new CircleShape(true, Color.YELLOW, 10);
                circleShape.onLayout(500, 500, 700, 700);
                displayview.addShape(circleShape);
                break;
            case R.id.oval:
                OvalShape ovalShape = new OvalShape(true, Color.DKGRAY, 10);
                ovalShape.onLayout(500, 600, 900, 1000);
                displayview.addShape(ovalShape);
                break;
            case R.id.txt:
                TextShape text = new TextShape("阿怪的点点滴滴点点滴滴 ", Color.DKGRAY);
                text.onLayout(100, 600, 200, 1000);
                displayview.addShape(text);
                break;
            case R.id.moveCenter:
                displayview.moveToCenter();
                break;
        }
    }

}
