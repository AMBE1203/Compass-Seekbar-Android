package com.ambe.compassseekbar.seekbars;

import android.graphics.Path;
import android.graphics.RectF;

import static com.ambe.compassseekbar.seekbars.SeekbarConstants.maxDegrees;
import static com.ambe.compassseekbar.seekbars.SeekbarConstants.minDegrees;


/**
 * Created by AMBE on 10/26/2018 at 11:13 AM.
 */
public class Circle {
    Path mPath;
    /* rectangle inside which we'll draw this circle */
    RectF rectF;

    public Circle() {
        mPath = new Path();
        rectF = new RectF();
    }

    public Path getPath() {
        return mPath;
    }

    public void setRect(int left, int top, int bottom, int right) {
        rectF.set(left, top, bottom, right);
    }

    /* should be called after setRect() */
    public void setPath() {
        mPath.arcTo(rectF, minDegrees, maxDegrees - 1, true);
    }
}
