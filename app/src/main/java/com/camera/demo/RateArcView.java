package com.camera.demo;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Create by wz
 * Time   2019年5月14日11:40:56
 * Name   显示时间的圆弧
 */
public class RateArcView extends View {

    private static final int TOTAL_TIME = 15000;//毫秒
    /**
     * 圆弧的宽度
     */
    private float borderWidth = dipToPx(10);
    /**
     * 时间
     */
    private String stepNumber = "0";
    /**
     * 开始绘制圆弧的角度
     */
    private float startAngle = 90;
    /**
     * 终点对应的角度和起始点对应的角度的夹角
     */
    private float angleLength = 360;
    /**
     * 所要绘制的当前时间的红色圆弧终点到起点的夹角
     */
    private float currentAngleLength = 0;
    /**
     * 动画时长
     */
    private int animationLength = TOTAL_TIME;
    private TimeCount mTime;
    private boolean isClick = true;
    private ValueAnimator progressAnimator;

    public RateArcView(Context context) {
        super(context);
    }

    public RateArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RateArcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**中心点的x坐标*/
        float centerX = (getWidth()) / 2;
        /**指定圆弧的外轮廓矩形区域*/
        RectF rectF = new RectF(0 + borderWidth, borderWidth, 2 * centerX - borderWidth, 2 * centerX - borderWidth);

