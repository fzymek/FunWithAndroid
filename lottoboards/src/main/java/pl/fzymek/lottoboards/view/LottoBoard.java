package pl.fzymek.lottoboards.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import pl.fzymek.lottoboards.R;

/**
 * Created by filip on 19.09.2016.
 */
public class LottoBoard extends View {

    private final static int DEFAULT_BORDER_SIZE_PX = 8;
    private final static int DEFAULT_BORDER_COLOR = Color.RED;

    Paint borderPaint;
    int borderColor;
    int borderSize;

    public LottoBoard(Context context) {
        super(context);
        init(context, null);
    }

    public LottoBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LottoBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LottoBoard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.LottoBoard, 0, 0);

        try {
            borderSize = attributes.getDimensionPixelSize(R.styleable.LottoBoard_border_size, DEFAULT_BORDER_SIZE_PX);
            borderColor = attributes.getColor(R.styleable.LottoBoard_border_color, DEFAULT_BORDER_COLOR);
        } finally {
            attributes.recycle();
        }

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), borderPaint);
    }
}
