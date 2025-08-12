package com.example.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GenderActivity extends AppCompatActivity {
    private ImageView ivBack;
    private TextView tvWhy;
    private RadioGroup radioGroupGender;
    private Button btnNextGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        ivBack           = findViewById(R.id.ivBack);
        tvWhy            = findViewById(R.id.tvWhy);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        btnNextGender    = findViewById(R.id.btnNextGender);

        // Back arrow → Home (carry prior extras if any)
        ivBack.setOnClickListener(v -> {
            Intent home = new Intent(this, HomeActivity.class);
            home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            home.putExtras(getIntent());
            startActivity(home);
            finish();
        });

        // “Why we ask” toast
        tvWhy.setOnClickListener(v ->
                Toast.makeText(this,
                        "We ask to personalize your nutrition plan.",
                        Toast.LENGTH_LONG).show()
        );

        // Next button → WeightActivity
        btnNextGender.setOnClickListener(v -> {
            int selectedId = radioGroupGender.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert to boolean: true = male, false = female
            boolean isMale = (selectedId != R.id.radioFemale);

            Intent intent = new Intent(this, WeightActivity.class);
            intent.putExtras(getIntent());     // carry AGE
            intent.putExtra("GENDER", isMale);  // uppercase key, boolean

            startActivity(intent);
            finish();
        });
    }
}