        /**【第一步】绘制整体的圆弧*/
        drawArcYellow(canvas, rectF);
        /**【第二步】绘制当前进度的红色圆弧*/
        drawArcRed(canvas, rectF);

    }

    /**
     * 1.绘制总时间的黄色圆弧
     *
     * @param canvas 画笔
     * @param rectF  参考的矩形
     */
    private void drawArcYellow(Canvas canvas, RectF rectF) {
        Paint paint = new Paint();
        /** 默认画笔颜色，黄色 */
        paint.setColor(Color.parseColor("#e0f9ff"));
        /** 结合处为圆弧*/
        paint.setStrokeJoin(Paint.Join.ROUND);
        /** 设置画笔的样式 Paint.Cap.Round ,Cap.SQUARE等分别为圆形、方形*/
        paint.setStrokeCap(Paint.Cap.ROUND);
        /** 设置画笔的填充样式 Paint.Style.FILL  :填充内部;Paint.Style.FILL_AND_STROKE  ：填充内部和描边;  Paint.Style.STROKE  ：仅描边*/
        paint.setStyle(Paint.Style.STROKE);
        /**抗锯齿功能*/
        paint.setAntiAlias(true);
        /**设置画笔宽度*/
        paint.setStrokeWidth(borderWidth);

        /**绘制圆弧的方法
         * drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint)//画弧，
         参数一是RectF对象，一个矩形区域椭圆形的界限用于定义在形状、大小、电弧，
         参数二是起始角(度)在电弧的开始，圆弧起始角度，单位为度。
         参数三圆弧扫过的角度，顺时针方向，单位为度,从右中间开始为零度。
         参数四是如果这是true(真)的话,在绘制圆弧时将圆心包括在内，通常用来绘制扇形；如果它是false(假)这将是一个弧线,
         参数五是Paint对象；
         */
        canvas.drawArc(rectF, startAngle, angleLength, false, paint);

    }

    /**
     * 2.绘制当前时间的红色圆弧
     */
    private void drawArcRed(Canvas canvas, RectF rectF) {
        Paint paintCurrent = new Paint();
        paintCurrent.setStrokeJoin(Paint.Join.ROUND);
        paintCurrent.setStrokeCap(Paint.Cap.ROUND);//圆角弧度
        paintCurrent.setStyle(Paint.Style.STROKE);//设置填充样式
        paintCurrent.setAntiAlias(true);//抗锯齿功能
        paintCurrent.setStrokeWidth(borderWidth);//设置画笔宽度
        paintCurrent.setColor(getResources().getColor(R.color.red_light));//设置画笔颜色
        canvas.drawArc(rectF, startAngle, currentAngleLength, false, paintCurrent);
    }


    /**
     * dip 转换成px
     *
     * @param dip
     * @return
     */

    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 所走的时间进度
     *
     * @param totalTimeNum 设置的时间
     * @param currentTime  所走时间
     */
    public void setCurrentCount(int totalTimeNum, int currentTime) {
        /**如果当前走的时间超过总时间则圆弧还是270度，不能成为园*/
        if (currentTime > totalTimeNum) {
            currentTime = totalTimeNum;
        }

        /**上次所走时间占用总共时间的百分比*/
        float scalePrevious = (float) Integer.valueOf(stepNumber) / totalTimeNum;
        /**换算成弧度最后要到达的角度的长度-->弧长*/
        float previousAngleLength = scalePrevious * angleLength;

        /**所走时间占用总共时间的百分比*/
        float scale = (float) currentTime / totalTimeNum;
        /**换算成弧度最后要到达的角度的长度-->弧长*/
        float currentAngleLength = scale * angleLength;
        /**开始执行动画*/
        setAnimation(previousAngleLength, currentAngleLength, animationLength);

        stepNumber = String.valueOf(currentTime);
    }

    /**
     * 为进度设置动画
     * ValueAnimator是整个属性动画机制当中最核心的一个类，属性动画的运行机制是通过不断地对值进行操作来实现的，
     * 而初始值和结束值之间的动画过渡就是由ValueAnimator这个类来负责计算的。
     * 它的内部使用一种时间循环的机制来计算值与值之间的动画过渡，
     * 我们只需要将初始值和结束值提供给ValueAnimator，并且告诉它动画所需运行的时长，
     * 那么ValueAnimator就会自动帮我们完成从初始值平滑地过渡到结束值这样的效果。
     *
     * @param start   初始值
     * @param current 结束值
     * @param length  动画时长
     */
    private void setAnimation(float start, float current, int length) {
        progressAnimator = ValueAnimator.ofFloat(start, current);
        progressAnimator.setDuration(length);
        progressAnimator.setTarget(currentAngleLength);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                /**每次在初始值和结束值之间产生的一个平滑过渡的值，逐步去更新进度*/
                currentAngleLength = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        progressAnimator.start();
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            isClick = false;
            // Toast.makeText(getContext(), "长按事件", Toast.LENGTH_LONG).show();
            TimerStart();
            setCurrentCount(TOTAL_TIME, TOTAL_TIME);
            rateViewClickListener.onLongClick();
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN
                ) {
            isClick = true;
            mHandler.sendEmptyMessageDelayed(1, 500);

        } else if (event.getAction() == MotionEvent.ACTION_UP
                ) {
            //没有触发长按逻辑，进行点击事件
            if (isClick) {
                rateViewClickListener.onClick();
                // Toast.makeText(getContext(), "点击事件", Toast.LENGTH_LONG).show();
            } else {
                cancel();
                rateViewClickListener.onLongClickEnd();

                //  Toast.makeText(getContext(), "长按事件结束", Toast.LENGTH_LONG).show();
            }
            mHandler.removeMessages(1);
        }

        return true;
    }


    public void TimerStart() {
        mTime = new TimeCount();
        mTime.start();
    }

    /**
     * 定义一个倒计时的内部类
     */
    class TimeCount extends CountDownTimer {

        public TimeCount() {
            super(TOTAL_TIME + 500, 500);
        }


        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {//计时完毕时触发
            cancel();
            rateViewClickListener.onTimeOver();
        }
    }

    public void cancel() {
        if (mTime != null) {
            mTime.cancel();
        }

        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
        stepNumber = "0";
        currentAngleLength = 0;
        invalidate();
    }


    private RateViewClickListener rateViewClickListener;

    public void setRateViewClickListener(RateViewClickListener rateViewClickListener) {
        this.rateViewClickListener = rateViewClickListener;
    }

    interface RateViewClickListener {
        void onClick();

        void onLongClick();

        void onLongClickEnd();

        void onTimeOver();
    }

}

