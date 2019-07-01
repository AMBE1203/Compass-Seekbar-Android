package com.ambe.compassseekbar.seekbars;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.ambe.compassseekbar.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static com.ambe.compassseekbar.seekbars.SeekbarConstants.intervalsCount;


/**
 * Created by AMBE on 10/24/2018 at 10:33 AM.
 */
public class SeekbarVertical extends View {

    public static int INVALID_VALUE = -1;
    public static final int MAX = 100;
    public static final int MIN = 0;
    private int mPoints = MIN;
    private int mMin = MIN;
    private int mMax = MAX;
    private int mStep = 1;
    private int mUpdateTimes = 0;
    private float mPreviousProgress = -1;
    private float mCurrentProgress = 0;
    private String unit = UnitWheelView.UNIT_KG_CM;
    private boolean isMax = false;
    private boolean isMin = false;
    private boolean mClockwise = true;
    private boolean mEnabled = true;
    private float mTextSize = 12;
    private Paint mTextPaint;
    private Rect mTextRect = new Rect();
    private float mProgressSweep = 0;
    private List<Interval> intervals = new ArrayList<>();
    private List<Interval> listLines = new ArrayList<>();
    private BasePaint intervalPaint;
    private BasePaint intervalLines;
    private BasePaint intervalPaintSmall;
    private float width;
    private float height;
    private int mIndicatorYStart;
    private GenderImage genderImage;



    public void setGenderImage(GenderImage genderImage) {
        this.genderImage = genderImage;
    }

    private OnCustomsSeekbarChangeListener mOnCustomsSeekbarChangeListener;

    public SeekbarVertical(Context context) {
        super(context);
        init(context, null);
    }

