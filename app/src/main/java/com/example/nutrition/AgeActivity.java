package com.example.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AgeActivity extends AppCompatActivity {
    private ImageView ivBackAge;
    private TextView tvWhyAge;
    private EditText editTextAgeOnly;
    private Button btnNextAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age);

        ivBackAge       = findViewById(R.id.ivBackAge);
        tvWhyAge        = findViewById(R.id.tvWhyAge);
        editTextAgeOnly = findViewById(R.id.editTextAgeOnly);
        btnNextAge      = findViewById(R.id.btnNextAge);

        // Back → WeightActivity (if that’s your actual back-flow)
        ivBackAge.setOnClickListener(v -> {
            Intent back = new Intent(this, WeightActivity.class);
            back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            back.putExtras(getIntent());  // carry forward any
            startActivity(back);
            finish();
        });

        // Why we ask
        tvWhyAge.setOnClickListener(v ->
                Toast.makeText(this,
                        "Knowing your age helps tailor your plan.",
                        Toast.LENGTH_LONG).show()
        );

        // Next → TallActivity
        btnNextAge.setOnClickListener(v -> {
            String s = editTextAgeOnly.getText().toString().trim();
            if (s.isEmpty()) {
                Toast.makeText(this, "Please enter your age", Toast.LENGTH_SHORT).show();
                return;
            }
            int age = Integer.parseInt(s);

            Intent intent = new Intent(this, TallActivity.class);
            intent.putExtras(getIntent());       // carry nothing yet, but consistent
            intent.putExtra("AGE", age);         // uppercase key

            startActivity(intent);
            finish();
        });
    }
}
