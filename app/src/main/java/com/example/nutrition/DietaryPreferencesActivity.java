package com.example.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DietaryPreferencesActivity extends AppCompatActivity {
    private ImageView ivBackDiet;
    private Button    btnGetPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietary_preferences);

        ivBackDiet = findViewById(R.id.ivBackDiet);
        btnGetPlan = findViewById(R.id.btnGetPlan);

        // Back arrow → FoodAllergiesActivity
        ivBackDiet.setOnClickListener(v -> {
            Intent back = new Intent(this, FoodAllergiesActivity.class);
            back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(back);
            finish();
        });

        // “Why we ask” Toast
        findViewById(R.id.tvWhyDiet).setOnClickListener(v ->
                Toast.makeText(this,
                        "Your dietary choice helps us tailor your nutrition plan.",
                        Toast.LENGTH_LONG).show()
        );

        // Get your plan → ResultsActivity, passing three boolean flags
        btnGetPlan.setOnClickListener(v -> {
            RadioButton rbVeg   = findViewById(R.id.radioVegetarian);
            RadioButton rbOmni  = findViewById(R.id.radioOmnivore);
            RadioButton rbPesca = findViewById(R.id.radioPescatarian);

            // ensure one is checked
            if (!rbVeg.isChecked() && !rbOmni.isChecked() && !rbPesca.isChecked()) {
                Toast.makeText(this, "Please select a preference", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, ResultsActivity.class);
            // carry forward any previous extras
            intent.putExtras(getIntent());

            // now only send the booleans—no "DIETARY_PREFERENCE" string
            intent.putExtra("DIET_VEGETARIAN", rbVeg.isChecked());
            intent.putExtra("DIET_OMNIVORE",   rbOmni.isChecked());
            intent.putExtra("DIET_PESCATARIAN",rbPesca.isChecked());

            startActivity(intent);
            finish();
        });
    }
}
