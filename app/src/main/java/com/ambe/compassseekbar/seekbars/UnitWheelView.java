package com.ambe.compassseekbar.seekbars;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by AMBE on 10/25/2018 at 5:26 PM.
 */
public class UnitWheelView extends View {
    private final String TAG = UnitWheelView.class.getSimpleName();

    public static String UNIT_KG_CM = "kg/cm";
    public static String UNIT_LBS_FT = "lbs/ft";

    private ArrayList<String> listItem = new ArrayList<>();
    private ArrayList<UnitPosition> listPosition = new ArrayList<>();

    private Paint paintTextSelected;
    private Paint paintNormalText;

    private OnUnitChangeListener listener;
    private String unit = UNIT_KG_CM;
    private int position = 0;

    private int fullWidth, fullHeight;
    private int outsideRadius;  //Radius to draw background
    private int insideRadius;   //Radius to draw text inside
    private int centerX, centerY;   //Center of circle
    private int eachItemAngle;  //Angle for all item


    private int prevAngle, currentAngle = 0;


    public UnitWheelView(Context context) {
        super(context);
        init(context, null);
    }

    public UnitWheelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //Init list item to draw
        String[] arrUnit = {UNIT_KG_CM, UNIT_LBS_FT,
                UNIT_KG_CM, UNIT_LBS_FT,
                UNIT_KG_CM, UNIT_LBS_FT};
        listItem.addAll(Arrays.asList(arrUnit));
        //Add 8 position for 8 item
        while (listPosition.size() < listItem.size()) listPosition.add(new UnitPosition());

        eachItemAngle = 360 / listItem.size();


        //Init paint
        float density = getResources().getDisplayMetrics().density;
        paintTextSelected = new Paint();
        paintTextSelected.setColor(Color.parseColor("#277DEE"));
        paintTextSelected.setStyle(Paint.Style.STROKE);
        paintTextSelected.setTextSize(25 * density);
        paintTextSelected.setTextAlign(Paint.Align.CENTER);

        paintNormalText = new Paint();
        paintNormalText.setColor(Color.parseColor("#B0B0B0"));
        paintNormalText.setStyle(Paint.Style.STROKE);
        paintNormalText.setTextSize(18 * density);
        paintNormalText.setTextAlign(Paint.Align.CENTER);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        fullWidth = MeasureSpec.getSize(widthMeasureSpec);
        fullHeight = MeasureSpec.getSize(heightMeasureSpec);

        outsideRadius = (int) (fullWidth * 0.35);
        insideRadius = (int) (fullWidth * 0.25);

        centerX = fullWidth / 2;
        centerY = fullWidth / 2;

        findItemPosition();
    }

    //Find position for all item
    private void findItemPosition() {
        for (int i = 0; i < listItem.size(); i++) {
            UnitPosition itemPosition = listPosition.get(i);
//            int diffPosition = i - position;
//            int diffAngle = diffPosition * eachItemAngle;
//            int textWidth = getTextWidth((i == position ? paintTextSelected : paintNormalText),
//                    listItem.get(i));
            itemPosition.xPosition = centerX;
            itemPosition.yPosition = centerY - insideRadius;
            itemPosition.rotationAngle = eachItemAngle;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);

        canvas.rotate(currentAngle, centerX, centerY);

        //Draw all item
        int size = listItem.size();
        for (int j = position; j < size + position; j++) {
            int i = j < size ? j : j - size;
            UnitPosition itemPosition = listPosition.get(i);
            canvas.rotate((i == position ? 0 : eachItemAngle),
                    centerX,
                    centerY);
            canvas.drawText(listItem.get(i),
                    itemPosition.xPosition,
                    itemPosition.yPosition,
                    i == position ? paintTextSelected : paintNormalText);
        }
    }

    private void drawBackground(Canvas canvas) {
        @SuppressLint("DrawAllocation") Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        int center = fullWidth / 2;
        @SuppressLint("DrawAllocation") RectF rect =
                new RectF(center - outsideRadius, center - outsideRadius, center + outsideRadius, center + outsideRadius);
        canvas.drawArc(rect, 0, 360, true, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point centerP = new Point(centerX, centerY);
        Point touchP = new Point((int) event.getX(), (int) event.getY());
        if (Util.calculateDistance(centerP, touchP) <= outsideRadius) {
            handleTouch(event);
            return true;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_UP) rotateToCurrent();
        return false;
    }

    private void handleTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                prevAngle = Util.convertTouchEventPointToAngle(event.getX(), event.getY(), outsideRadius);
                break;
            case MotionEvent.ACTION_MOVE:
                int changeAngle = Util.convertTouchEventPointToAngle(event.getX(), event.getY(), outsideRadius) - prevAngle;
                prevAngle += changeAngle;
                int nAngle = currentAngle + changeAngle;
                if (Math.abs(changeAngle) < 10) {//Maybe convert touch position to angle wrong, so check again to sure it true
                    currentAngle = nAngle;

                    if (currentAngle > eachItemAngle / 2) {
                        String oldUnit = listItem.get(position);
                        //Update new position
                        position--;
                        if (position == -1) position = listItem.size() - 1;

                        //call back to update other view
                        String newUnit = listItem.get(position);
                        if (listener != null) listener.onChangeUnit(oldUnit, newUnit);

                        //Calculate to update current view
                        currentAngle = currentAngle - eachItemAngle;
                        findItemPosition();
                    } else if (currentAngle < -eachItemAngle / 2) {
                        String oldUnit = listItem.get(position);
                        //Update new position
                        position++;
                        if (position == listItem.size()) position = 0;

                        //call back to update other view
                        String newUnit = listItem.get(position);
                        if (listener != null) listener.onChangeUnit(oldUnit, newUnit);

                        //Calculate to update current view
                        currentAngle = eachItemAngle + currentAngle;
                        findItemPosition();
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                rotateToCurrent();
                break;
            default:
                break;
        }
    }

    private void rotateToCurrent() {
        ValueAnimator animator = ValueAnimator.ofInt(currentAngle, 0);
        animator.setDuration(200);
        animator.addUpdateListener(animation -> {
            currentAngle = (int) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    public void setUnit(String unit) {
        this.unit = unit;
        int nPosition = findPositionByUnit(unit);
        if(nPosition>=0) {
            position = nPosition;
            invalidate();
        }
    }

    private int findPositionByUnit(String unit){
        for (int i = 0; i < listItem.size(); i++) {
            if (listItem.get(i).equals(unit)) {
                return i;
            }
        }
        return -1;
    }

    public void setUnitChangeListener(OnUnitChangeListener listener) {
        this.listener = listener;
    }

    public interface OnUnitChangeListener {
        void onChangeUnit(String oldUnitType, String newUnitType);
    }
}
