package edu.luc.etl.cs313.android.simplestopwatch.model.time;

import static edu.luc.etl.cs313.android.simplestopwatch.common.Constants.*;

/**
 * An implementation of the timer data model.
 * Time is bounded between 0 and 99.
 */
public class DefaultTimeModel implements TimeModel {

    private int time = 0;

    private static final int MIN_TIME = 0;
    private static final int MAX_TIME = 99;

    @Override
    public void resetTime() {
        time = MIN_TIME;
    }

    @Override
    public void incTime() {
        if (time < MAX_TIME) {
            time++;
        }
    }

    @Override
    public void decTime() {
        if (time > MIN_TIME) {
            time--;
        }
    }

    @Override
    public int getTime() {
        return time;
    }

    @Override
    public void setTime(int newTime) {
        if (newTime >= MIN_TIME && newTime <= MAX_TIME) {
            time = newTime;
        }
    }

    @Override
    public boolean isZero() {
        return time == MIN_TIME;
    }

    @Override
    public boolean isMax() {
        return time == MAX_TIME;
    }
}