package pl.fzymek.lottoboards.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import pl.fzymek.lottoboards.R;
import timber.log.Timber;

@SuppressLint({"BinaryOperationInTimber", "TimberArgCount"})
public class LottoBoard extends View {

    public static final int BORDER_COUNT = 2;
    private final static int DEFAULT_BORDER_SIZE_DIP = 4;
    private final static int DEFAULT_DIVIDER_SIZE_DIP = 4;
    private final static int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#FFC77F");
    private final static int DEFAULT_FIELD_COUNT = 2;
    private final static int DEFAULT_TEXT_COLOR = Color.BLACK;
    private final static int DEFAULT_TEXT_SIZE_SP = 14;

    int borderSize;

    Paint backgroundPaint;
    int backgroundColor;

    int dividerSize;
    int dividerXPadding = 0;
    int dividerYPadding = 0;

    int horizontalFieldCount;
    int verticalFieldCount;

    Paint fieldPaint;
    int fieldWidth;
    int fieldHeight;

    TextPaint textPaint;
    int textColor;
    int textSize;

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

            dividerSize = attributes.getDimensionPixelSize(R.styleable.LottoBoard_border_size,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_DIVIDER_SIZE_DIP, metrics));

            backgroundColor = attributes.getColor(R.styleable.LottoBoard_background_color, DEFAULT_BACKGROUND_COLOR);

            horizontalFieldCount = attributes.getInteger(R.styleable.LottoBoard_horizontal_field_count, DEFAULT_FIELD_COUNT);
            verticalFieldCount = attributes.getInteger(R.styleable.LottoBoard_vertical_field_count, DEFAULT_FIELD_COUNT);

            textColor = attributes.getColor(R.styleable.LottoBoard_text_color, DEFAULT_TEXT_COLOR);
            textSize = attributes.getDimensionPixelSize(R.styleable.LottoBoard_text_size,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE_SP, metrics));
        } finally {
            attributes.recycle();
        }

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundColor);

        fieldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fieldPaint.setStyle(Paint.Style.FILL);
        fieldPaint.setColor(Color.WHITE);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setStrokeJoin(Paint.Join.BEVEL);
        textPaint.setColor(textColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        calculateBoardSize(widthMeasureSpec, heightMeasureSpec);
        calculateFieldSize();
        calculatePaddings();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawFields(canvas);
        drawFieldLabels(canvas);
    }

    void calculateBoardSize(int widthMeasureSpec, int heightMeasureSpec) {

        int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSize(minW, widthMeasureSpec);

        int minH = getPaddingTop() + getPaddingBottom() + getSuggestedMinimumHeight();
        int h = resolveSize(minH, heightMeasureSpec);

        int size = Math.max(w, h);

        Timber.d("setMeasuredDimension(%d, %1$d)", size);
        setMeasuredDimension(size, size);
    }

    private void calculateFieldSize() {
        int availableSpaceX = getAvailableSpace(getMeasuredWidth(), borderSize, dividerSize);
        int availableSpaceY = getAvailableSpace(getMeasuredHeight(), borderSize, dividerSize);

        fieldWidth = calculateFieldSize(availableSpaceX, horizontalFieldCount, dividerSize);
        fieldHeight = calculateFieldSize(availableSpaceY, verticalFieldCount, dividerSize);
    }

    private void calculatePaddings() {
        int widthDividerCount = horizontalFieldCount - 1;
        int occupiedWidth = borderSize * 2 + fieldWidth * horizontalFieldCount + widthDividerCount * dividerSize;
        int widthLeft = getMeasuredWidth() - occupiedWidth;

        if (widthLeft > 0) {
            dividerXPadding = widthLeft / (widthDividerCount + BORDER_COUNT); //+2 because we have 2 borders
        }

        int heightDividerCount = verticalFieldCount - 1;
        int occupiedHeight = borderSize * 2 + fieldHeight * verticalFieldCount + heightDividerCount * dividerSize;
        int heightLeft = getMeasuredHeight() - occupiedHeight;

        if (heightLeft > 0) {
            dividerYPadding = heightLeft / (heightDividerCount + BORDER_COUNT);
        }
    }

    private int getAvailableSpace(int space, int borderSize, int dividerSize) {
        return space - BORDER_COUNT * borderSize - BORDER_COUNT * dividerSize;
    }

    private int calculateFieldSize(int availableSpace, int fieldCount, int dividerSize) {
        return (availableSpace - (fieldCount - 1) * dividerSize) / fieldCount;
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
    }

    private void drawFields(Canvas canvas) {

        for (int i = 0; i < verticalFieldCount; i++) {
            int y = borderSize + dividerYPadding + i * fieldHeight + i * (dividerSize + dividerYPadding);
            for (int j = 0; j < horizontalFieldCount; j++) {
                int x = borderSize + dividerXPadding + j * fieldWidth + j * (dividerSize + dividerXPadding);
                canvas.drawRect(x, y, x + fieldWidth, y + fieldHeight, fieldPaint);
            }
        }

    }

    //TODO: optimize text drawing -> put canvas.drawText inside drawFields() for loop
    private void drawFieldLabels(Canvas canvas) {
        int count = 0;
        for (int i = 0; i < verticalFieldCount; i++) {
            int rectY = borderSize + dividerYPadding + i * fieldHeight + i * (dividerSize + dividerYPadding);
            int textY = rectY + fieldHeight / 2 - (int) ((textPaint.descent() + textPaint.ascent()) / 2);

            for (int j = 0; j < horizontalFieldCount; j++) {
                int rectX = borderSize + dividerXPadding + j * fieldWidth + j * (dividerSize + dividerXPadding);
                String text = "" + ++count;

                int textWidth = (int) textPaint.measureText(text);
                int textX = rectX + fieldWidth / 2 - textWidth / 2;

                canvas.drawText(text, textX, textY, textPaint);

            }
        }
    }

}
