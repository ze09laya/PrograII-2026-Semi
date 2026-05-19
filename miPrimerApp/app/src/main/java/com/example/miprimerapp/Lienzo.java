package com.example.miprimerapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

public class Lienzo extends View {

    private Paint paint = new Paint();
    private Path path = new Path();

    public Lienzo(Context context) {
        super(context);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8f);
        paint.setAntiAlias(true);

        // hace las líneas suaves
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLACK);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                return true;

            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
        }

        invalidate();
        return true;
    }

    // LIMPIAR
    public void limpiar() {
        path.reset();
        invalidate();
    }

    // CAMBIAR COLOR
    public void cambiarColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    // CAMBIAR GROSOR
    public void cambiarGrosor(float grosor) {
        paint.setStrokeWidth(grosor);
        invalidate();
    }
}