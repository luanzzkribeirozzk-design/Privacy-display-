package com.lndisplay.privacydisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnowView extends View {

    private static class Snowflake {
        float x, y, radius, speed, alpha, drift, driftSpeed;
    }

    private final List<Snowflake> snowflakes = new ArrayList<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean running = false;

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            update();
            invalidate();
            if (running) handler.postDelayed(this, 33);
        }
    };

    public SnowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(0xFFFFFFFF);
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    public SnowView(Context context) {
        this(context, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        snowflakes.clear();
        int count = 60;
        for (int i = 0; i < count; i++) {
            snowflakes.add(createFlake(w, h, true));
        }
    }

    private Snowflake createFlake(int w, int h, boolean randomY) {
        Snowflake s = new Snowflake();
        s.x = random.nextFloat() * w;
        s.y = randomY ? random.nextFloat() * h : -10f;
        s.radius = 1.5f + random.nextFloat() * 3f;
        s.speed = 0.8f + random.nextFloat() * 2f;
        s.alpha = 0.3f + random.nextFloat() * 0.7f;
        s.drift = 0;
        s.driftSpeed = (random.nextFloat() - 0.5f) * 0.3f;
        return s;
    }

    private void update() {
        int w = getWidth();
        int h = getHeight();
        for (int i = 0; i < snowflakes.size(); i++) {
            Snowflake s = snowflakes.get(i);
            s.y += s.speed;
            s.drift += s.driftSpeed;
            s.x += (float) Math.sin(s.drift) * 0.5f;
            if (s.y > h + 10) {
                snowflakes.set(i, createFlake(w, h, false));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Snowflake s : snowflakes) {
            paint.setAlpha((int)(s.alpha * 255));
            // Draw snowflake cross shape
            canvas.drawCircle(s.x, s.y, s.radius, paint);
            if (s.radius > 2.5f) {
                float arm = s.radius * 1.4f;
                Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                linePaint.setColor(0xFFFFFFFF);
                linePaint.setAlpha((int)(s.alpha * 200));
                linePaint.setStrokeWidth(0.8f);
                canvas.drawLine(s.x - arm, s.y, s.x + arm, s.y, linePaint);
                canvas.drawLine(s.x, s.y - arm, s.x, s.y + arm, linePaint);
                canvas.drawLine(s.x - arm * 0.7f, s.y - arm * 0.7f, s.x + arm * 0.7f, s.y + arm * 0.7f, linePaint);
                canvas.drawLine(s.x + arm * 0.7f, s.y - arm * 0.7f, s.x - arm * 0.7f, s.y + arm * 0.7f, linePaint);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        running = true;
        handler.post(tick);
    }

    @Override
    protected void onDetachedFromWindow() {
        running = false;
        handler.removeCallbacks(tick);
        super.onDetachedFromWindow();
    }
}