    public SeekbarVertical(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        float density = getResources().getDisplayMetrics().density;
        int textColor = ContextCompat.getColor(context, R.color.color_text);
        intervalPaint = new BasePaint.Builder(R.color.black)
                .setAntiAlias(true)
                .setStyle(Paint.Style.FILL)
                .setStrokeWidth(4)
                .setStrokeCap(Paint.Cap.ROUND)
                .build();
        intervalLines = new BasePaint.Builder(getResources().getColor(R.color.colorBlue))
                .setAntiAlias(true)
                .setStyle(Paint.Style.FILL)
                .setStrokeWidth(4)
                .setStrokeCap(Paint.Cap.ROUND)
                .build();
        intervalPaintSmall = new BasePaint.Builder(R.color.black)
                .setAntiAlias(true)
                .setStyle(Paint.Style.FILL)
                .setStrokeWidth(4)
                .setStrokeCap(Paint.Cap.ROUND)
                .build();
        mTextSize = (int) (mTextSize * density);
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwagPoints, 0, 0);
            mPoints = a.getInteger(R.styleable.SwagPoints_points, mPoints);
            mMin = a.getInteger(R.styleable.SwagPoints_min, mMin);
            mMax = a.getInteger(R.styleable.SwagPoints_max, mMax);
            mStep = a.getInteger(R.styleable.SwagPoints_step, mStep);
            mTextSize = (int) a.getDimension(R.styleable.SwagPoints_textSize, mTextSize);
            textColor = a.getColor(R.styleable.SwagPoints_textColor, textColor);
            mClockwise = a.getBoolean(R.styleable.SwagPoints_clockwise,
                    mClockwise);
            mEnabled = a.getBoolean(R.styleable.SwagPoints_enabled, mEnabled);
            a.recycle();
        }
        // range check
        mPoints = (mPoints > mMax) ? mMax : mPoints;
        mPoints = (mPoints < mMin) ? mMin : mPoints;
        mProgressSweep = (float) mPoints;

        mTextPaint = new Paint();
        mTextPaint.setColor(textColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);

        genderImage = new GenderImage(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        updateLines();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String textPoint = String.valueOf(mPoints);
        mTextPaint.getTextBounds(textPoint, 0, textPoint.length(), mTextRect);
        if (mEnabled) {
            canvas.drawLine(9 * width / 10, height / 10, 9 * width / 10, height, intervalPaint.getPaint());
            if (unit.equals(UnitWheelView.UNIT_KG_CM)) {
                canvas.drawText(String.valueOf((int) (mProgressSweep * 2)) + " " + unit.split("/")[1], width / 10, height - mIndicatorYStart - 24, mTextPaint);
            } else {
                canvas.drawText(formatter.format((mProgressSweep * 2) * 0.032808399) + " " + unit.split("/")[1], width / 10, height - mIndicatorYStart - 24, mTextPaint);

            }
        }
        for (Interval interval : intervals) {
            if (interval.getSize() == 0) {
                canvas.drawLine(interval.getIntervalLine().getStartXY().getCood_X(),
                        interval.getIntervalLine().getStartXY().getCood_Y(),
                        interval.getIntervalLine().getEndXY().getCood_X(),
                        interval.getIntervalLine().getEndXY().getCood_Y(),
                        intervalPaint.getPaint());
            } else {
                canvas.drawLine(interval.getIntervalLine().getStartXY().getCood_X(),
                        interval.getIntervalLine().getStartXY().getCood_Y(),
                        interval.getIntervalLine().getEndXY().getCood_X(),
                        interval.getIntervalLine().getEndXY().getCood_Y(),
                        intervalPaintSmall.getPaint());
            }
            /* drawing the text with interval */
            canvas.drawText(interval.getIntervalHeading().getHeading(),
                    interval.getIntervalHeading().getCoordinates().getCood_X(),
                    interval.getIntervalHeading().getCoordinates().getCood_Y(),
                    mTextPaint);
        }

        for (Interval interval : listLines) {
            canvas.drawLine(interval.getIntervalLine().getStartXY().getCood_X(),
                    interval.getIntervalLine().getStartXY().getCood_Y() - mIndicatorYStart,
                    interval.getIntervalLine().getEndXY().getCood_X(),
                    interval.getIntervalLine().getEndXY().getCood_Y() - mIndicatorYStart,
                    intervalLines.getPaint());
        }
        genderImage.drawGenderImage(canvas);
    }

    private NumberFormat formatter = new DecimalFormat("#0.0");


    public void initialize() {
        if (intervals == null || intervals.size() == 0) {
            genderImage.currentX = width / 2 - genderImage.sizeX / 2;
            genderImage.currentY = height - genderImage.currentSize;
            for (int i = 0; i < intervalsCount; i++) {
                if (i % 2 == 0) {
                    IntervalLine line = new IntervalLine();
                    IntervalHeading heading = new IntervalHeading();
                    line.setStartXY((int) (0.9 * width), (int) (height - 0.0225 * height * i));
                    line.setEndXY((int) (0.9 * width - 30), (int) (height - 0.0225 * height * i));


                    if (unit.equals(UnitWheelView.UNIT_KG_CM)) {
                        heading.setHeading(String.valueOf(i * 5));
                    } else {
                        if (i == 0) {
                            heading.setHeading("0");
                        } else {
                            heading.setHeading(formatter.format(i * 5 * 0.032808399));
                        }
                    }
                    heading.setCoordinates((int) (0.9 * width - 100), (int) (height - 0.0225 * height * i));
                    intervals.add(new Interval(line, heading, 0));
                } else {
                    IntervalLine line = new IntervalLine();
                    IntervalHeading heading = new IntervalHeading();
                    line.setStartXY((int) (0.9 * width), (int) (height - 0.0225 * height * i));
                    line.setEndXY((int) (0.9 * width - 10), (int) (height - 0.0225 * height * i));
                    heading.setHeading("");
                    heading.setCoordinates((int) (0.9 * width - 60), (int) (height - 0.0225 * height * i));
                    intervals.add(new Interval(line, heading, 1));
                }
            }
            if (listLines == null || listLines.size() == 0) {
                for (int i = 0; i < 17; i++) {
                    IntervalLine line = new IntervalLine();
                    IntervalHeading heading = new IntervalHeading();
                    heading.setHeading("");
                    line.setStartXY((int) (9 * width / 10 - 0.05 * width * i), (int) (height));
                    line.setEndXY((int) (9 * width / 10 - 30 - 0.05 * width * i), (int) (height));
                    listLines.add(new Interval(line, heading, 0));

                }
            }
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnabled) {
            this.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mOnCustomsSeekbarChangeListener != null) {
                        mOnCustomsSeekbarChangeListener.onStartTrackingTouch(this);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_UP:
                    if (mOnCustomsSeekbarChangeListener != null) {
                        mOnCustomsSeekbarChangeListener.onStopTrackingTouch(this);
                    }
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (mOnCustomsSeekbarChangeListener != null) {
                        mOnCustomsSeekbarChangeListener.onStopTrackingTouch(this);
                    }
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
        return false;
    }

    private void updateOnTouch(MotionEvent event) {
        setPressed(true);
        int progress = convertPointToProgress(event.getX(), event.getY());

        if (progress >= 50) updateProgress(progress, true);
    }

    private int convertPointToProgress(float x, float y) {
        return (int) (((4 * height / 5) - y) * 100 / (3 * height / 5));
    }

    private void updateProgress(int progress, boolean fromUser) {
        // detect points change closed to max or min
        final int maxDetectValue = (int) ((double) mMax * 0.95);
        final int minDetectValue = (int) ((double) mMax * 0.05) + mMin;
        mUpdateTimes++;
        if (progress == INVALID_VALUE) {
            return;
        }
        if (progress > maxDetectValue && mPreviousProgress == INVALID_VALUE) {
            return;
        }

        if (mUpdateTimes == 1) {
            mCurrentProgress = progress;
        } else {
            mPreviousProgress = mCurrentProgress;
            mCurrentProgress = progress;
        }

        // draw text progress
        mPoints = progress - (progress % mStep);

        if (mUpdateTimes > 1 && !isMin && !isMax) {
            if (mPreviousProgress >= maxDetectValue && mCurrentProgress <= minDetectValue && mPreviousProgress > mCurrentProgress) {
                isMax = true;
                progress = mMax;
                // draw text progress
                mPoints = mMax;

                if (mOnCustomsSeekbarChangeListener != null) {
                    mOnCustomsSeekbarChangeListener.onChanged(this, progress, fromUser);
                    return;
                }
            } else if ((mCurrentProgress >= maxDetectValue
                    && mPreviousProgress <= minDetectValue
                    && mCurrentProgress > mPreviousProgress) || mCurrentProgress <= mMin) {
                isMin = true;
                progress = mMin;
                // draw text progress
                mPoints = mMin;
                if (mOnCustomsSeekbarChangeListener != null) {
                    mOnCustomsSeekbarChangeListener.onChanged(this, progress, fromUser);
                    return;
                }
            }
            invalidate();
        } else {
            if (isMax & (mCurrentProgress < mPreviousProgress) && mCurrentProgress >= maxDetectValue) {
                isMax = false;
            }
            if (isMin
                    && (mPreviousProgress < mCurrentProgress)
                    && mPreviousProgress <= minDetectValue && mCurrentProgress <= minDetectValue
                    && mPoints >= mMin) {
                isMin = false;
            }

        }

        if (!isMax && !isMin) {
            progress = (progress > mMax) ? mMax : progress;
            progress = (progress < mMin) ? mMin : progress;

            if (mOnCustomsSeekbarChangeListener != null) {
                progress = progress - (progress % mStep);
                mOnCustomsSeekbarChangeListener.onChanged(this, progress, fromUser);
            }

            mProgressSweep = (float) progress;

            updateLines();
            invalidate();
        }
    }

    private void updateLines() {

        mIndicatorYStart = (int) (9 * height * mProgressSweep / 1000);
        genderImage.currentSize = mIndicatorYStart;
        genderImage.currentY = height - mIndicatorYStart;

    }

    public interface OnCustomsSeekbarChangeListener {
        void onChanged(SeekbarVertical seekbarVertical, int progress, boolean frommUser);

        void onStartTrackingTouch(SeekbarVertical seekbarVertical);

        void onStopTrackingTouch(SeekbarVertical seekbarVertical);

    }

    public float getCurrentProgress() {
        return mCurrentProgress;
    }

    public void setCurrentProgress(float mCurrentProgress) {
        this.mCurrentProgress = mCurrentProgress;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public void setTextColor(int textColor) {
        mTextPaint.setColor(textColor);
        invalidate();
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int mMax) {
        if (mMax <= mMin)
            throw new IllegalArgumentException("Max should not be less than min.");
        this.mMax = mMax;
    }

    public int getMin() {
        return mMin;
    }

    public void setMin(int min) {
        if (mMax <= mMin)
            throw new IllegalArgumentException("Min should not be greater than max.");
        mMin = min;
    }

    public int getStep() {
        return mStep;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
        intervals.clear();
        initialize();
    }

    public void setStep(int step) {
        mStep = step;
    }



    public void setmOnCustomsSeekbarChangeListener(OnCustomsSeekbarChangeListener mOnCustomsSeekbarChangeListener) {
        this.mOnCustomsSeekbarChangeListener = mOnCustomsSeekbarChangeListener;
    }


    public void setmIndicatorYStart(int currentProgerss) {

        this.mIndicatorYStart = (int) (9 * height * currentProgerss / 1000);
        this.mProgressSweep = currentProgerss;


    }


}
