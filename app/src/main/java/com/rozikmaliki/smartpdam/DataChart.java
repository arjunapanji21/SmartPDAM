package com.rozikmaliki.smartpdam;

import java.time.DayOfWeek;

public class DataChart {
    String x, y;

    public DataChart(String x, String  y) {
        this.x = x;
        this.y = y;
    }

    public DataChart(){}

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }
}
