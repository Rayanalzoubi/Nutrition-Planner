package com.example.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private Button btnNewPlan, btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Grab the two buttons
        btnNewPlan = findViewById(R.id.btnNewPlan);
        btnHistory = findViewById(R.id.btnHistory);

        // “Create New Plan” → GenderActivity
        btnNewPlan.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, GenderActivity.class))
        );

        // “View History Plans” → HistoryActivity
        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, HistoryActivity.class))
        );
    }
}
