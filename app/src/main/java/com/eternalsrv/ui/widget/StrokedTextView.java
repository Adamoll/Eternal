package com.eternalsrv.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import androidx.core.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.eternalsrv.R;

public class StrokedTextView extends TextView {
    private static final int DEFAULT_STROKE_WIDTH = 0;

    private int _strokeColor;
    private float _strokeWidth;

    public StrokedTextView(Context context) {
        this(context, null, 0);
    }

    public StrokedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrokedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if(attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StrokedTextAttrs);
            _strokeColor = a.getColor(R.styleable.StrokedTextAttrs_textStrokeColor,
                    getCurrentTextColor());
            _strokeWidth = a.getFloat(R.styleable.StrokedTextAttrs_textStrokeWidth,
                    DEFAULT_STROKE_WIDTH);

            a.recycle();
        }
        else {
            _strokeColor = getCurrentTextColor();
            _strokeWidth = DEFAULT_STROKE_WIDTH;
        }
        //convert values specified in dp in XML layout to
        //px, otherwise stroke width would appear different
        //on different screens
        _strokeWidth = dpToPx(context, _strokeWidth);
    }

    @Override
    public void onDraw(Canvas canvas) {
        String mText = this.getText().toString();
        Paint p = getPaint();
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.hipster_script_pro);
        p.setStrokeWidth(10);
        p.setTypeface(typeface);
        p.setAntiAlias(true);
        p.setTextSize(getTextSize());
        p.setTextAlign(Paint.Align.CENTER);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeJoin(Paint.Join.ROUND);
        p.setColor(0xffffffff);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((p.descent() +
                p.ascent()) / 2));
        canvas.drawText(mText, xPos, yPos, p);
        p.setStyle(Paint.Style.FILL);
        p.setColor(0xff333333);

        canvas.drawText(mText, xPos, yPos, p);
    }

    public static int dpToPx(Context context, float dp)
    {
        final float scale= context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}