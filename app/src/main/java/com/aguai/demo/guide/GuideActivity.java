package com.aguai.demo.guide;

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

public class GuideActivity extends AppCompatActivity {



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
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

    }


}
