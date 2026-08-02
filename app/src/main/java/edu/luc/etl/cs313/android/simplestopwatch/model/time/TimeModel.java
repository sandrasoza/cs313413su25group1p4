package edu.luc.etl.cs313.android.simplestopwatch.model.time;

/**
 * The passive data model of the timer.
 * It does not emit any events.
 * Time is bounded between 0 and 99.
 *
 * @author laufer
 */
public interface TimeModel {
    void resetTime();       // Rests time to zero
    void incTime();         // Increments time by 1, Max of 99
    void decTime();         // Decrements time by 1, Min of 0
    int getTime();          // Returns current time
    void setTime(int time); // Sets specific time
    boolean isZero();       // Returns true if timer == 0
    boolean isMax();        // Returns true if timer == 99
}
