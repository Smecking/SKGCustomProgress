package com.smecking.customprogress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * Created by Smecking on 2016/12/1.
 */

public class CustomProgress extends ProgressBar {

    private static final int DEFLUT_TEXT_SIZE = 10;
    private static final int DEFLUT_TEXT_COLOR = 0xDDFF6633;
    private static final int DEFLUT_UNREACH_COLOR = 0xDDCC6666;
    private static final int DEFLUT_REACH_COLOR = 0xDDCC6633;
    private static final int DEFLUT_UNREACH_HEIGHT = 2;
    private static final int DEFLUT_REACH_HEIGHT = 2;
    private static final int DEFLUT_TEXT_OFFSET = 10;


    private int mTextsize = sp2px(DEFLUT_TEXT_SIZE);
    private int mTextColor = DEFLUT_TEXT_COLOR;
    private int mUnreachColor = DEFLUT_UNREACH_COLOR;
    private int mReachColor = DEFLUT_REACH_COLOR;
    private int mUnreachHeight = dp2px(DEFLUT_UNREACH_HEIGHT);
    private int mReachHeight = dp2px(DEFLUT_REACH_HEIGHT);
    private int mTextOffset = dp2px(DEFLUT_TEXT_OFFSET);

    private Paint mPaint = new Paint();
    //控件的宽度减去padding值
    private int mRealWidth;

    public CustomProgress(Context context) {
        this(context, null);
    }

    public CustomProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取自定义属性
        getStyledAttr(context,attrs);
    }

    private void getStyledAttr(Context context,AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomProgress);

        mTextsize = (int) typedArray.getDimension(R.styleable.CustomProgress_text_size, mTextsize);
        mTextColor = typedArray.getColor(R.styleable.CustomProgress_text_color, mTextColor);
        mUnreachColor = typedArray.getColor(R.styleable.CustomProgress_unreach_color, mUnreachColor);
        mUnreachHeight = (int) typedArray.getDimension(R.styleable.CustomProgress_unreach_height, mUnreachHeight);
        mReachColor = typedArray.getColor(R.styleable.CustomProgress_reach_color, mReachColor);
        mReachHeight = (int) typedArray.getDimension(R.styleable.CustomProgress_reach_height, mReachHeight);
        mTextOffset = (int) typedArray.getDimension(R.styleable.CustomProgress_text_offset, mTextOffset);
        typedArray.recycle();

        mPaint.setTextSize(mTextsize);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthVal = MeasureSpec.getSize(widthMeasureSpec);

        int height = measureHeight(heightMeasureSpec);

        setMeasuredDimension(widthVal, height);
        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            result = getPaddingBottom() + getPaddingTop() + Math.max(Math.max(mReachHeight, mUnreachHeight), Math.abs(textHeight));
        }
        if (mode == MeasureSpec.AT_MOST) {
            result = Math.min(result, size);
        }
        return result;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2);
        boolean noNeedUnreach = false;
        float radio = getProgress() * 1.0f / getMax();
        String text = getProgress() + "%";
        int textWidth = (int) mPaint.measureText(text);
        float progressX = radio * mRealWidth;
        if (progressX + textWidth > mRealWidth) {
            progressX = mRealWidth - textWidth;
            noNeedUnreach = true;
        }
        float endX = progressX - mTextOffset / 2;
        if (endX > 0) {
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }
        //绘制字体
        mPaint.setColor(mTextColor);
        int y = (int) (-(mPaint.descent() + mPaint.ascent()) / 2);
        canvas.drawText(text, progressX, y, mPaint);

        //绘制unreach
        if (!noNeedUnreach) {
            float start = progressX + mTextOffset / 2 + textWidth;
            mPaint.setColor(mUnreachColor);
            mPaint.setStrokeWidth(mUnreachHeight);
            canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
        }

        canvas.restore();
    }

    private int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

    private int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources().getDisplayMetrics());
    }
}
