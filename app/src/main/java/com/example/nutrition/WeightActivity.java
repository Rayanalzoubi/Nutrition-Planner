package com.example.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class WeightActivity extends AppCompatActivity {
    private ImageView ivBackWeight;
    private TextView tvWhyWeight;
    private EditText editTextWeightOnly;
    private Button btnNextWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        ivBackWeight       = findViewById(R.id.ivBackWeight);
        tvWhyWeight        = findViewById(R.id.tvWhyWeight);
        editTextWeightOnly = findViewById(R.id.editTextWeightOnly);
        btnNextWeight      = findViewById(R.id.btnNextWeight);

        // Back → GenderActivity (carry forward AGE, GENDER, HEIGHT)
        ivBackWeight.setOnClickListener(v -> {
            Intent back = new Intent(this, GenderActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            back.putExtras(getIntent());
            startActivity(back);
            finish();
        });

        // “Why we ask” toast
        tvWhyWeight.setOnClickListener(v ->
                Toast.makeText(this,
                        "Your weight helps tailor your nutrition plan.",
                        Toast.LENGTH_LONG).show()
        );

        // Next → AgeActivity
        btnNextWeight.setOnClickListener(v -> {
            String w = editTextWeightOnly.getText().toString().trim();
            if (w.isEmpty()) {
                Toast.makeText(this, "Please enter your weight", Toast.LENGTH_SHORT).show();
                return;
            }

            float weight = Float.parseFloat(w);

            Intent intent = new Intent(this, AgeActivity.class);
            // carry forward AGE, GENDER, HEIGHT
            intent.putExtras(getIntent());
            // use uppercase key to match ResultsActivity
            intent.putExtra("WEIGHT", weight);

            startActivity(intent);
            finish();
        });
    }
}
