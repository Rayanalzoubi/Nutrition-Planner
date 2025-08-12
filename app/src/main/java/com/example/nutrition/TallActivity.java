package com.example.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TallActivity extends AppCompatActivity {
    private ImageView ivBackTall;
    private TextView tvWhyTall;
    private EditText editTextTallOnly;
    private Button btnNextTall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tall);

        ivBackTall       = findViewById(R.id.ivBackTall);
        tvWhyTall        = findViewById(R.id.tvWhyTall);
        editTextTallOnly = findViewById(R.id.editTextTallOnly);
        btnNextTall      = findViewById(R.id.btnNextTall);

        // Back arrow → AgeActivity (carry forward AGE & GENDER)
        ivBackTall.setOnClickListener(v -> {
            Intent back = new Intent(this, AgeActivity.class);
            back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            back.putExtras(getIntent());  // preserve AGE, GENDER
            startActivity(back);
            finish();
        });

        // “why we ask”
        tvWhyTall.setOnClickListener(v ->
                Toast.makeText(this,
                        "Your height helps personalize your plan.",
                        Toast.LENGTH_LONG).show()
        );

        // Next → GoalActivity
        btnNextTall.setOnClickListener(v -> {
            String s = editTextTallOnly.getText().toString().trim();
            if (s.isEmpty()) {
                Toast.makeText(this, "Please enter your height", Toast.LENGTH_SHORT).show();
                return;
            }
            float height = Float.parseFloat(s);

            Intent intent = new Intent(this, GoalActivity.class);
            intent.putExtras(getIntent());     // carry AGE, GENDER
            intent.putExtra("HEIGHT", height);  // uppercase key

            startActivity(intent);
            finish();
        });
    }
}
