package com.ambe.compassseekbar.seekbars;

/**
 * Created by AMBE on 10/24/2018 at 11:13 AM.
 */
public class Coordinates {
    /* defining the x and y coordinates for drawings */
    private int cood_X, cood_Y;

    public Coordinates(int cood_X, int cood_Y) {
        this.cood_X = cood_X;
        this.cood_Y = cood_Y;
    }

    public Coordinates() {}

    public int getCood_X() {
        return cood_X;
    }

    public void setCood_X(int cood_X) {
        this.cood_X = cood_X;
    }

    public int getCood_Y() {
        return cood_Y;
    }

    public void setCood_Y(int cood_Y) {
        this.cood_Y = cood_Y;
    }
}
