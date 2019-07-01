package com.ambe.compassseekbar.seekbars;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
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

import static com.ambe.compassseekbar.seekbars.SeekbarConstants.centerRadius;
import static com.ambe.compassseekbar.seekbars.SeekbarConstants.intervalAngle;
import static com.ambe.compassseekbar.seekbars.SeekbarConstants.intervalsCountCir;
import static com.ambe.compassseekbar.seekbars.SeekbarConstants.offsetIntervalAngle;
import static com.ambe.compassseekbar.seekbars.Util.convertTouchEventPointToAngle;


/**
 * Created by AMBE on 10/26/2018 at 10:41 AM.
 */
public class SeekbarCircle extends View {
    public static int INVALID_VALUE = -1;
    private final int MAX_WEIGHT_KG = 360;
    private final int MIN_WEIGHT_KG = 0;
    private int mPoints = MIN_WEIGHT_KG;
    private int mMin = MIN_WEIGHT_KG;
    private int mMax = MAX_WEIGHT_KG;
    private int mStep = 1;
    private int mUpdateTimes = 0;
    private float mPreviousProgress = -1;
    private float mCurrentProgress = 0;
    private String unit = UnitWheelView.UNIT_KG_CM;
    private boolean isMax = false;
    private boolean isMin = false;
    private boolean mClockwise = true;
    private float mTextSize = 24;
    private Paint mTextWeight;
    private float mProgressSweep = 0;
    private List<Interval> intervals = new ArrayList<>();
    private BasePaint intervalHeadingPaint;
    private BasePaint intervalPaint;
    private BasePaint intervalPaintSmall;
    private CircleCenter circleCenter;
    private BasePaint clockCenterPaint;
    private BasePaint xxx;
    private int mRadius = 0;
    private int finalWidth = 0;
    private int finalHeight = 0;
    private Circle circle;
    private SeekbarLine seekbarLine;


    private OnSwagSeekbarChangeListener mOnSwagSeekbarChangeListener;

    public SeekbarCircle(Context context) {
        super(context);
        init(context, null);
    }

    public SeekbarCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        float density = getResources().getDisplayMetrics().density;
        int textColor = ContextCompat.getColor(context, R.color.color_text);
        int textColorW = ContextCompat.getColor(context, R.color.white);

        //  bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_rect_weight);
        mTextSize = (int) (mTextSize * density);

        intervalHeadingPaint = new BasePaint.Builder(R.color.black)
                .setStyle(Paint.Style.FILL)
                .setAntiAlias(true)
                .build();
        intervalHeadingPaint.setTextSize((int) (10 * density));
        intervalPaint = new BasePaint.Builder(R.color.black)
                .setAntiAlias(true)
                .setStyle(Paint.Style.FILL)
                .setStrokeWidth(8)
                .setStrokeCap(Paint.Cap.ROUND)
                .build();
        clockCenterPaint = new BasePaint.Builder(R.color.gray)
                .setAntiAlias(true)
                .setStyle(Paint.Style.FILL)
                .setStrokeWidth(4)
                .build();

        xxx = new BasePaint.Builder(R.color.gray)
                .setAntiAlias(true)
                .setStyle(Paint.Style.STROKE)
                .setStrokeWidth(4)
                .build();
        intervalPaintSmall = new BasePaint.Builder(R.color.black)
                .setAntiAlias(true)
                .setStyle(Paint.Style.FILL)
                .setStrokeWidth(4)
                .setStrokeCap(Paint.Cap.ROUND)
                .build();
        circleCenter = new CircleCenter(centerRadius);
        circleCenter.setCoordinates(new Coordinates(centerRadius, centerRadius));
        seekbarLine = new SeekbarLine();
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
            a.recycle();
        }
        // range check
        mPoints = (mPoints > mMax) ? mMax : mPoints;
        mPoints = (mPoints < mMin) ? mMin : mPoints;
        mProgressSweep = (float) mPoints;

        circle = new Circle();
        Paint mTextPaint = new Paint();
        mTextPaint.setColor(textColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);

        mTextWeight = new Paint();
        mTextWeight.setColor(textColorW);
        mTextWeight.setAntiAlias(true);
        mTextWeight.setStyle(Paint.Style.FILL);
        float mTextSizeW = 30 * density;
        mTextWeight.setTextSize(mTextSizeW);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        finalWidth = MeasureSpec.getSize(widthMeasureSpec);
        finalHeight = MeasureSpec.getSize(heightMeasureSpec); //Set height = width

        mRadius = (finalWidth / 2);

        setMeasuredDimension(finalWidth, finalHeight);
        updateWeight();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(circleCenter.getCoordinates().getCood_X(), circleCenter.getCoordinates().getCood_Y(), (float) (finalWidth / 2.5), clockCenterPaint.getPaint());
        canvas.drawCircle(circleCenter.getCoordinates().getCood_X(), circleCenter.getCoordinates().getCood_Y(), (float) (finalWidth / 2 - 16), xxx.getPaint());
