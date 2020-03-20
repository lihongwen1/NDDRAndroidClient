package ddr.example.com.nddrandroidclient.other;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


public class SlideButton extends View {


    //状态改变监听
    public interface SlideButtonOnCheckedListener {
        void onCheckedChangeListener(boolean isChecked);
    }

    private SlideButtonOnCheckedListener mListener;

    //view默认的高,view默认的宽是高的两倍(单位:dp)
    public static final int VIEW_HEIGHT = 20;
    //椭圆的边框宽度
    private static final int strokeLineWidth = 3;
    //圆的边框宽度
    private static final int circleStrokeWidth = 3;

    //椭圆边框颜色
    private String StrokeLineColor = "#bebfc1";
    //椭圆填充颜色
    private String StrokeSolidColor = "#00ffffff";
    //圆形边框颜色
    private String CircleStrokeColor = "#abacaf";
    //圆形checked填充颜色
    private String CircleCheckedColor = "#ff5555";
    //圆形非checked填充颜色
    private String CircleNoCheckedColor = "#bebfc1";

    //控件内边距
    private static int PADDING = 20;
    //移动的判定距离
    private static int MOVE_DISTANCE = 50;

    //圆的x轴圆心
    private float circle_x;

    //是否是大圆
    private boolean isBigCircle = false;

    //圆角矩形的高
    private int strokeHeight;
    //圆角矩形的半径
    private float strokeCircleRadius;
    //内部圆的半径
    private float circleRadius;
    private Scroller mScroller;
    //当前按钮的开关状态
    public boolean isChecked = false;

    private int mWidth;
    private int mHeight;

    private Paint mPaint;
    private float circleStartX;
    private float circleEndX;
    private int centerX;
    private int centerY;
    private float preX = 0;
    private boolean isMove;
    private int view_height_int;
    private int strokeLineColor_int;
    private int strokeCheckedSolidColor_int;
    private int strokeNoCheckedSolidColor_int;
    private int circleStrokeColor_int;
    private int circleChecked_int;
    private int circleNoCheckedColor_int;

