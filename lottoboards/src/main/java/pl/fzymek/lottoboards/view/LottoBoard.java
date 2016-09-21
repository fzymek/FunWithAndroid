package pl.fzymek.lottoboards.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import pl.fzymek.lottoboards.R;
import timber.log.Timber;

public class LottoBoard extends View {

    private final static int DEFAULT_BORDER_SIZE_DIP = 4;
    private final static int DEFAULT_DIVIDER_SIZE_DIP = 2;
    private final static int DEFAULT_BORDER_COLOR = Color.RED;
    private final static int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#FFC77F");
    private final static int DEFAULT_FIELD_COUNT = 2;

    Paint borderPaint;
    int borderColor;
    int borderSize;

    Paint backgroundPaint;
    int backgroundColor;

    int horizontalFieldCount;
    int verticalFieldCount;

    int minDividerSize;

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
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            borderSize = attributes.getDimensionPixelSize(R.styleable.LottoBoard_border_size,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_BORDER_SIZE_DIP, metrics));
            minDividerSize = attributes.getDimensionPixelSize(R.styleable.LottoBoard_min_divider_size,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_DIVIDER_SIZE_DIP, metrics));

            backgroundColor = attributes.getColor(R.styleable.LottoBoard_background_color, DEFAULT_BACKGROUND_COLOR);
            borderColor = attributes.getColor(R.styleable.LottoBoard_border_color, DEFAULT_BORDER_COLOR);

            horizontalFieldCount = attributes.getInteger(R.styleable.LottoBoard_horizontal_field_count, DEFAULT_FIELD_COUNT);
            verticalFieldCount = attributes.getInteger(R.styleable.LottoBoard_vertical_field_count, DEFAULT_FIELD_COUNT);
        } finally {
            attributes.recycle();
        }

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderSize);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        calculateBoardSize(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawBorder(canvas);
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
    }

    private void drawBorder(Canvas canvas) {
        canvas.drawRect(0,0, getWidth(), getHeight(), borderPaint);
    }

    /**
     * Returns point containing calculated view sizes
     * @return
     */
    @SuppressLint("TimberArgCount")
    void calculateBoardSize(int widthMeasureSpec, int heightMeasureSpec) {

        int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSize(minW, widthMeasureSpec);

        int minH = getPaddingTop() + getPaddingBottom() + getSuggestedMinimumHeight();
        int h = resolveSize(minH, heightMeasureSpec);

        int size = Math.max(w,h);

        Timber.d("setMeasuredDimension(%d, %1$d)", size);
        setMeasuredDimension(size, size);
    }
}
