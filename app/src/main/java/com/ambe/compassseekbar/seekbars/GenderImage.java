package com.ambe.compassseekbar.seekbars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

/**
 * Created by AMBE on 10/25/2018 at 5:25 PM.
 */
public class GenderImage {
    private Context context;


    public static final int NORMAL_SIZE = Util.dpToPx(170);
    public static final int NORMAL_SIZE_X = Util.dpToPx(80);

    public static final int CHOOSE_SIZE = Util.dpToPx(100);

    public static final int DISTANCE = Util.dpToPx(15);

    public static final int MAX_WIDTH_TITLE = Util.dpToPx(70);

    public int currentSize = NORMAL_SIZE;
    public int sizeX = NORMAL_SIZE_X;

    public int beginSize;

    public int endSize;

    public float currentX;

    public float currentY;

    public float beginY;

    public float endY;

    public Bitmap imageOrigin;


    public Paint genderPaint;
    private int imageResource;

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
        Drawable drawable = ContextCompat.getDrawable(context, imageResource);
        imageOrigin = drawableToBitmap(drawable);
        //  imageOrigin = BitmapFactory.decodeResource(context.getResources(), imageResource);
        genderPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        genderPaint.setAntiAlias(true);
    }

    public GenderImage(Context context) {
        this.context = context;

    }


    public void drawGenderImage(Canvas canvas) {

        canvas.drawBitmap(imageOrigin, null, new RectF(currentX, currentY, currentX + sizeX, currentY + currentSize), genderPaint);

    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