//        canvas.drawText(String.valueOf((int) (mProgressSweep)) + " " + unit, circleCenter.getCoordinates().getCood_X() - mTextWeight.getTextSize() * 3 / 2, circleCenter.getCoordinates().getCood_Y() - finalWidth / 2 - 75, mTextWeight);


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
                    intervalHeadingPaint.getPaint());

        }

    }

    private NumberFormat formatter = new DecimalFormat("#0.0");


    public void initialize() {
        circleCenter.setCoordinates(new Coordinates(finalWidth / 2, finalWidth / 2));

        circle.setRect(16, finalHeight / 4, finalWidth - 2, finalWidth - 16);
        circle.setPath();

        seekbarLine.setStartXY(circleCenter.getCoordinates().getCood_X(), circleCenter.getCoordinates().getCood_Y());
        secondsRotation = CircleGeometry.getRadianAngle(-offsetIntervalAngle + intervalAngle);


        if (intervals.size() == 0) {
            intervals = new ArrayList<>();
            for (int i = 0; i < intervalsCountCir; i++) {
                IntervalLine line = new IntervalLine();
                IntervalHeading heading = new IntervalHeading();
                double linesRotation = CircleGeometry.getRadianAngle((int) (offsetIntervalAngle + intervalAngle + (i * intervalAngle)));

                line.setStartXY(
                        CircleGeometry.calculateX(circleCenter.getCoordinates().getCood_X(), mRadius - 16, linesRotation),
                        CircleGeometry.calculateY(circleCenter.getCoordinates().getCood_Y(), mRadius - 16, linesRotation)
                );
                line.setEndXY(
                        CircleGeometry.calculateX(circleCenter.getCoordinates().getCood_X(), mRadius - 32, linesRotation),
                        CircleGeometry.calculateY(circleCenter.getCoordinates().getCood_Y(), mRadius - 32, linesRotation)
                );
                if (i % 2 == 0) {
                    if (unit.equals(UnitWheelView.UNIT_KG_CM)) {
                        heading.setHeading(String.valueOf((360 - i * 5)));
                        heading.setCoordinates(
                                CircleGeometry.calculateX(circleCenter.getCoordinates().getCood_X(), mRadius - 60, linesRotation)
                                        - (int) (intervalHeadingPaint.getPaint().measureText(heading.getHeading()) / 2),
                                CircleGeometry.calculateY(circleCenter.getCoordinates().getCood_Y(), mRadius - 60, linesRotation)
                                        - (int) ((intervalHeadingPaint.getPaint().descent()
                                        + intervalHeadingPaint.getPaint().ascent()) / 2)
                        );
                    } else {
                        heading.setHeading(formatter.format(((360 - i * 5) * 2.20462262)));
                        heading.setCoordinates(
                                CircleGeometry.calculateX(circleCenter.getCoordinates().getCood_X(), mRadius - 60, linesRotation) - (int) (intervalHeadingPaint.getPaint().measureText(heading.getHeading()) / 2),
                                CircleGeometry.calculateY(circleCenter.getCoordinates().getCood_Y(), mRadius - 60, linesRotation) - (int) ((intervalHeadingPaint.getPaint().descent() + intervalHeadingPaint.getPaint().ascent()) / 2)
                        );

                    }

                    intervals.add(new Interval(line, heading, 0));
                } else {
                    heading.setHeading("");
                    heading.setCoordinates(
                            CircleGeometry.calculateX(circleCenter.getCoordinates().getCood_X(), mRadius - 60, linesRotation)
                                    - (int) (intervalHeadingPaint.getPaint().measureText(heading.getHeading()) / 2),
                            CircleGeometry.calculateY(circleCenter.getCoordinates().getCood_Y(), mRadius - 60, linesRotation)
                                    - (int) ((intervalHeadingPaint.getPaint().descent()
                                    + intervalHeadingPaint.getPaint().ascent()) / 2)
                    );
                    intervals.add(new Interval(line, heading, 1));
                }
            }
            invalidate();

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        this.getParent().requestDisallowInterceptTouchEvent(true);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mOnSwagSeekbarChangeListener != null)
                    mOnSwagSeekbarChangeListener.onStartTrackingTouch(this);
                oldAngle = convertTouchEventPointToAngle(event.getX(), event.getY(), mRadius);
                break;
            case MotionEvent.ACTION_MOVE:
                updateOnTouch(event);
                break;
            case MotionEvent.ACTION_UP:
                if (mOnSwagSeekbarChangeListener != null)
                    mOnSwagSeekbarChangeListener.onStopTrackingTouch(this);
                setPressed(false);
//                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mOnSwagSeekbarChangeListener != null)
                    mOnSwagSeekbarChangeListener.onStopTrackingTouch(this);
                setPressed(false);
//                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;

    }

    private double mTouchAngle;

    private void updateOnTouch(MotionEvent event) {
        setPressed(true);
        double change = convertTouchEventPointToAngle(event.getX(), event.getY(), mRadius) - oldAngle;
        oldAngle = oldAngle + change;
        double nAngle = mTouchAngle + change;
        int progress = convertAngleToProgress(nAngle);
        if (progress <= 300 && progress >= 20 && Math.abs(change) < 10) {//Maybe convert touch position to angle wrong, so check again to sure it true
            mTouchAngle = nAngle;
            updateProgress(progress, true);
        }
    }

    private int convertAngleToProgress(double mTouchAngle) {
        return (int) Math.round(valuePerDegree() * mTouchAngle);
    }

    private double oldAngle;

    private void updateProgress(int progress, boolean fromUser) {
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
        mPoints = progress - (progress % mStep);
        if (mUpdateTimes > 1 && !isMin && !isMax) {
            if (mPreviousProgress >= maxDetectValue && mCurrentProgress <= minDetectValue &&
                    mPreviousProgress > mCurrentProgress) {
                isMax = true;
                progress = mMax;
                mPoints = mMax;
                if (mOnSwagSeekbarChangeListener != null) {
                    mOnSwagSeekbarChangeListener
                            .onSeekbarChanged(this, progress, fromUser);
                    return;
                }
            } else if ((mCurrentProgress >= maxDetectValue
                    && mPreviousProgress <= minDetectValue
                    && mCurrentProgress > mPreviousProgress) || mCurrentProgress <= mMin) {
                isMin = true;
                progress = mMin;
                mPoints = mMin;
                if (mOnSwagSeekbarChangeListener != null) {
                    mOnSwagSeekbarChangeListener
                            .onSeekbarChanged(this, progress, fromUser);
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

            if (mOnSwagSeekbarChangeListener != null) {
                progress = progress - (progress % mStep);

                mOnSwagSeekbarChangeListener
                        .onSeekbarChanged(this, progress, fromUser);
            }
            mProgressSweep = (float) progress / valuePerDegree();
            updateWeight();
            invalidate();
        }


    }

    public void setmOnSwagSeekbarChangeListener(OnSwagSeekbarChangeListener mOnSwagSeekbarChangeListener) {
        this.mOnSwagSeekbarChangeListener = mOnSwagSeekbarChangeListener;
    }

    private double secondsRotation;


    public void setProgressSweep(float mProgressSweep) {
        this.mProgressSweep = mProgressSweep;
        mTouchAngle = convertProgressToAngle(mProgressSweep);
        updateWeight();
        invalidate();
    }

    private double convertProgressToAngle(float progress) {
        return progress * 1d / valuePerDegree();
    }

    private void updateWeight() {
        if (circleCenter.getCoordinates() != null) {
            int thumbAngle = (int) (mProgressSweep + 160);
            secondsRotation = CircleGeometry.getRadianAngle((-offsetIntervalAngle + intervalAngle + thumbAngle));
            double x;


            seekbarLine.setEndXY(
                    CircleGeometry.calculateX(circleCenter.getCoordinates().getCood_X(), mRadius - 50, secondsRotation),
                    CircleGeometry.calculateY(circleCenter.getCoordinates().getCood_Y(), mRadius - 50, secondsRotation)
            );

            if (intervals.size() != 0) {
                for (int i = 0; i < intervalsCountCir && i < intervals.size(); i++) {
                    IntervalLine line = intervals.get(i).getIntervalLine();
                    IntervalHeading heading = intervals.get(i).getIntervalHeading();


                    x = CircleGeometry.getRadianAngle(offsetIntervalAngle + intervalAngle + (i * intervalAngle) + thumbAngle);
                    line.setStartXY(
                            CircleGeometry.calculateX(circleCenter.getCoordinates().getCood_X(), mRadius - 16, x + 5),
                            CircleGeometry.calculateY(circleCenter.getCoordinates().getCood_Y(), mRadius - 16, x + 5)
                    );
                    line.setEndXY(
                            CircleGeometry.calculateX(circleCenter.getCoordinates().getCood_X(), mRadius - 32, x + 5),
                            CircleGeometry.calculateY(circleCenter.getCoordinates().getCood_Y(), mRadius - 32, x + 5)
                    );
                    if (i % 2 == 0) {
                        if (unit.equals(UnitWheelView.UNIT_KG_CM)) {
                            heading.setHeading(String.valueOf((360 - i * 5)));
                        } else {
                            heading.setHeading(formatter.format(((360 - i * 5) * 2.20462262)));

                        }
                        heading.setCoordinates(
                                CircleGeometry.calculateX(circleCenter.getCoordinates().getCood_X(), mRadius - 60, x + 5) - (int) (intervalHeadingPaint.getPaint().measureText(heading.getHeading()) / 2),
                                CircleGeometry.calculateY(circleCenter.getCoordinates().getCood_Y(), mRadius - 60, x + 5) - (int) ((intervalHeadingPaint.getPaint().descent() + intervalHeadingPaint.getPaint().ascent()) / 2)
                        );

                        intervals.set(i, new Interval(line, heading, 0));
                    } else {
                        heading.setHeading("");

                        heading.setCoordinates(
                                CircleGeometry.calculateX(circleCenter.getCoordinates().getCood_X(), mRadius - 60, x + 5)
                                        - (int) (intervalHeadingPaint.getPaint().measureText(heading.getHeading()) / 2),
                                CircleGeometry.calculateY(circleCenter.getCoordinates().getCood_Y(), mRadius - 60, x + 5)
                                        - (int) ((intervalHeadingPaint.getPaint().descent()
                                        + intervalHeadingPaint.getPaint().ascent()) / 2)
                        );
                        intervals.set(i, new Interval(line, heading, 1));
                    }
                }


            }
        }
    }

    private float valuePerDegree() {
        return (float) (mMax) / 360.0f;
    }

    public interface OnSwagSeekbarChangeListener {
        void onSeekbarChanged(SeekbarCircle seekbarCircle, int progress, boolean fromUser);

        void onStartTrackingTouch(SeekbarCircle seekbarCircle);

        void onStopTrackingTouch(SeekbarCircle seekbarCircle);

    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
        intervals.clear();
        initialize();
    }
}
