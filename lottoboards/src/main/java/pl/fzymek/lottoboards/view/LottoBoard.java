package pl.fzymek.lottoboards.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by filip on 19.09.2016.
 */
public class LottoBoard extends View {

    Paint borderPaint = new Paint();

    public LottoBoard(Context context) {
        super(context);
        init(context);
    }

    public LottoBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LottoBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LottoBoard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), borderPaint);
    }

    private void init(Context context) {
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.RED);
    }
}
