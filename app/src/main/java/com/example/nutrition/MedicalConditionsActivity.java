package com.example.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MedicalConditionsActivity extends AppCompatActivity {
    private ImageView ivBackMedical;
    private CheckBox  checkHeart, checkDiabetes, checkHypertension;
    private Button    btnNextMedical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_conditions);

        ivBackMedical     = findViewById(R.id.ivBackMedical);
        checkHeart        = findViewById(R.id.checkHeart);
        checkDiabetes     = findViewById(R.id.checkDiabetes);
        checkHypertension = findViewById(R.id.checkHypertension);
        btnNextMedical    = findViewById(R.id.btnNextMedical);

        // Back arrow → ActivityLevelActivity (carry forward all previous inputs)
        ivBackMedical.setOnClickListener(v -> {
            Intent back = new Intent(MedicalConditionsActivity.this, ActivityLevelActivity.class);
            back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            back.putExtras(getIntent());   // preserve all extras so far
            startActivity(back);
            finish();
        });

        // Next (with 1s delay) → FoodAllergiesActivity
        btnNextMedical.setOnClickListener(v -> {
            btnNextMedical.setEnabled(false);

            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MedicalConditionsActivity.this, FoodAllergiesActivity.class);
                intent.putExtras(getIntent());  // carry forward all inputs
                intent.putExtra("HEART_DISEASE",   checkHeart.isChecked());
                intent.putExtra("DIABETES",        checkDiabetes.isChecked());
                intent.putExtra("HYPERTENSION",    checkHypertension.isChecked());
                startActivity(intent);
                finish();
            }, 1000);
        });
    }
}
