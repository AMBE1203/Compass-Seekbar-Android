package com.ambe.compassseekbar.seekbars;

/**
 * Created by AMBE on 10/24/2018 at 11:17 AM.
 */
public class Interval {
    IntervalLine intervalLine;
    IntervalHeading intervalHeading;
    int size;

    public Interval(IntervalLine intervalLine, IntervalHeading intervalHeading, int size) {
        this.intervalLine = intervalLine;
        this.intervalHeading = intervalHeading;
        this.size = size;
    }

    public Interval(IntervalLine intervalLine, IntervalHeading intervalHeading) {
        this.intervalLine = intervalLine;
        this.intervalHeading = intervalHeading;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public IntervalLine getIntervalLine() {
        return intervalLine;
    }

    public IntervalHeading getIntervalHeading() {
        return intervalHeading;
    }
}
