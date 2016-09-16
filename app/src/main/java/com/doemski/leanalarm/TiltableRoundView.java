package com.doemski.leanalarm;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TiltableRoundView extends View{

    private boolean mTiltable;
    private Paint mCirclePaint;
    private float mDiameter;
    private RectF mBoundingRectangle;
    private static final String TAG = "[TiltableRoundView]";
    public TiltableRoundView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TiltableRoundView,
                0, 0);

        try {
            mTiltable = a.getBoolean(R.styleable.TiltableRoundView_tiltable, false);
        } finally {
            a.recycle();
        }
        init();
    }

    @Override
    public void onDraw(Canvas canvas) {
        //canvas.drawPaint(mCirclePaint);
        super.onDraw(canvas);
        canvas.drawOval(mBoundingRectangle, mCirclePaint);
    }

    @Override
    protected void onSizeChanged (int w,
                        int h,
                        int oldw,
                        int oldh) {
        // Account for padding
        float xpad = (float)(getPaddingLeft() + getPaddingRight());
        float ypad = (float)(getPaddingTop() + getPaddingBottom());

        float ww = (float)w - xpad;
        float hh = (float)h - ypad;

        // Figure out how big we can make the pie.
        mDiameter = Math.min(ww, hh);
        int startTop = 0;
        int startLeft = 0;
        int endBottom = startTop + (int) mDiameter;
        int endRight = startLeft + (int) mDiameter;

        mBoundingRectangle = new RectF(startLeft, startTop, endRight, endBottom);

        Log.d(TAG, mDiameter + "");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = MeasureSpec.getSize(w) - getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

    //TODO: Recources.getColor deprecated
    private void init() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(getResources().getColor(R.color.colorPrimary));
    }
}
