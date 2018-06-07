package com.aguai.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aguai.demo.canvas.CanvasActivity;
import com.aguai.demo.guide.GuideActivity;
import com.aguai.guide.R;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by whb on 17-8-30.
 */

public class HelloActivity extends Activity {
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    List<String> titles = new ArrayList<>();
    List<Class> activities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        ButterKnife.bind(this);
        activities.add(CanvasActivity.class);
        activities.add(GuideActivity.class);
        titles.add("canvasWrapper绘制");
        titles.add("基于canvasWrapper实现向导");
        Adpter adpter = new Adpter(activities, titles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setAdapter(adpter);

    }


    class Adpter extends RecyclerView.Adapter<Adpter.ViewHodler> {

        private final List<Class> activitys;
        private final List<String> titles;

        public Adpter(List<Class> activities, List<String> titles) {
            this.activitys = activities;
            this.titles = titles;
        }

        @Override
        public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_function,
                    parent, false);
            return new ViewHodler(view);
        }

        @Override
        public void onBindViewHolder(ViewHodler holder, int position) {

            holder.itemName.setText(titles.get(position));
        }

        @Override
        public int getItemCount() {
            return activitys.size();
        }

        class ViewHodler extends RecyclerView.ViewHolder {
            TextView itemName;

            public ViewHodler(View itemView) {
                super(itemView);
                itemName = itemView.findViewById(R.id.item_name);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(HelloActivity.this, activities.get(getAdapterPosition())));
                    }
                });
            }
        }
    }

}
