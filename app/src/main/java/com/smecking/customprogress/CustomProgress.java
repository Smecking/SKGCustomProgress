package com.smecking.customprogress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Smecking on 2016/12/1.
 */

public class CustomProgress extends View {

    /**
     * 文字颜色
     */
private int textColor;
    /**
     * 文字大小
     */
    private float textSize;
    /**
     * reached部分颜色
     */
    private int reachedColor;

    /**
     * reached部分的高度
     * */
    private float reachedHeight;
    /**
     * unReached部分的颜色
     * */
    private int unReachedColor;
    /**
     * unReached部分的高度
     * */
    private float unReachedHeight;
    /**
     * 当前进度
     * */
    private int currentProgress;
    /**
     * 总进度
     * */
    private int maxProgress;

    /**
     * reach of rect
     * */
    private RectF reachedRectF = new RectF(0,0,0,0);

    /**
     * unReach of rect
     * */
    private RectF unReachedRectF = new RectF(0,0,0,0);

    private Paint textPaint;

    private Paint reachedPaint;

    private Paint unReachedPaint;
    private String currentDrawText;

    private boolean dawUnreachedBar = true;

    private boolean drawReachedBar = true;
    private float drawTextStart,drawTextEnd;

    public CustomProgress(Context context) {
        this(context,null);
    }

    public CustomProgress(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.CustomProgress);
        textColor  = a.getColor(R.styleable.CustomProgress_textColor, Color.parseColor("#3498DB"));
        textSize = sp2px(context, a.getDimension(R.styleable.CustomProgress_textSize, 14f));
        reachedHeight = dip2px(context, a.getDimension(R.styleable.CustomProgress_reachedHeight, 1.25f));
        reachedColor = a.getColor(R.styleable.CustomProgress_reachedColor, Color.parseColor("#3498DB"));
        unReachedHeight = dip2px(context, a.getDimension(R.styleable.CustomProgress_unReachedHeight, 0.75f));
        unReachedColor = a.getColor(R.styleable.CustomProgress_unReachedColor, Color.parseColor("#CCCCCC"));

        currentProgress = a.getInt(R.styleable.CustomProgress_currentProgress, 0);
        maxProgress = a.getInt(R.styleable.CustomProgress_maxProgress, 100);

        reachedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        reachedPaint.setColor(reachedColor);

        unReachedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unReachedPaint.setColor(unReachedColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);

        a.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 一个MeasureSpec由大小和模式组成。
         * 它有三种模式：
         * UNSPECIFIED(未指定),父元素不对子元素施加任何束缚，子元素可以得到任意想要的大小；
         * EXACTLY(完全)，父元素决定子元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小；
         * AT_MOST(至多)，子元素至多达到指定大小的值。
         * */
        setMeasuredDimension(measureWidth(widthMeasureSpec),measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int heightMeasureSpec) {
        int result;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int padding = getPaddingTop()+getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY){
            result = size;
        }else{
            result = getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST){
                result = Math.min(result, size);
            }
        }
        return result;
    }

    private int measureWidth(int widthMeasureSpec) {
        int result;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int padding = getPaddingLeft()+getPaddingRight();
        if (mode == MeasureSpec.EXACTLY){
            result = size;
        }else{
            result = getSuggestedMinimumWidth();
            result += padding;
            if (mode == MeasureSpec.AT_MOST){
                result = Math.max(result, size);
            }
        }
        return result;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) textSize;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return Math.max((int) textSize, Math.max((int) reachedHeight, (int) unReachedHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        calculateDrawRectF();
        if (drawReachedBar) {
            canvas.drawRect(reachedRectF, reachedPaint);
        }

        canvas.drawText(currentDrawText, drawTextStart, drawTextEnd, textPaint);

        if (dawUnreachedBar) {
            canvas.drawRect(unReachedRectF, unReachedPaint);
        }
    }

    private void calculateDrawRectF() {
        currentDrawText = String.format("%d", currentProgress * 100 / maxProgress);
        currentDrawText = currentDrawText+"%";
        float drawTextWidth = textPaint.measureText(currentDrawText);

        if (currentProgress == 0){
            drawReachedBar = false;
            drawTextStart = getPaddingLeft();
            reachedRectF.right = 0;
        }else{
            drawReachedBar = true;
            reachedRectF.left = getPaddingLeft();
            reachedRectF.top = getHeight()/2.0f-reachedHeight/2.0f;
            reachedRectF.right = (getWidth()-getPaddingLeft()-getPaddingRight())/(maxProgress*1.0f) * (float)currentProgress;
            reachedRectF.bottom = getHeight()/2.0f+reachedHeight/2.0f;
            drawTextStart = reachedRectF.right;
        }

        if ((drawTextStart+drawTextWidth) >= (getWidth()-getPaddingRight())){
            drawTextStart = getWidth() - getPaddingRight() - drawTextWidth;
            reachedRectF.right = drawTextStart;
        }

        drawTextEnd =  (int) ((getHeight() / 2.0f) - ((textPaint.descent() + textPaint.ascent()) / 2.0f));
        float unreachedBarStart = reachedRectF.right+drawTextWidth;
        if (unreachedBarStart >= getWidth() - getPaddingRight()) {
            dawUnreachedBar = false;
        }else{
            dawUnreachedBar = true;
            unReachedRectF.left = unreachedBarStart;
            unReachedRectF.right = getWidth() - getPaddingRight();
            unReachedRectF.top = getHeight() / 2.0f + -unReachedHeight / 2.0f;
            unReachedRectF.bottom = getHeight() / 2.0f + unReachedHeight / 2.0f;
        }
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        if (currentProgress<=maxProgress && currentProgress >=0) {
            this.currentProgress = currentProgress;
            invalidate();
        }
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    /**
     * dp2px
     */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    /**
     * sp2px
     */
    public static float sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (spValue * fontScale + 0.5f);
    }
}
