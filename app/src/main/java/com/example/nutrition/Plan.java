package com.example.nutrition;

import java.util.List;

/**
 * Represents a saved weekly nutrition plan.
 */
public class Plan {
    /** Date the plan was created (formatted). */
    public String date;

    /** Overall macro totals for the week snapshot. */
    public int dailyCal;
    public double proteinG;
    public double carbsG;

    /** Detailed breakdown for each day (7 entries). */
    public List<DayPlan> days;
}
