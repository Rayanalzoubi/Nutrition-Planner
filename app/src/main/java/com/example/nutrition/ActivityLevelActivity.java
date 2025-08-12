package com.example.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityLevelActivity extends AppCompatActivity {
    private ImageView ivBackLevel;
    private TextView tvWhyLevel;
    private RadioGroup radioGroupLevel;
    private Button btnNextLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_level);

        ivBackLevel     = findViewById(R.id.ivBackLevel);
        tvWhyLevel      = findViewById(R.id.tvWhyLevel);
        radioGroupLevel = findViewById(R.id.radioGroupLevel);
        btnNextLevel    = findViewById(R.id.btnNextLevel);

        // 1) Back arrow → GoalActivity (carry previous extras back)
        ivBackLevel.setOnClickListener(v -> {
            Intent back = new Intent(this, GoalActivity.class);
            back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            back.putExtras(getIntent());  // keep everything
            startActivity(back);
            finish();
        });

        // 2) “why we ask”
        tvWhyLevel.setOnClickListener(v ->
                Toast.makeText(this,
                        "Knowing your activity level lets us set realistic targets.",
                        Toast.LENGTH_LONG).show()
        );

        // 3) Next → MedicalConditionsActivity, carrying ALL extras so far + ACTIVITY_LEVEL
        btnNextLevel.setOnClickListener(v -> {
            int level = getSelectedLevel();

            Intent intent = new Intent(this, MedicalConditionsActivity.class);
            intent.putExtras(getIntent());               // carry forward all previous values
            intent.putExtra("ACTIVITY_LEVEL", level);     // add this screen’s value

            startActivity(intent);
            finish();
        });
    }

    /**
     * @return 0=Sedentary, 1=Light, 2=Very, 3=Moderate, 4=Extreme
     */
    private int getSelectedLevel() {
        int id = radioGroupLevel.getCheckedRadioButtonId();
        if (id == R.id.radioSedentary)         return 0;
        if (id == R.id.radioLightlyActive)     return 1;
        if (id == R.id.radioVeryActive)        return 2;
        if (id == R.id.radioModeratelyActive)  return 3;
        if (id == R.id.radioExtremelyActive)   return 4;
        return 0; // default to Sedentary
    }
}
