package com.lndisplay.privacydisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class MiniPrivacyPreview extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int level = 70;

    public MiniPrivacyPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLevel(int level) {
        this.level = level;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        float coverage = (level / 100f) * 0.85f;
        int dark = Color.argb(240, 0, 0, 0);
        int transparent = Color.TRANSPARENT;

        // Left
        paint.setShader(new LinearGradient(0, 0, w * coverage, 0, dark, transparent, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w * coverage, h, paint);
        // Right
        paint.setShader(new LinearGradient(w, 0, w * (1 - coverage), 0, dark, transparent, Shader.TileMode.CLAMP));
        canvas.drawRect(w * (1 - coverage), 0, w, h, paint);
        // Top
        paint.setShader(new LinearGradient(0, 0, 0, h * coverage, dark, transparent, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h * coverage, paint);
        // Bottom
        paint.setShader(new LinearGradient(0, h, 0, h * (1 - coverage), dark, transparent, Shader.TileMode.CLAMP));
        canvas.drawRect(0, h * (1 - coverage), w, h, paint);
    }
}
