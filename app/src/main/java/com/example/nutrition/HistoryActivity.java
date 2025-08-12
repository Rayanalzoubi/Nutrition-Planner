package com.example.nutrition;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView historyRecycler;
    private HistoryAdapter adapter;
    private List<Plan> plans = new ArrayList<>();
    private FirebaseFirestore db;
    private String uid;
    private ImageView ivBackhistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // 1) Set up RecyclerView + Adapter
        historyRecycler = findViewById(R.id.history_recycler);
        historyRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(plans);
        historyRecycler.setAdapter(adapter);



        // 2) Initialize Firestore + get current user ID
        db  = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 3) Fetch saved plans from Firestore
        db.collection("users")
                .document(uid)
                .collection("plans")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(qs -> {
                    plans.clear();
                    for (var doc : qs.getDocuments()) {
                        Plan p = new Plan();

                        // a) Format the timestamp into a readable date
                        long ts = doc.getLong("timestamp");
                        p.date = DateFormat.getDateFormat(this)
                                .format(new Date(ts));

                        // b) Read overall macros
                        p.dailyCal = doc.getLong("daily_cal").intValue();
                        p.proteinG = doc.getDouble("protein_g");
                        p.carbsG   = doc.getDouble("carbs_g");

                        // c) Parse the stored JSON array of daily plans
                        List<DayPlan> dayPlans = new ArrayList<>();
                        String daysJson = doc.getString("meals_json");
                        if (daysJson != null) {
                            try {
                                JSONArray arr = new JSONArray(daysJson);
                                for (int j = 0; j < arr.length(); j++) {
                                    JSONObject d = arr.getJSONObject(j);
                                    DayPlan dp = new DayPlan();
                                    dp.day       = d.optInt("day", j + 1);
                                    dp.breakfast = d.optString("breakfast", "—");
                                    dp.lunch     = d.optString("lunch", "—");
                                    dp.dinner    = d.optString("dinner", "—");
                                    dp.snack     = d.optString("snack", "—");
                                    dayPlans.add(dp);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        p.days = dayPlans;

                        plans.add(p);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            this,
                            "Error loading history: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }
}
