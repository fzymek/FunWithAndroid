package pl.fzymek.lottoboards.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import pl.fzymek.lottoboards.R;

public class LottoBoard extends View implements View.OnTouchListener {


    class TapListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            toggleCheckMarkOnField(new PointF(e.getX(), e.getY()));
            return true;
        }
    }

    public static final int BORDER_COUNT = 2;
    private final static int DEFAULT_BORDER_SIZE_DIP = 4;
    private final static int DEFAULT_DIVIDER_SIZE_DIP = 4;
    private final static int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#FFC77F");
    private final static int DEFAULT_HIGHLIGHT_BACKGROUND_COLOR = Color.parseColor("#AAAAAA");
    private final static int DEFAULT_MAX_SELECTIONS = 1;
    private final static int DEFAULT_FIELD_COUNT = 2;
    private final static int DEFAULT_CORNER_RADIUS_DIP = 3;
    private final static int DEFAULT_TEXT_COLOR = Color.BLACK;
    private final static int DEFAULT_TEXT_SIZE_SP = 14;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CORNER_TYPE_ROUND, CORNER_TYPE_SHARP})
    public @interface CornerType {}

    public final static int CORNER_TYPE_SHARP = 0;
    public final static int CORNER_TYPE_ROUND = 1;

    int borderSize;

    Paint backgroundPaint;
    int backgroundColor;
    int highlightBackgroundColor;

    int dividerSize;
    int dividerXPadding = 0;
    int dividerYPadding = 0;

    int horizontalFieldCount;
    int verticalFieldCount;

    Paint fieldPaint;
    int fieldWidth;
    int fieldHeight;
    int cornerType;
    int cornerRadius;
    //rectangle shape used for optimizing drawing of fields (allocated once)
    RectF fieldRect;

    TextPaint textPaint;
    int textColor;
    int textSize;

    int maxSelections;

    @DrawableRes
    int checkMarkDrawableRes;
    Bitmap checkMarkBitmap;
    Rect checkMarkRect;
    Rect srcRect;

    SparseBooleanArray markedFields;

    GestureDetector tapDetector;

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

            cornerRadius = attributes.getDimensionPixelSize(R.styleable.LottoBoard_corner_radius,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CORNER_RADIUS_DIP, metrics));

            backgroundColor = attributes.getColor(R.styleable.LottoBoard_background_color, DEFAULT_BACKGROUND_COLOR);
            highlightBackgroundColor = attributes.getColor(R.styleable.LottoBoard_highlight_background_color, DEFAULT_HIGHLIGHT_BACKGROUND_COLOR);

            horizontalFieldCount = attributes.getInteger(R.styleable.LottoBoard_horizontal_field_count, DEFAULT_FIELD_COUNT);
            verticalFieldCount = attributes.getInteger(R.styleable.LottoBoard_vertical_field_count, DEFAULT_FIELD_COUNT);

            textColor = attributes.getColor(R.styleable.LottoBoard_text_color, DEFAULT_TEXT_COLOR);
            textSize = attributes.getDimensionPixelSize(R.styleable.LottoBoard_text_size,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE_SP, metrics));

            cornerType = attributes.getInt(R.styleable.LottoBoard_corner_type, CORNER_TYPE_SHARP);

            checkMarkDrawableRes = attributes.getResourceId(R.styleable.LottoBoard_checkmark_drawable, R.drawable.ic_check_mark);

            maxSelections = attributes.getInteger(R.styleable.LottoBoard_max_selections, DEFAULT_MAX_SELECTIONS);
        } finally {
            attributes.recycle();
        }

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundColor);

        fieldPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fieldPaint.setStyle(Paint.Style.FILL);
        fieldPaint.setColor(Color.WHITE);
        fieldRect = new RectF();

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setStrokeJoin(Paint.Join.BEVEL);
        textPaint.setColor(textColor);

        markedFields = new SparseBooleanArray();

        if (isInEditMode()) {
            markedFields.put(1, true);
        }

        checkMarkRect = new Rect();
        checkMarkBitmap = BitmapFactory.decodeResource(context.getResources(), checkMarkDrawableRes);
        srcRect = new Rect(0, 0, checkMarkBitmap.getWidth(), checkMarkBitmap.getHeight());

        setOnTouchListener(this);

        tapDetector = new GestureDetector(context, new TapListener());

        setSaveEnabled(true);
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    public int getBoardBackgroundColor() {
        return backgroundColor;
    }

    public void setBoardBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getHighlightBackgroundColor() {
        return highlightBackgroundColor;
    }

    public void setHighlightBackgroundColor(int highlightBackgroundColor) {
        this.highlightBackgroundColor = highlightBackgroundColor;
    }

    public int getDividerSize() {
        return dividerSize;
    }

    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
    }

    public int getHorizontalFieldCount() {
        return horizontalFieldCount;
    }

    public void setHorizontalFieldCount(int horizontalFieldCount) {
        this.horizontalFieldCount = horizontalFieldCount;
    }

    public int getVerticalFieldCount() {
        return verticalFieldCount;
    }

    public void setVerticalFieldCount(int verticalFieldCount) {
        this.verticalFieldCount = verticalFieldCount;
    }

    public @CornerType int getCornerType() {
        return cornerType;
    }

    public void setCornerType(@CornerType int cornerType) {
        this.cornerType = cornerType;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getMaxSelections() {
        return maxSelections;
    }

    public void setMaxSelections(int maxSelections) {
        this.maxSelections = maxSelections;
    }

    public @DrawableRes int getCheckMarkDrawable() {
        return checkMarkDrawableRes;
    }

    public void setCheckMarkDrawable(@DrawableRes int checkMarkDrawableRes) {
        this.checkMarkDrawableRes = checkMarkDrawableRes;
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
        drawSelectionMarkers(canvas);
    }

    void calculateBoardSize(int widthMeasureSpec, int heightMeasureSpec) {

        int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSize(minW, widthMeasureSpec);

        int minH = getPaddingTop() + getPaddingBottom() + getSuggestedMinimumHeight();
        int h = resolveSize(minH, heightMeasureSpec);

        int size = Math.min(w, h);

        setMeasuredDimension(size, size);
    }

    void calculateFieldSize() {
        int availableSpaceX = getAvailableSpace(getMeasuredWidth(), borderSize, dividerSize);
        int availableSpaceY = getAvailableSpace(getMeasuredHeight(), borderSize, dividerSize);

        fieldWidth = calculateFieldSize(availableSpaceX, horizontalFieldCount, dividerSize);
        fieldHeight = calculateFieldSize(availableSpaceY, verticalFieldCount, dividerSize);
    }

    void calculatePaddings() {
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

    int getAvailableSpace(int space, int borderSize, int dividerSize) {
        return space - BORDER_COUNT * borderSize - BORDER_COUNT * dividerSize;
    }

    int calculateFieldSize(int availableSpace, int fieldCount, int dividerSize) {
        return (availableSpace - (fieldCount - 1) * dividerSize) / fieldCount;
    }

    void drawBackground(Canvas canvas) {
        if (markedFields.size() < maxSelections) {
            backgroundPaint.setColor(backgroundColor);
        } else {
            backgroundPaint.setColor(highlightBackgroundColor);
        }
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
    }

    void drawFields(Canvas canvas) {

        for (int i = 0; i < verticalFieldCount; i++) {
            int y = borderSize + dividerYPadding + i * fieldHeight + i * (dividerSize + dividerYPadding);
            for (int j = 0; j < horizontalFieldCount; j++) {
                int x = borderSize + dividerXPadding + j * fieldWidth + j * (dividerSize + dividerXPadding);

                if (cornerType == CORNER_TYPE_SHARP) {
                    //draw rectangle with sharp corners
                    canvas.drawRect(x, y, x + fieldWidth, y + fieldHeight, fieldPaint);
                } else if (cornerType == CORNER_TYPE_ROUND) {
                    //draw rectangle with rounded corners
                    fieldRect.left = x;
                    fieldRect.top = y;
                    fieldRect.right = x + fieldWidth;
                    fieldRect.bottom = y + fieldHeight;
                    canvas.drawRoundRect(fieldRect, cornerRadius, cornerRadius, fieldPaint);
                }
            }
        }

    }

    //TODO: optimize text drawing -> put canvas.drawText inside drawFields() for loop
    void drawFieldLabels(Canvas canvas) {
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

    //TODO: optimize check mark drawing -> put canvas.drawBitmap() inside drawField() for loop
    void drawSelectionMarkers(Canvas canvas) {
        if (markedFields.size() == 0) {
            return;
        }

        int fieldNo = 0;
        for (int i = 0; i < verticalFieldCount; i++) {
            for (int j = 0; j < horizontalFieldCount; j++) {
                ++fieldNo;
                if (markedFields.get(fieldNo)) {
                    //draw marker

                    int y = borderSize + dividerYPadding + i * fieldHeight + i * (dividerSize + dividerYPadding);
                    int x = borderSize + dividerXPadding + j * fieldWidth + j * (dividerSize + dividerXPadding);

                    checkMarkRect.left = x;
                    checkMarkRect.top = y;
                    checkMarkRect.right = x + fieldWidth;
                    checkMarkRect.bottom = y + fieldHeight;

                    canvas.drawBitmap(checkMarkBitmap, srcRect, checkMarkRect, null);
                }
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        tapDetector.onTouchEvent(motionEvent);
        return true;
    }

    void toggleCheckMarkOnField(PointF pointF) {
        int fieldIndex = calculateFieldIndexFromTap(pointF);
        if (fieldIndex < 0) {
            return;
        }
        if (markedFields.get(fieldIndex)) {
            markedFields.delete(fieldIndex);
        } else {
            if (markedFields.size() < maxSelections) {
                markedFields.put(fieldIndex, true);
            }
        }
        invalidate();
    }

    private int calculateFieldIndexFromTap(PointF tapPoint) {
        int fieldNo=0;
        for (int i = 0; i < verticalFieldCount; i++) {
            int y = borderSize + dividerYPadding + i * fieldHeight + i * (dividerSize + dividerYPadding);
            for (int j = 0; j < horizontalFieldCount; j++) {
                ++fieldNo;
                int x = borderSize + dividerXPadding + j * fieldWidth + j * (dividerSize + dividerXPadding);
                RectF field = new RectF(x,y, x+fieldWidth, y+fieldHeight);
                if (field.contains(tapPoint.x, tapPoint.y)) {
                    return fieldNo;
                }
            }
        }
        return -1;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), getCheckedFieldIds());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        int selections[] = savedState.markedFields;
        for (int selection : selections) {
            markedFields.put(selection, true);
        }
    }

    private int[] getCheckedFieldIds() {
        if (markedFields.size() == 0) {
            return null;
        }

        int selectedIds[] = new int[markedFields.size()];
        for (int i = 0; i < markedFields.size(); i++) {
            selectedIds[i] = markedFields.keyAt(i);
        }
        return selectedIds;
    }


    static class SavedState extends BaseSavedState {

        public static final BaseSavedState.Creator<SavedState> CREATOR = new BaseSavedState.Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override
            public SavedState[] newArray(int i) {
                return new SavedState[0];
            }
        };

        int markedFields[];

        public SavedState(Parcel source) {
            super(source);
            source.readIntArray(markedFields);
        }

        public SavedState(Parcelable superState, int[] markedFields) {
            super(superState);
            this.markedFields = markedFields;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeIntArray(markedFields);
        }

    }

}
