package com.example.theychat.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;

import com.example.theychat.util.Utils;

public class RoundDrawable extends BitmapDrawable {
    private Paint paint = new Paint();
    private int roundRadius;

    public RoundDrawable(Context ctx, Bitmap bitmap) {
        super(ctx.getResources(), bitmap);

        BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
        paint.setShader(shader);
        roundRadius = Utils.dip2px(ctx, 8);
    }

    @Override
    public void draw(Canvas canvas) {
        RectF rect = new RectF(0, 0, getBitmap().getWidth(), getBitmap().getHeight());
        canvas.drawRoundRect(rect, roundRadius, roundRadius, paint);
    }
}
