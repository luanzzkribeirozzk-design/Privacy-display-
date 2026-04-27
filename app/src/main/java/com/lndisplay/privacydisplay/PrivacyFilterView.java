package com.lndisplay.privacydisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.View;

/**
 * Privacy filter view - renders dark gradients from all 4 edges toward center.
 * The higher the privacy level, the more the dark "vignette" covers the screen.
 * At maximum level, only a small center area is visible; side/top/bottom viewers see black.
 */
public class PrivacyFilterView extends View {

    private Paint edgePaint;
    private int privacyLevel = 70; // 0-100

    public PrivacyFilterView(Context context) {
        super(context);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        edgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setPrivacyLevel(int level) {
        this.privacyLevel = Math.max(0, Math.min(100, level));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (privacyLevel == 0) return;

        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        // How much of the screen the dark covers (0.0 = nothing, 1.0 = full black)
        // At level 100, 90% of screen from each edge is dark (privacy filter effect)
        float coverage = (privacyLevel / 100f) * 0.88f;

        int darkColor = Color.argb((int)(255 * 0.97f), 0, 0, 0);
        int transparent = Color.TRANSPARENT;

        // LEFT gradient
        LinearGradient leftGrad = new LinearGradient(
                0, 0, w * coverage, 0,
                darkColor, transparent,
                Shader.TileMode.CLAMP
        );
        edgePaint.setShader(leftGrad);
        canvas.drawRect(0, 0, w * coverage, h, edgePaint);

        // RIGHT gradient
        LinearGradient rightGrad = new LinearGradient(
                w, 0, w * (1f - coverage), 0,
                darkColor, transparent,
                Shader.TileMode.CLAMP
        );
        edgePaint.setShader(rightGrad);
        canvas.drawRect(w * (1f - coverage), 0, w, h, edgePaint);

        // TOP gradient
        LinearGradient topGrad = new LinearGradient(
                0, 0, 0, h * coverage,
                darkColor, transparent,
                Shader.TileMode.CLAMP
        );
        edgePaint.setShader(topGrad);
        canvas.drawRect(0, 0, w, h * coverage, edgePaint);

        // BOTTOM gradient
        LinearGradient botGrad = new LinearGradient(
                0, h, 0, h * (1f - coverage),
                darkColor, transparent,
                Shader.TileMode.CLAMP
        );
        edgePaint.setShader(botGrad);
        canvas.drawRect(0, h * (1f - coverage), w, h, edgePaint);
    }
}
