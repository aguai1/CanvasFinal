package com.aguai.guide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    GuideView guideView;
    GuideContainer guideContainer;
    private Button button;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        guideContainer= (GuideContainer) findViewById(R.id.guideContainer);

        guideView= (GuideView) findViewById(R.id.guideView);

        button = (Button) findViewById(R.id.btn_exit);
        button2 = (Button) findViewById(R.id.btn_exit2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGuideVideoLowValueCountry();
                guideContainer.animIn();
            }
        });
        ViewTreeObserver viewTreeObserver = findViewById(R.id.laymain).getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                showGuideVideoHighValueCountry();
                guideContainer.animIn();
                findViewById(R.id.laymain).getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void showGuideVideoHighValueCountry() {

        View inflate = getLayoutInflater().inflate(R.layout.guide_item_watch_more_videos, null);
        View inflate1 = getLayoutInflater().inflate(R.layout.guide_item_complete_an_offer, null);
        guideContainer.addBlackRect(button,inflate,null,10);
        guideContainer.addBlackRect(button2,null,inflate1,10);
    }
    private void showGuideVideoLowValueCountry() {
        View view = getLayoutInflater().inflate(R.layout.guide_item_watch_more_videos, null);
        guideContainer.addBlackRect(button,view,null,10);

        View inflate = getLayoutInflater().inflate(R.layout.guide_item_check_in_for_novideo_highvalue, null);
        View inflate1 = getLayoutInflater().inflate(R.layout.guide_item_test_lucky_1_credits, null);
        guideContainer.addBlackRect(findViewById(R.id.lay1),inflate,null,-10);
        guideContainer.addBlackRect(findViewById(R.id.lay2),null,inflate1,-10);
    }
//    private void showGuideNoVideoLowValueCountry() {
//        List<View> viewList=new ArrayList<>();
//        viewList.add(findViewById(R.id.lay1));
//        viewList.add(findViewById(R.id.lay2));
//        View inflate = getLayoutInflater().inflate(R.layout.guide_item_check_in_for_free_credits, null);
//        View inflate1 = getLayoutInflater().inflate(R.layout.guide_item_test_lucky_1_credits, null);
//        guideContainer.addBlackRect(viewList,inflate,inflate1,10);
//    }
//    private void showGuideNoVideoHighValueCountry() {
//        View view = getLayoutInflater().inflate(R.layout.guide_item_fast_get_free_credits, null);
//        List<View> viewList1=new ArrayList<>();
//        viewList1.add(button);
//        guideContainer.addBlackRect(viewList1,view,null,10);
//
//        List<View> viewList=new ArrayList<>();
//        viewList.add(findViewById(R.id.lay1));
//        viewList.add(findViewById(R.id.lay2));
//        View inflate = getLayoutInflater().inflate(R.layout.guide_item_check_in_for_novideo_highvalue, null);
//        View inflate1 = getLayoutInflater().inflate(R.layout.guide_item_test_lucky_1_credits, null);
//        guideContainer.addBlackRect(viewList,inflate,inflate1,-10);
//    }
//
//    private void showGuideCheckIn() {
//        List<View> viewList=new ArrayList<>();
//        viewList.add(findViewById(R.id.lay1));
//        View inflate = getLayoutInflater().inflate(R.layout.guide_item_check_in, null);
//        guideContainer.addBlackRect(viewList,inflate,null,10);
//    }
//
//    private void showFeelingLucky() {
//        List<View> viewList=new ArrayList<>();
//        viewList.add(findViewById(R.id.lay1));
//        View inflate = getLayoutInflater().inflate(R.layout.guide_item_feeling_lucky, null);
//        guideContainer.addBlackRect(viewList,inflate,null,10);
//    }
    public void exit(View view){
        Toast.makeText(getBaseContext(),"",Toast.LENGTH_LONG).show();
    }

}
