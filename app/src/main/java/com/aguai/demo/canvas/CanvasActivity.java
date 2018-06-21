package com.aguai.demo.canvas;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.aguai.canvaswrap.OpMode;
import com.aguai.canvaswrap.ShapeCanvasView;
import com.aguai.canvaswrap.shape.CircleShape;
import com.aguai.canvaswrap.shape.OvalShape;
import com.aguai.canvaswrap.shape.RectangleShape;
import com.aguai.canvaswrap.shape.TextShape;
import com.aguai.demo.widget.arcmenu.ArcMenu;
import com.aguai.guide.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CanvasActivity extends AppCompatActivity {

    @Bind(R.id.displayview)
    ShapeCanvasView shapeCanvasView;
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
        shapeCanvasView.setBgColor(Color.WHITE);
        CircleShape circleShape = new CircleShape(false, Color.GREEN, 10,300,300,200);
        shapeCanvasView.addShape(circleShape);
        arcMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                switch (view.getId()) {
                    case R.id.iv_pen:
                        shapeCanvasView.setCurrentMode(OpMode.MODE_PAINT);
                        break;
                    case R.id.iv_eraser:
                        shapeCanvasView.setCurrentMode(OpMode.MODE_ERASER);
                        break;
                    case R.id.iv_nomal:
                        shapeCanvasView.setCurrentMode(OpMode.MODE_SHOW);
                        break;
                    case R.id.iv_static:
                        shapeCanvasView.setCurrentMode(OpMode.MODE_STATIC);
                        break;
                }
            }
        });
    }

    @OnClick({R.id.oval, R.id.txt, R.id.rect, R.id.circle,R.id.moveCenter,R.id.clear})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.rect:
                RectangleShape rectangleShape = new RectangleShape(true, Color.BLUE, 10,100, 1000, 500, 800);
                shapeCanvasView.addShape(rectangleShape);
                break;
            case R.id.circle:
                CircleShape circleShape = new CircleShape(true, Color.YELLOW, 10,500,600,100);
                shapeCanvasView.addShape(circleShape);
                break;
            case R.id.oval:
                OvalShape ovalShape = new OvalShape(false, Color.DKGRAY, 10,500,300,700,1000);
                shapeCanvasView.addShape(ovalShape);
                break;
            case R.id.txt:
                TextShape text = new TextShape("阿怪的点点滴滴\n点点滴滴 ", Color.DKGRAY,100, 600);
                shapeCanvasView.addShape(text);
                break;
            case R.id.moveCenter:
                shapeCanvasView.moveToCenter();
                break;
            case R.id.clear:
                shapeCanvasView.removeAllShape();
                break;
        }
    }

}
