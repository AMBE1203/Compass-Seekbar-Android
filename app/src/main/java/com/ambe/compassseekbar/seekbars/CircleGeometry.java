package com.ambe.compassseekbar.seekbars;

/**
 * Created by AMBE on 10/29/2018 at 10:56 AM.
 */
public class CircleGeometry {
    public static double getRadianAngle(int angle){
        return angle * (Math.PI / 180);
    }

    public static int calculateX(int coodX, int radius, double rotationRadian){
        return (int) (coodX + ((radius) * Math.cos(rotationRadian)));
    }

    public static int calculateY(int coodY, int radius, double rotationRadian){
        return (int) (coodY + ((radius) * Math.sin(rotationRadian)));
    }
}
