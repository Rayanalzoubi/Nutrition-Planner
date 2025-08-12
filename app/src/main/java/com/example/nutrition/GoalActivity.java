package com.example.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GoalActivity extends AppCompatActivity {
    private ImageView ivBackGoal;
    private TextView tvWhyGoal;
    private RadioGroup radioGroupGoal;
    private Button btnNextGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);

        ivBackGoal     = findViewById(R.id.ivBackGoal);
        tvWhyGoal      = findViewById(R.id.tvWhyGoal);
        radioGroupGoal = findViewById(R.id.radioGroupGoal);
        btnNextGoal    = findViewById(R.id.btnNextGoal);

        // Back → TallActivity (carry prior extras)
        ivBackGoal.setOnClickListener(v -> {
            Intent back = new Intent(this, TallActivity.class);
            back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            back.putExtras(getIntent());  // preserve AGE, GENDER, HEIGHT, WEIGHT
            startActivity(back);
            finish();
        });

        // “Why we ask” toast
        tvWhyGoal.setOnClickListener(v ->
                Toast.makeText(this,
                        "Your goal helps us customize your plan.",
                        Toast.LENGTH_LONG).show()
        );

        // Next → ActivityLevelActivity
        btnNextGoal.setOnClickListener(v -> {
            int goalIndex = getSelectedGoal();

            Intent intent = new Intent(this, ActivityLevelActivity.class);
            intent.putExtras(getIntent());    // carry forward all previous inputs
            intent.putExtra("GOAL", goalIndex); // use uppercase key "GOAL"

            startActivity(intent);
            finish();
        });
    }

    /**
     * @return 0 for Lose Weight, 1 for Maintain, 2 for Build Muscle
     */
    private int getSelectedGoal() {
        int selectedId = radioGroupGoal.getCheckedRadioButtonId();
        if (selectedId == R.id.radioLoseWeight)  return 0;
        if (selectedId == R.id.radioMaintain)     return 1;
        if (selectedId == R.id.radioBuildMuscle)  return 2;
        return 1; // default to Maintain
    }
}
