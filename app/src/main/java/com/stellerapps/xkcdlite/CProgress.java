package com.stellerapps.xkcdlite;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

public class CProgress extends View {
    private float progressDegree = 1f;
    private float progressPercent;
    private float currentProgressDegree = 0f;
    private float aVelocity = 0;
    private float springiness = 1.1f;
    private float damping = 0.2f;
    private float translateX, translateY;
    private final float TOLERANCE = 0.01F;

    private long lastTime = 0;
    private long now;

    private int strokeWidth = 50;
    private int progressColor = Color.rgb(224, 99, 38);
    private int bgColor = Color.rgb(232, 232, 232);

    private Paint progressPaint;
    private Paint bgPaint;

    private RectF bounds = new RectF();

    private boolean fullProgress = false;

    public CProgress (Context context){
        super(context);
        updateProgressPaint();
        updateBgPaint();
    }

    public CProgress(Context context, AttributeSet attr){
        super(context,attr);
        updateProgressPaint();
        updateBgPaint();
    }

    @Override
    public void onDraw(final Canvas canvas){
        canvas.translate(translateX,translateY);

        if (currentProgressDegree>360f)
            fullProgress = true;

        if(!fullProgress){
            canvas.drawArc(bounds,270,-(360-currentProgressDegree),false,bgPaint);
        }

        canvas.drawArc(bounds,270,fullProgress?360:currentProgressDegree,false,progressPaint);
    }

    @Override
    public void onMeasure(final int wSpec,final int hSpec){
        final int height = getDefaultSize(getSuggestedMinimumHeight()+getPaddingTop()+getPaddingBottom(),hSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth()+getPaddingLeft()+getPaddingRight(),wSpec);
        final int side;

        if(hSpec == MeasureSpec.UNSPECIFIED){
            side = width;
        }else if(wSpec == MeasureSpec.UNSPECIFIED){
            side = height;
        }else{
            side = Math.min(width,height);
        }

        setMeasuredDimension(side,side);

        final float radius = side /2f;
        final float stroke = strokeWidth /2f;
        final float netRadius = radius - stroke - 0.5f;
        bounds.set(-netRadius,-netRadius,netRadius,netRadius);
        translateX = radius;
        translateY = radius;
    }

    public void updateProgressPaint(){
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth/2);
        invalidate();
    }

    public void updateBgPaint(){
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(strokeWidth/2);
        invalidate();
    }

    public float percentToDegree(float percent){
        return (percent/100f)*360f;
    }

    public void setProgress(float percent){
        progressDegree = percentToDegree(percent);
        removeCallbacks(animator);
        post(animator);
    }

    public void setProgressColor(int color){
        progressColor = color;
        updateProgressPaint();

    }

    public void setBgColor(int color){
        bgColor = color;
        updateBgPaint();
    }

    public void setStrokeWidth(int stroke){
        strokeWidth = stroke;
        updateBgPaint();
        updateProgressPaint();
    }

    public float getProgressPercent(){
        return progressPercent;
    }

    public void update(long now){
        long dt = Math.min(now-lastTime,50);
        aVelocity += (progressDegree - currentProgressDegree)*springiness;
        aVelocity *= (1-damping);
        currentProgressDegree += aVelocity*dt/1000;
        lastTime = now;
    }

    public boolean isAtRest(){
        boolean standingStill = Math.abs(aVelocity) < TOLERANCE;
        boolean isAtTarget = Math.abs(progressDegree - currentProgressDegree) < TOLERANCE;
        return standingStill && isAtTarget;
    }

    private Runnable animator = new Runnable(){
        public void run(){
            now = AnimationUtils.currentAnimationTimeMillis();
            update(now);
            if(!isAtRest())
                postDelayed(this,15);
            updateBgPaint();
            updateProgressPaint();
        }
    };
}
