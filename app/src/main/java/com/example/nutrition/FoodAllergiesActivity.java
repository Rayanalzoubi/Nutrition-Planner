package com.example.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FoodAllergiesActivity extends AppCompatActivity {
    private ImageView ivBackAllergies;
    private CheckBox  checkNuts, checkDairy, checkGluten;
    private Button    btnNextAllergies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_allergies);

        ivBackAllergies  = findViewById(R.id.ivBackAllergies);
        checkNuts        = findViewById(R.id.checkNuts);
        checkDairy       = findViewById(R.id.checkDairy);
        checkGluten      = findViewById(R.id.checkGluten);
        btnNextAllergies = findViewById(R.id.btnNextAllergies);

        // Back → MedicalConditionsActivity (carry prior extras)
        ivBackAllergies.setOnClickListener(v -> {
            Intent back = new Intent(this, MedicalConditionsActivity.class);
            back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            back.putExtras(getIntent());  // carry everything so far
            startActivity(back);
            finish();
        });

        // “Why we ask” toast
        findViewById(R.id.tvWhyAllergies).setOnClickListener(v ->
                Toast.makeText(this,
                        "Identifying allergies keeps your plan safe.",
                        Toast.LENGTH_LONG).show()
        );

        // Next → DietaryPreferencesActivity
        btnNextAllergies.setOnClickListener(v -> {
            Intent intent = new Intent(this, DietaryPreferencesActivity.class);

            // carry forward all previous inputs
            intent.putExtras(getIntent());

            // add allergy flags with uppercase keys
            intent.putExtra("ALLERGY_NUTS",   checkNuts.isChecked());
            intent.putExtra("ALLERGY_DAIRY",  checkDairy.isChecked());
            intent.putExtra("ALLERGY_GLUTEN", checkGluten.isChecked());

            startActivity(intent);
            finish();
        });
    }
}
