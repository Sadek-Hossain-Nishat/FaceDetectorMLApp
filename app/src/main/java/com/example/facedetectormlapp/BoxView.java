package com.example.facedetectormlapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;


public class BoxView extends View {

    Context context;
    private Paint paint;
    private RectF mRect;

    public BoxView(Context context) {
        super(context);
        this.context=context;
        paint=new Paint();
        mRect=new RectF();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cornerRadius = 10;

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        canvas.drawRoundRect(mRect,cornerRadius,cornerRadius,paint);

    }

    public void setRect(RectF rect) {
        this.mRect = rect;
        invalidate();
        requestLayout();
    }

}
