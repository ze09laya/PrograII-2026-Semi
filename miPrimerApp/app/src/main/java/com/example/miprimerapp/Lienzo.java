package com.example.miprimerapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.graphics.Bitmap;

public class Lienzo extends View {

    private Paint paint;
    private Path path;


    private boolean dibujoRealizado = false;

    private Bitmap dibujoBase;
    private float escala = 1f;


    private float offsetX = 0;
    private float offsetY = 0;

    private float lastX;
    private float lastY;

    private boolean moviendo = false;


    public void cargarImagen(Bitmap bitmap) {

        dibujoBase = bitmap;

        dibujoRealizado = true;

        path = new Path();

        invalidate();
    }
    private ScaleGestureDetector detectorZoom;

    public Lienzo(Context context) {

        super(context);

        paint = new Paint();

        paint.setColor(Color.WHITE);

        paint.setStyle(Paint.Style.STROKE);

        paint.setStrokeWidth(8f);

        paint.setAntiAlias(true);

        paint.setStrokeCap(Paint.Cap.ROUND);

        paint.setStrokeJoin(Paint.Join.ROUND);

        path = new Path();

        detectorZoom =
                new ScaleGestureDetector(
                        context,
                        new ScaleGestureDetector.SimpleOnScaleGestureListener() {

                            @Override
                            public boolean onScale(ScaleGestureDetector detector) {

                                float factor =
                                        detector.getScaleFactor();

                                float focusX =
                                        detector.getFocusX();

                                float focusY =
                                        detector.getFocusY();

                                offsetX =
                                        focusX -
                                                ((focusX - offsetX) * factor);

                                offsetY =
                                        focusY -
                                                ((focusY - offsetY) * factor);

                                escala *= factor;

                                escala =
                                        Math.max(
                                                0.5f,
                                                Math.min(
                                                        escala,
                                                        5f
                                                )
                                        );

                                invalidate();

                                return true;
                            }
                        }
                );
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);


        canvas.drawColor(Color.BLACK);

        canvas.save();


        canvas.translate(offsetX, offsetY);


        canvas.scale(escala, escala);


        if (dibujoBase != null) {

            canvas.drawBitmap(
                    dibujoBase,
                    0,
                    0,
                    null
            );
        }

        canvas.drawPath(path, paint);

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        detectorZoom.onTouchEvent(event);


        if (event.getPointerCount() == 2) {

            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_POINTER_DOWN:

                    lastX =
                            (event.getX(0) + event.getX(1)) / 2f;

                    lastY =
                            (event.getY(0) + event.getY(1)) / 2f;

                    moviendo = true;

                    break;

                case MotionEvent.ACTION_MOVE:

                    if (moviendo) {

                        float x =
                                (event.getX(0) + event.getX(1)) / 2f;

                        float y =
                                (event.getY(0) + event.getY(1)) / 2f;

                        offsetX += x - lastX;
                        offsetY += y - lastY;

                        lastX = x;
                        lastY = y;

                        invalidate();
                    }

                    break;

                case MotionEvent.ACTION_POINTER_UP:

                    moviendo = false;

                    break;
            }

            return true;
        }



        float x =
                (event.getX() - offsetX) / escala;

        float y =
                (event.getY() - offsetY) / escala;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                dibujoRealizado = true;

                path.moveTo(x, y);

                invalidate();

                return true;

            case MotionEvent.ACTION_MOVE:

                path.lineTo(x, y);

                invalidate();

                return true;

            case MotionEvent.ACTION_UP:

                invalidate();

                return true;
        }

        return true;
    }



    public void limpiar() {

        path = new Path();

        dibujoBase = null;

        dibujoRealizado = false;

        escala = 1f;

        offsetX = 0;

        offsetY = 0;

        invalidate();
    }



    public void cambiarColor(int color) {

        paint.setColor(color);

        invalidate();
    }



    public void cambiarGrosor(float grosor) {

        paint.setStrokeWidth(grosor);

        invalidate();
    }



    public boolean hayDibujo() {

        return dibujoRealizado || dibujoBase != null;
    }


    public void resetZoom() {

        escala = 1f;

        offsetX = 0;

        offsetY = 0;

        invalidate();
    }



    public float getEscala() {

        return escala;
    }
}