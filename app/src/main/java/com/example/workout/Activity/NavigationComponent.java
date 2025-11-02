package com.example.workout.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.workout.R;
import com.example.workout.Activity.MainActivity;

public class NavigationComponent extends LinearLayout {
    public NavigationComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // TODO: Create component_bottom_navigation layout
        //LayoutInflater.from(context).inflate(R.layout.component_bottom_navigation, this, true);

        // TODO: Add these IDs to layout
        //LinearLayout navChart = findViewById(R.id.navChart);
        LinearLayout navHome = findViewById(R.id.navHome);
        //LinearLayout navSchedule = findViewById(R.id.navSchedule);
        //LinearLayout navProfile = findViewById(R.id.navProfile);

        // Keep only home navigation active for now
        /*// TODO: Implement Chart feature
        if (navChart != null) {
            navChart.setOnClickListener(v -> {
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();
            });
        }*/

        // Keep home navigation active
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                context.startActivity(new Intent(context, MainActivity.class));
            });
        }

        /*// TODO: Implement Schedule feature
        if (navSchedule != null) {
            navSchedule.setOnClickListener(v -> {
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();
            });
        }

        // TODO: Implement Profile feature
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show();
            });
        }*/


    }
}
