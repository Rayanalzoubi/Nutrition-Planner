package com.example.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ResultsActivity extends AppCompatActivity {
    private TextView textViewCalories,
            textViewProtein,
            textViewCarbs,
            textViewMealPlan,
            textViewHealthConsiderations;
    private Button buttonStartOver;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        db  = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        textViewCalories             = findViewById(R.id.textViewCalories);
        textViewProtein              = findViewById(R.id.textViewProtein);
        textViewCarbs                = findViewById(R.id.textViewCarbs);
        textViewMealPlan             = findViewById(R.id.textViewMealPlan);
        textViewHealthConsiderations = findViewById(R.id.textViewHealthConsiderations);
        buttonStartOver              = findViewById(R.id.buttonStartOver);

        callFlaskApi();

        buttonStartOver.setOnClickListener(v -> {
            Intent i = new Intent(ResultsActivity.this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        });
    }

    private void callFlaskApi() {
        Intent intent = getIntent();

        // 1) Grab all of our inputs — keys must exactly match your putExtra(...) calls
        int   age           = intent.getIntExtra("AGE", 0);

        // Option A: read height/weight as floats
        float htF           = intent.getFloatExtra("HEIGHT", 0f);
        float wtF           = intent.getFloatExtra("WEIGHT", 0f);
        double ht           = htF;
        double wt           = wtF;

        boolean isMale        = intent.getBooleanExtra("GENDER", true);
        int     activityLevel = intent.getIntExtra("ACTIVITY_LEVEL", 0);
        int     goal          = intent.getIntExtra("GOAL", 1);

        boolean heart         = intent.getBooleanExtra("HEART_DISEASE", false);
        boolean diabetes      = intent.getBooleanExtra("DIABETES", false);
        boolean hypertension  = intent.getBooleanExtra("HYPERTENSION", false);
        boolean nutsAllergy   = intent.getBooleanExtra("ALLERGY_NUTS", false);
        boolean dairyAllergy  = intent.getBooleanExtra("ALLERGY_DAIRY", false);
        boolean glutenAllergy = intent.getBooleanExtra("ALLERGY_GLUTEN", false);

        boolean veg           = intent.getBooleanExtra("DIET_VEGETARIAN", false);
        boolean omni          = intent.getBooleanExtra("DIET_OMNIVORE", false);
        boolean pescatarian   = intent.getBooleanExtra("DIET_PESCATARIAN", false);

        // Log to verify we got the real values
        Log.d("ResultsActivity", "Inputs → AGE=" + age
                + " HT=" + ht + " WT=" + wt
                + " GENDER=" + isMale
                + " LEVEL=" + activityLevel + " GOAL=" + goal);

        // 2) Compute BMR & TDEE
        double bmr = isMale
                ? (10 * wt + 6.25 * ht - 5 * age + 5)
                : (10 * wt + 6.25 * ht - 5 * age - 161);

        double factor;
        switch (activityLevel) {
            case 1: factor = 1.375; break;
            case 2: factor = 1.55;  break;
            case 3: factor = 1.725; break;
            case 4: factor = 1.9;   break;
            default:factor = 1.2;   break;
        }

        int tdee = (int) Math.round(bmr * factor);
        int dailyCalTarget;
        switch (goal) {
            case 0:  dailyCalTarget = tdee - 500; break;
            case 2:  dailyCalTarget = tdee + 300; break;
            default: dailyCalTarget = tdee;      break;
        }

        double proteinG = wt * 1.2;
        double carbsG   = (dailyCalTarget * 0.5) / 4.0;

        // 3) Display stats
        textViewCalories.setText(
                String.format(Locale.getDefault(), "%d kcal", dailyCalTarget)
        );
        textViewProtein.setText(
                String.format(Locale.getDefault(), "%.0f g", proteinG)
        );
        textViewCarbs.setText(
                String.format(Locale.getDefault(), "%.0f g", carbsG)
        );

        // 4) Build health advice
        StringBuilder advice = new StringBuilder();
        if (heart)         advice.append("• Limit saturated fat and sodium.\n");
        if (diabetes)      advice.append("• Monitor carbs and sugar intake.\n");
        if (hypertension)  advice.append("• Keep salt low and eat potassium-rich foods.\n");
        if (nutsAllergy)   advice.append("• Avoid nuts and nut oils.\n");
        if (dairyAllergy)  advice.append("• Substitute with non-dairy proteins.\n");
        if (glutenAllergy) advice.append("• Choose gluten-free grains.\n");
        if (advice.length() == 0)
            advice.append("• No special health considerations.");
        textViewHealthConsiderations.setText(advice.toString());

        // 5) Build JSON & fire off the network request
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("age",                   age);
            userJson.put("height",                ht);
            userJson.put("weight",                wt);
            userJson.put("gender",                isMale ? 0 : 1);
            userJson.put("activity_level",        activityLevel);
            userJson.put("daily_calorie_target",  dailyCalTarget);
            userJson.put("nuts",                  nutsAllergy   ? 1 : 0);
            userJson.put("dairy",                 dairyAllergy  ? 1 : 0);
            userJson.put("gluten",                glutenAllergy ? 1 : 0);
            userJson.put("heart",                 heart         ? 1 : 0);
            userJson.put("diabetes",              diabetes      ? 1 : 0);
            userJson.put("hypertension",          hypertension  ? 1 : 0);

            // your three diet flags
            userJson.put("vegetarian",    veg        ? 1 : 0);
            userJson.put("omnivore",      omni       ? 1 : 0);
            userJson.put("pescatarian",   pescatarian? 1 : 0);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(
                com.android.volley.Request.Method.POST,
                "http://10.0.2.2:5000/recommend",
                userJson,
                response -> {
                    try {
                        JSONArray days = response.getJSONArray("days");
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < days.length(); i++) {
                            JSONObject d = days.getJSONObject(i);
                            sb.append("Day ").append(d.getInt("day")).append(":\n")
                                    .append("  Breakfast: ").append(d.getString("breakfast")).append("\n")
                                    .append("  Lunch:     ").append(d.getString("lunch")).append("\n")
                                    .append("  Dinner:    ").append(d.getString("dinner")).append("\n")
                                    .append("  Snack:     ").append(d.getString("snack")).append("\n\n");
                        }
                        textViewMealPlan.setText(sb.toString());

                        Map<String,Object> plan = new HashMap<>();
                        plan.put("timestamp",     System.currentTimeMillis());
                        plan.put("daily_cal",     dailyCalTarget);
                        plan.put("protein_g",     proteinG);
                        plan.put("carbs_g",       carbsG);
                        plan.put("meals_json",    days.toString());
                        plan.put("vegetarian",    veg);
                        plan.put("omnivore",      omni);
                        plan.put("pescatarian",   pescatarian);

                        db.collection("users")
                                .document(uid)
                                .collection("plans")
                                .add(plan)
                                .addOnSuccessListener(docRef ->
                                        Log.d("Firestore","Plan saved: "+docRef.getId()))
                                .addOnFailureListener(e ->
                                        Log.w("Firestore","Save failed", e));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        textViewMealPlan.setText("Parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("FLASK_API","Request failed", error);
                    textViewMealPlan.setText("API error: " + error.getMessage());
                }
        );

        req.setRetryPolicy(new DefaultRetryPolicy(
                30_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        Volley.newRequestQueue(this).add(req);
    }
}