    public SlideButton(Context context) {
        super(context);
        init(context);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * * 设置小圆模式
     *
     * @param strokeLineColor      圆角矩形的边颜色
     * @param strokeSolidColor     圆角矩形的填充颜色
     * @param circleCheckedColor   内部小圆被选中的颜色
     * @param circleNoCheckedColor 内部小圆未被选中的颜色
     */
    public void setSmallCircleModel(int strokeLineColor, int strokeSolidColor, int strokeCheckedSolidColor, int circleCheckedColor, int circleNoCheckedColor) {
        isBigCircle = false;
        strokeLineColor_int = strokeLineColor;
        strokeNoCheckedSolidColor_int = strokeSolidColor;
        strokeCheckedSolidColor_int = strokeCheckedSolidColor;
        circleChecked_int = circleCheckedColor;
        circleNoCheckedColor_int = circleNoCheckedColor;
        invalidate();
    }

    /**
     * 设置大圆模式
     *
     * @param strokeLineColor           圆角矩形边线颜色
     * @param strokeCheckedSolidColor   圆角矩形选择状态下的填充颜色
     * @param strokeNoCheckedSolidColor 圆角矩形非选择状态下填充颜色
     * @param circleChecked             滑动圆选择状态下的填充颜色
     * @param circleNoCheckColor        滑动圆非选中状态下的填充颜色
     */
    public void setBigCircleModel(int strokeLineColor, int strokeCheckedSolidColor,
                                  int strokeNoCheckedSolidColor, int circleChecked,
                                  int circleNoCheckColor) {
        Logger.e("------先");
        isBigCircle = true;
        strokeLineColor_int = strokeLineColor;
        strokeCheckedSolidColor_int = strokeCheckedSolidColor;
        strokeNoCheckedSolidColor_int = strokeNoCheckedSolidColor;
        circleChecked_int = circleChecked;
        circleNoCheckedColor_int = circleNoCheckColor;
        invalidate();
    }

    /**
     * 设置点击监听
     *
     * @param listener
     */
    public void setOnCheckedListener(SlideButtonOnCheckedListener listener) {
        this.mListener = listener;
    }

    /**
     * 设置按钮状态
     *
     * @param checked
     */
    public void setChecked(boolean checked) {
        this.isChecked = checked;
        if (isChecked) {
            circle_x = circleEndX;
        } else {
            circle_x = circleStartX;
        }
        invalidate();
    }

    private void init(Context context) {
        setEnabled(true);
        setClickable(true);
        mPaint = new Paint();
        mScroller = new Scroller(context);
        view_height_int = dip2px(context, VIEW_HEIGHT);
        if (isChecked){
            strokeLineColor_int = Color.parseColor("#49c265");
        }else {
            strokeLineColor_int = Color.parseColor(StrokeLineColor);
        }

        strokeNoCheckedSolidColor_int = Color.parseColor(StrokeSolidColor);
        circleStrokeColor_int = Color.parseColor(CircleStrokeColor);
        circleChecked_int = Color.parseColor(CircleCheckedColor);
        circleNoCheckedColor_int = Color.parseColor(CircleNoCheckedColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST) {
            //如果是wrap_content
            heightSize = view_height_int;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = heightSize * 2;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Logger.e("--------后");
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        Logger.e(mWidth+"---"+mHeight);
        if (isBigCircle) {
            PADDING = h / 10;//6.6
        } else {
            PADDING = h / 15;
        }
        MOVE_DISTANCE = mWidth / 100;
        //圆角椭圆的高
        strokeHeight = h - PADDING * 2;//53
        //外部圆角矩形的半径
        strokeCircleRadius = strokeHeight / 2;//26
        centerY = mHeight / 2;
        //内部圆的半径
        if (isBigCircle) {
            circleRadius = strokeCircleRadius + PADDING;//32
            Logger.e(circleRadius+"-------");
        } else {
            circleRadius = strokeCircleRadius - PADDING;
            Logger.e(circleRadius+"-------");
        }
        Log.i("TAG", "mHeight:" + mHeight + "   strokeCircleRadius: " + strokeCircleRadius);
        //内部圆的x轴起始坐标
        circleStartX = PADDING + strokeCircleRadius;
        //内部圆的x轴终点坐标
        circleEndX = mWidth - circleStartX;
        if (isChecked) {
            circle_x = circleEndX;
        } else {
            circle_x = circleStartX;
        }

        //控件的中线
        centerX = mWidth / 2;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRect(canvas);
        drawCircle(canvas);
    }

    //画圆角矩形
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawRect(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        if (isChecked) {
            mPaint.setColor(strokeCheckedSolidColor_int);
        } else {
            mPaint.setColor(strokeNoCheckedSolidColor_int);
        }
        //画填充
        canvas.drawRoundRect(PADDING, PADDING, mWidth - PADDING, mHeight - PADDING, strokeCircleRadius, strokeCircleRadius, mPaint);
        //画边框
        mPaint.setStrokeWidth(strokeLineWidth);
        mPaint.setColor(strokeLineColor_int);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(PADDING, PADDING, mWidth - PADDING, mHeight - PADDING, strokeCircleRadius, strokeCircleRadius, mPaint);
    }

    //画里面的圆
    private void drawCircle(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        float circleRadiusNew = circleRadius;
        if (isBigCircle) {
            circleRadiusNew -= circleStrokeWidth;
        }
        if (isChecked) {
            mPaint.setColor(circleChecked_int);
        } else {
            mPaint.setColor(circleNoCheckedColor_int);
        }


        if (isBigCircle) {
            Logger.e("---画大圆的边");
            //画圆的边
            mPaint.setColor(circleStrokeColor_int);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(circleStrokeWidth);
            canvas.drawCircle(circle_x, centerY, circleRadiusNew, mPaint);
        }
        canvas.drawCircle(circle_x, centerY, circleRadiusNew, mPaint);
        Paint tpaint=new Paint();
        tpaint.setColor(Color.parseColor("#49c265"));
        tpaint.setStyle(Paint.Style.FILL_AND_STROKE);
        tpaint.setStrokeWidth(1);
        tpaint.setTextSize(28);
        if (isChecked){
            canvas.drawText("开",circle_x-14, centerY+10,tpaint);
        }else {
            tpaint.setColor(Color.parseColor("#999999"));
            canvas.drawText("关",circle_x-14, centerY+10,tpaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preX = event.getX();
                isMove = false;
                if (!isChecked) {
                    circle_x = PADDING + strokeCircleRadius;
                } else {
                    circle_x = mWidth - PADDING - strokeCircleRadius;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float move_x = event.getX();
                if (Math.abs(move_x - preX) > MOVE_DISTANCE) {
                    isMove = true;
                    if (move_x < circleStartX) {
                        circle_x = circleStartX;
                        isChecked = false;
                    } else if (move_x > circleEndX) {
                        circle_x = circleEndX;
                        isChecked = true;
                    } else {
                        circle_x = move_x;
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isMove) {
                    if (circle_x >= centerX) {
                        //关闭(执行开启)
                        mScroller.startScroll((int) circle_x, 0, (int) (circleEndX - circle_x), 0);
                        isChecked = true;
                    } else {
                        //开启（执行关闭）
                        mScroller.startScroll((int) circle_x, 0, (int) (circleStartX - circle_x), 0);
                        isChecked = false;
                    }
                } else {
                    if (!isChecked) {
                        //关闭(执行开启)
                        mScroller.startScroll((int) circle_x, 0, (int) (circleEndX - circle_x), 0);
                        isChecked = true;
                    } else {
                        //开启（执行关闭）
                        mScroller.startScroll((int) circle_x, 0, (int) (circleStartX - circle_x), 0);
                        isChecked = false;
                    }
                }
                if (mListener != null) {
                    mListener.onCheckedChangeListener(isChecked);
                }
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            circle_x = mScroller.getCurrX();
            invalidate();
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

