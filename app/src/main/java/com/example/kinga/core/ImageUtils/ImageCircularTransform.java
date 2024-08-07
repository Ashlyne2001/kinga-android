package com.example.kinga.core.ImageUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.kinga.R;
import com.squareup.picasso.Transformation;


public class ImageCircularTransform implements Transformation {

    private boolean mCircleSeparator = false;
    private Context mContext;
    private int mStrokeWidth;
    private int mAntiRadius = 3;

    public ImageCircularTransform(Context context) {
        mContext = context;
        mStrokeWidth = 5;

    }

    public ImageCircularTransform(Context context, int stroke_width) {
        mContext = context;
        mStrokeWidth = stroke_width;
    }

    public ImageCircularTransform(boolean circleSeparator) { mCircleSeparator = circleSeparator;
    }



    @Override
    public Bitmap transform(Bitmap source) {

        return transformBitmap(source);
    }

    private Bitmap transformBitmap(Bitmap source){



        try{

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            // Sometimes when the back button is pressed and this process is ongoing,
            // a "NullPointerException" might be raised.

            // (java.lang.NullPointerException: Attempt to invoke virtual method
            // 'int android.content.Context.getColor(int)' on a null object reference)


            Canvas canvas = new Canvas(bitmap);
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
            paint.setShader(shader);
            float r = size / 2f;
            canvas.drawCircle(r, r, r - 3, paint);
            // Make the thin border:
            Paint paintBorder = new Paint();
            paintBorder.setStyle(Paint.Style.STROKE);
            paintBorder.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            paintBorder.setAntiAlias(true);
            paintBorder.setStrokeWidth(mStrokeWidth);
            canvas.drawCircle(r, r, r-mAntiRadius, paintBorder);

            // Optional separator for stacking:
            if (mCircleSeparator) {
                Paint paintBorderSeparator = new Paint();
                paintBorderSeparator.setStyle(Paint.Style.STROKE);
                paintBorderSeparator.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                paintBorderSeparator.setAntiAlias(true);
                paintBorderSeparator.setStrokeWidth(4);
                canvas.drawCircle(r, r, r+1, paintBorderSeparator);
            }
            squaredBitmap.recycle();
            return bitmap;

        }catch (Exception e){
            // Do nothing
        }

        return source;

    }


    @Override
    public String key() {
        return "circle";
    }
}
