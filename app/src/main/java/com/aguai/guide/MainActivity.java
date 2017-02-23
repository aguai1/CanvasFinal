package com.aguai.guide;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    GuideView guideView;
    GuideContainer guideContainer;
    private Button button;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        guideContainer = (GuideContainer) findViewById(R.id.guideContainer);

        guideView = (GuideView) findViewById(R.id.guideView);

        button = (Button) findViewById(R.id.btn_exit);
        button2 = (Button) findViewById(R.id.btn_exit2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guideContainer.clearAllView();
                showGuideCheckIn();
                guideContainer.animIn(200);
                button.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        guideContainer.clearAllView();
                        guideView.clearRect();
                        showGuideCheckIn2();
                        guideContainer.noAnimRefresh();
                    }
                }, 500);
            }
        });
        ViewTreeObserver viewTreeObserver = findViewById(R.id.laymain).getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                showGuideVideoLowValueCountry();
                guideContainer.animIn(200);
                findViewById(R.id.laymain).getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void showGuideVideoHighValueCountry() {

        View inflate = getLayoutInflater().inflate(R.layout.guide_item_watch_more_videos, null);
        View inflate1 = getLayoutInflater().inflate(R.layout.guide_item_complete_an_offer, null);
        guideContainer.addBlackRect(button, inflate, null, 10,null);
        guideContainer.addBlackRect(button2, null, inflate1, 10,null);
    }

    private void showGuideVideoLowValueCountry() {
        View view = getLayoutInflater().inflate(R.layout.guide_item_watch_more_videos, null);
        guideContainer.addBlackRect(button, view, null, 10,null);

        View inflate = getLayoutInflater().inflate(R.layout.guide_item_check_in_for_novideo_highvalue, null);
        View inflate1 = getLayoutInflater().inflate(R.layout.guide_item_test_lucky_1_credits, null);
        guideContainer.addBlackRect(findViewById(R.id.lay1), inflate, null, -10,null);
        guideContainer.addBlackRect(findViewById(R.id.lay2), null, inflate1, -10,null);
    }

    private void showGuideCheckIn() {

        View inflate = getLayoutInflater().inflate(R.layout.guide_item_check_in, null);
        guideContainer.addBlackRect(findViewById(R.id.lay1), inflate, null, 10,null);
    }

    private void showGuideCheckIn2() {
        View inflate = getLayoutInflater().inflate(R.layout.guide_item_check_in, null);
        guideContainer.addBlackRect(findViewById(R.id.lay2), inflate, null, 10,null);
    }

    public void exit(View view) {
        Toast.makeText(getBaseContext(), "", Toast.LENGTH_LONG).show();
    }

}
