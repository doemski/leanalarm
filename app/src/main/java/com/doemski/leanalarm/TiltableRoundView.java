package com.doemski.leanalarm;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class TiltableRoundView extends RelativeLayout {

    private boolean mTiltable;
    private Paint mCirclePaint;
    private float mDiameter;
    private RectF mBoundingRectangle;
    private int mColor;
    private static final String TAG = "[TiltableRoundView]";
    private Point mScreenSize;
    float mRadiusFraction;

    public TiltableRoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        TypedArray styleAttrs = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TiltableRoundView,
                0, 0);

        //get screen dimensions
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        mScreenSize = new Point();
        display.getSize(mScreenSize);

        try {
            mRadiusFraction = styleAttrs.getFloat(R.styleable.TiltableRoundView_radius_fraction, -1);
            mTiltable = styleAttrs.getBoolean(R.styleable.TiltableRoundView_tiltable, false);
            //TODO: deprecated
            mColor = styleAttrs.getColor(R.styleable.TiltableRoundView_background_color, getResources().getColor(R.color.colorAccent));
        } finally {
            styleAttrs.recycle();
        }
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "ChildCount View: " + getChildCount());
        final int count = getChildCount();

        int j = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            int diameter = getMeasuredHeight(), radius = diameter/2;
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            if (child.getVisibility() != GONE) {
                /*switch (j) {
                    case 0:
                        child.layout(0, radius, child.getMeasuredWidth(), child.getMeasuredHeight());
                        j++;
                        break;
                    case 1:
                        child.layout(radius, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                        j++;
                        break;
                    case 2:
                        child.layout(diameter, radius, child.getMeasuredWidth(), child.getMeasuredHeight());
                        j++;
                        break;
                    case 3:
                        child.layout(radius, diameter, child.getMeasuredWidth(), child.getMeasuredHeight());
                        j = 0;
                        break;
                }*/

                Log.d(TAG, child.getMeasuredWidth() + ", " + child.getMeasuredHeight());

            }
        }

    }

    @Override
    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        Log.d(TAG, "onDraw called");
        //canvas.drawPaint(mCirclePaint);
        canvas.drawOval(mBoundingRectangle, mCirclePaint);
        //canvas.drawCircle(0,0,100,mCirclePaint);
    }

    @Override
    protected void onSizeChanged(int w,
                                 int h,
                                 int oldw,
                                 int oldh) {
        // Account for padding
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;

        // Figure out how big we can make the pie.
        mDiameter = Math.min(ww, hh);
        int startTop = 0;
        int startLeft = 0;
        int endBottom = startTop + (int) mDiameter;
        int endRight = startLeft + (int) mDiameter;

        mBoundingRectangle = new RectF(startLeft, startTop, endRight, endBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);


        //w = mScreenSize.x - getPaddingLeft() - getPaddingRight();
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, r.getDisplayMetrics());
        w = (int) (mScreenSize.x - px);

        int minh = MeasureSpec.getSize(w) - getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0);

        //Set maximum possible radius if not specified
        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }

        if (mRadiusFraction != -1) {
            setMeasuredDimension(Math.round(mRadiusFraction * w), Math.round(mRadiusFraction * w));
        } else {
            setMeasuredDimension(w, w);
        }
    }

    private void init() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(mColor);
    }


}
