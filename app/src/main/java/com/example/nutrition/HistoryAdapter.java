package com.example.nutrition;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {
    private final List<Plan> items;

    public HistoryAdapter(List<Plan> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        Plan p = items.get(i);

        // Bind date & macros
        h.tvDay      .setText(p.date);
        h.tvCalories.setText(p.dailyCal + " kcal");
        h.tvProtein .setText(String.format(Locale.getDefault(), "%.0f g", p.proteinG));
        h.tvCarbs   .setText(String.format(Locale.getDefault(), "%.0f g", p.carbsG));


        // Clear out any previous day views
        h.dayContainer.removeAllViews();

        // Inflate each of the 7 DayPlan entries
        for (DayPlan dp : p.days) {
            View dv = LayoutInflater.from(h.itemView.getContext())
                    .inflate(R.layout.item_day, h.dayContainer, false);

            TextView tvNum  = dv.findViewById(R.id.tvDayNumber);
            TextView tvB    = dv.findViewById(R.id.tvDayBreakfast);
            TextView tvL    = dv.findViewById(R.id.tvDayLunch);
            TextView tvD    = dv.findViewById(R.id.tvDayDinner);
            TextView tvS    = dv.findViewById(R.id.tvDaySnack);

            tvNum.setText("Day " + dp.day);
            tvB  .setText("Breakfast: " + dp.breakfast);
            tvL  .setText("Lunch:     " + dp.lunch);
            tvD  .setText("Dinner:    " + dp.dinner);
            tvS  .setText("Snack:     " + dp.snack);

            h.dayContainer.addView(dv);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView    tvDay, tvCalories, tvProtein, tvCarbs;
        LinearLayout dayContainer;

        VH(View v) {
            super(v);
            tvDay        = v.findViewById(R.id.tvDay);
            tvCalories   = v.findViewById(R.id.tvCalories);
            tvProtein    = v.findViewById(R.id.tvProtein);
            tvCarbs      = v.findViewById(R.id.tvCarbs);
            dayContainer = v.findViewById(R.id.dayContainer);
        }
    }
}
