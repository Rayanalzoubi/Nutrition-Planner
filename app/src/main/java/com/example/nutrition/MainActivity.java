package com.example.nutrition;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // your welcome layout

        // Find the buttons
        Button btnNew   = findViewById(R.id.btn_i_am_new);
        Button btnSign  = findViewById(R.id.btn_sign_in);

        // “I am new” → SignUpActivity
        btnNew.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SignUpActivity.class))
        );

        // “Sign in” → LoginActivity
        btnSign.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, LoginActivity.class))
        );
    }
}
