package com.yanleiz.waitbus.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yanleiz.waitbus.waitbus.R;


public class DrawView extends View {

    private Paint sPaint;
    private Paint sPaint2;
    private Paint fPaint;
    private Paint redPaint;
    private Paint blackPaint;

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        sPaint = new Paint();
        sPaint2 = new Paint();
        fPaint = new Paint();
        redPaint = new Paint();
        blackPaint = new Paint();

        sPaint.setAntiAlias(true);          //抗锯齿
        sPaint2.setAntiAlias(true);          //抗锯齿
        fPaint.setAntiAlias(true);          //抗锯齿
        redPaint.setAntiAlias(true);          //抗锯齿

        sPaint.setColor(getResources().getColor(R.color.station));//画笔颜色
        sPaint2.setColor(getResources().getColor(R.color.station));//画笔颜色
        fPaint.setColor(getResources().getColor(R.color.station));//画笔颜色
        redPaint.setColor(Color.RED);//画笔颜色
        blackPaint.setColor(Color.BLACK);//画笔颜色

        sPaint.setStyle(Paint.Style.STROKE);  //画笔风格STROKE
        sPaint2.setStyle(Paint.Style.STROKE);  //画笔风格STROKE
        fPaint.setStyle(Paint.Style.FILL);  //画笔风格
        redPaint.setStyle(Paint.Style.FILL);  //画笔风格

        sPaint.setTextSize(36);             //绘制文字大小，单位px
        sPaint2.setTextSize(36);             //绘制文字大小，单位px
        fPaint.setTextSize(40);             //绘制文字大小，单位px
        redPaint.setTextSize(40);             //绘制文字大小，单位px
        blackPaint.setTextSize(45);             //绘制文字大小，单位px

        sPaint.setStrokeWidth(8);           //画笔粗细
        sPaint2.setStrokeWidth(2);           //画笔粗细
        fPaint.setStrokeWidth(4);           //画笔粗细
        redPaint.setStrokeWidth(6);           //画笔粗细
        blackPaint.setStrokeWidth(6);           //画笔粗细


    }

    //重写该方法，在这里绘图
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //公交车位置图标
        Bitmap busBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bus);

        Matrix matrix = new Matrix();
        matrix.postScale((float) 64.0 / (float) busBitmap.getWidth(), (float) 64.0 / (float) busBitmap.getHeight());
        busBitmap = Bitmap.createBitmap(busBitmap, 0, 0, busBitmap.getWidth(), busBitmap.getHeight(), matrix, true);
        //当前位置图标
        Bitmap positionBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.currposition);

        Matrix matrix1 = new Matrix();

        matrix1.postScale((float) 74.0 / (float) positionBitmap.getWidth(), (float) 74.0 / (float) positionBitmap.getHeight());
        positionBitmap = Bitmap.createBitmap(positionBitmap, 0, 0, positionBitmap.getWidth(), positionBitmap.getHeight(), matrix1, true);

        int center = getWidth() / 10;
        //圆环半径
        int radius = getWidth() / 36;
        int texty = radius * 2 - 10;
        if (Utils.nearStation.size() > 0) {
            for (int i = 0; i < Utils.busAbstracts.size(); i++) {
                canvas.drawText(Utils.busAbstracts.get(i).toString(), 0, texty, blackPaint);
                texty += radius * 2 - 10;
            }
        }else{
            canvas.drawText(Utils.busAbstracts.get(0).toString(), 0, texty, blackPaint);
            texty += radius * 2 - 10;
            canvas.drawText("您附近没有本车公交站点", 0, texty, blackPaint);
            texty += radius * 2 - 10;
        }
        canvas.drawLine(0, texty, getWidth(), texty, blackPaint);

        //int innerCircle = dip2px(getContext(), 20); //设置内圆半径
        //int ringWidth = dip2px(getContext(), 5); //设置圆环宽度

        //绘制内圆
        //mPaint.setARGB(155, 167, 190, 206);
        //mPaint.setStrokeWidth(2);
        //canvas.drawCircle(center,center, innerCircle, mPaint);

        //绘制圆环
        //mPaint.setARGB(255, 212 ,225, 233);
        // mPaint.setStrokeWidth(ringWidth);
        int center1 = center;
        for (int i = 1; i <= Utils.busStations.size(); i++) {
//        canvas.drawCircle(0,0, 30, sPaint);
//        canvas.drawCircle(0,0, 20, sPaint2);
//        canvas.drawCircle(0,0, 10, fPaint);
            if (i % 2 == 1) {
                center1 += center;
                if (Utils.busLocation.get(i - 1).toString().trim() != "") {
                    canvas.drawCircle(center, center1, radius, sPaint);
                    canvas.drawCircle(center, center1, radius - 8, redPaint);
                    canvas.drawBitmap(busBitmap, 10, center1 - 30, null);
                    if (Utils.nearStation.size() > 0) {
                        if (Utils.nearStation.get(0).replace("(公交站)", "").equals(Utils.busStations.get(i - 1).toString())) {
                            canvas.drawBitmap(positionBitmap, center + radius + 10, center1 - 30, null);
                            Utils.aboardStation = (i + 1) / 2;
                        }

                    }
                    canvas.drawLine(center + radius, center1 + 27, center * 4, center1 + radius - 3, sPaint2);
                    canvas.drawText(Utils.busStations.get(i - 1).toString(), center * 4 + 4, center1 + radius - 5, fPaint);
                } else {
                    canvas.drawCircle(center, center1, radius, sPaint);
                    canvas.drawCircle(center, center1, radius - 8, fPaint);
                    canvas.drawLine(center + radius + 4, center1 + radius - 3, center * 4, center1 + radius - 3, sPaint2);
                    canvas.drawText(Utils.busStations.get(i - 1).toString(), center * 4 + 4, center1 + radius - 5, fPaint);
                    if (Utils.nearStation.size() > 0) {
                        if (Utils.nearStation.get(0).replace("(公交站)", "").equals(Utils.busStations.get(i - 1).toString())) {
                            canvas.drawBitmap(positionBitmap, center + radius + 10, center1 - 30, null);
                            Utils.aboardStation = (i + 1) / 2;
                        }

                    }
                }


                //mPaint.setARGB(100, 0, 0, 0);
                //mPaint.setStrokeWidth(2);
                //canvas.drawCircle(center,center, innerCircle+ringWidth, mPaint);
            } else {
                if (Utils.busLocation.get(i - 1).toString().trim() != "") {
                    canvas.drawLine(center, center1 + radius + 4, center, center1 + center - radius - 4, redPaint);
                    canvas.drawBitmap(busBitmap, 10, center1 + center / 2 - radius, null);

                } else {
                    canvas.drawLine(center, center1 + radius, center, center1 + center - radius, sPaint2);
                }
            }

        }
        postInvalidateDelayed(500);//300毫秒刷新
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int len = getMeasuredWidth() / 10;
        setMeasuredDimension(getMeasuredWidth(), len * Utils.busStations.size() / 2 + 3 * len);

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
}