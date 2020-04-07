package com.example.testrit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class SpeedometerDrawlerView extends View {

    int colorMain;
    int colorArrow;
    int colorShadowText;
    int colorShadowCircle;
    int textColor;
    int maxSpeed;
    int textSize;
    int line_size;

    float currentSpeed;
    private Paint mPaint;
    private Paint textPaint;
    private Paint arrowPaint;
    private Rect mTextBoundRect;
    private Path path;
    public SpeedometerDrawlerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SpeedometerDrawlerView);
        colorMain = typedArray.getColor(R.styleable.SpeedometerDrawlerView_spd_main_color, Color.BLUE);
        colorArrow = typedArray.getColor(R.styleable.SpeedometerDrawlerView_spd_arrow_color, Color.RED);
        textColor = typedArray.getColor(R.styleable.SpeedometerDrawlerView_spd_text_color, Color.GREEN);
        colorShadowText = typedArray.getColor(R.styleable.SpeedometerDrawlerView_spd_text_shadow_color, Color.BLUE);
        colorShadowCircle = typedArray.getColor(R.styleable.SpeedometerDrawlerView_spd_main_shadow_color, Color.GREEN);
        maxSpeed = typedArray.getColor(R.styleable.SpeedometerDrawlerView_spd_max_speed, 200);
        textSize = typedArray.getInt(R.styleable.SpeedometerDrawlerView_spd_text_size, 35);
        line_size = typedArray.getInt(R.styleable.SpeedometerDrawlerView_spd_line_size, 10);
        currentSpeed = 0;
        init();
        typedArray.recycle();
    }
    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(colorMain);
        mPaint.setStrokeWidth(line_size);

        arrowPaint = new Paint();
        arrowPaint.setColor(colorArrow);
        arrowPaint.setStyle(Paint.Style.FILL);
        arrowPaint.setStrokeWidth(line_size);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setStrokeWidth(2.0f);
        textPaint.setStyle(Paint.Style.FILL);

        path = new Path();
        mTextBoundRect = new Rect();
    }
    public void setSpeed(float speed){
        this.currentSpeed = speed;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        //mPaint.setAntiAlias(true);
        float width, height, centerX, centerY, radius,xArrow,yArrow,angleArrow,startAngle,sweepAngle;
        if(getWidth()>getHeight()){
            width = getWidth()-10;
            height = getWidth()-10;
        }else{
            width = getHeight()-10;
            height = getHeight()-10;
        }

        centerX = (width+10) / 2;
        centerY = (height+10) / 2;
        radius = width-centerX;
        startAngle = 150;
        sweepAngle = 240;



        canvas.drawArc(10,10,width,height, startAngle, sweepAngle, false, mPaint);
        int countDel = maxSpeed/20;
        float angleDel = sweepAngle/countDel;
        for (int i=0;i<countDel+1;i++){
            float angleDelNow = startAngle+angleDel*i;
            float x0 = (float) (centerX+(radius*Math.cos(Math.toRadians(angleDelNow))));
            float y0 = (float) (centerY+(radius*Math.sin(Math.toRadians(angleDelNow))));
            float x1 = (float) (centerX+((radius*0.8)*Math.cos(Math.toRadians(angleDelNow))));
            float y1 = (float) (centerY+((radius*0.8)*Math.sin(Math.toRadians(angleDelNow))));
            canvas.drawLine(x0, y0, x1, y1, mPaint);
            String textInt = String.valueOf(20*i);
            textPaint.getTextBounds(textInt, 0, textInt.length(), mTextBoundRect);
            float mTextWidth = mTextBoundRect.width();//.measureText(textInt);
            float mTextHeight = mTextBoundRect.height();

            x0 = (float) (centerX-(mTextWidth/1.5)+((radius*0.65)*Math.cos(Math.toRadians(angleDelNow))));
            y0 = (float) (centerY+(mTextHeight/3)+((radius*0.65)*Math.sin(Math.toRadians(angleDelNow))));
            canvas.drawText(textInt,
                    x0,
                    y0,
                   textPaint
            );
            if (i!=countDel){
                angleDelNow = (float) (startAngle+(angleDel*0.5)+angleDel*i);
                x0 = (float) (centerX+(radius*Math.cos(Math.toRadians(angleDelNow))));
                y0 = (float) (centerY+(radius*Math.sin(Math.toRadians(angleDelNow))));
                x1 = (float) (centerX+((radius*0.9)*Math.cos(Math.toRadians(angleDelNow))));
                y1 = (float) (centerY+((radius*0.9)*Math.sin(Math.toRadians(angleDelNow))));
                canvas.drawLine(x0, y0, x1, y1, mPaint);
            }
        }
        angleArrow = startAngle+(sweepAngle*(currentSpeed/maxSpeed));
        xArrow = (float) (centerX+(radius*Math.cos(Math.toRadians(angleArrow))));
        yArrow = (float) (centerY+(radius*Math.sin(Math.toRadians(angleArrow))));


        path.reset();
        path.moveTo( xArrow, yArrow);
        path.lineTo(centerX-10, yArrow);
        path.addArc(centerX-10, centerY-10, centerX+10, centerY+10, 90+angleArrow, 180);
        path.lineTo(xArrow,yArrow);
        path.close();


        canvas.drawPath(path,arrowPaint);


    }
}
