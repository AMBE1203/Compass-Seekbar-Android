package com.ambe.compassseekbar.seekbars;

/**
 * Created by AMBE on 10/29/2018 at 10:57 AM.
 */
public class CircleCenter {
    /* xy coordinates for center of the clock */
    private Coordinates coordinates;
    /* default radius of center circle */
    private int mRadius;

    public CircleCenter(int mRadius) {
        this.mRadius = mRadius;
    }

    public CircleCenter() {}

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int mRadius) {
        this.mRadius = mRadius;
    }
}
