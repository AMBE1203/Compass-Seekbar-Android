package com.ambe.compassseekbar.seekbars;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by AMBE on 10/25/2018 at 5:26 PM.
 */
public class Util {
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static double getRadianAngle(int angle) {
        return angle * (Math.PI / 180);
    }

    public static int calculateX(int coodX, int radius, double rotationRadian) {
        return (int) (coodX + ((radius) * Math.cos(rotationRadian)));
    }

    public static int calculateY(int coodY, int radius, double rotationRadian) {
        return (int) (coodY + ((radius) * Math.sin(rotationRadian)));
    }

    public static double getSin(int angleDegree) {
        double angleRadian = angleDegree * Math.PI / 180;
        return Math.sin(angleRadian);
    }

    public static double getCos(int angleDegree) {
        double angleRadian = angleDegree * Math.PI / 180;
        return Math.abs(Math.cos(angleRadian));
    }

    public static int getTextWidth(Paint paint, String text) {
        Rect bounds = new Rect();   //Temp object just use for get width and height of text
        paint.getTextBounds(text, 0, text.length(), bounds);
//        return (int) (bounds.width() * Math.abs(Math.cos(diffAngle * Math.PI / 180)));
        return bounds.width();
    }

    public static double calculateDistance(Point firstP, Point secondP){
        return Math.sqrt(Math.pow(firstP.x - secondP.x, 2) + Math.pow(firstP.y - secondP.y, 2));
    }

    public static int convertTouchEventPointToAngle(float xPos, float yPos, int mRadius) {
        double tan = (xPos - mRadius) / (mRadius - yPos);
        return (int) Math.toDegrees(Math.atan(tan));
    }

}
