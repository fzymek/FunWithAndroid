package pl.fzymek.lottoboards.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.FrameLayout;

import pl.fzymek.lottoboards.R;
import pl.fzymek.lottoboards.view.LottoBoard;

public class MainActivity2 extends AppCompatActivity {

    FrameLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        content = (FrameLayout) findViewById(R.id.content);

        LottoBoard board = new LottoBoard(this);
        board.setVerticalFieldCount(5);
        board.setHorizontalFieldCount(5);
        board.setBackgroundColor(Color.GRAY);
        board.setHighlightBackgroundColor(Color.BLACK);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        board.setDividerSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, metrics));
        board.setCornerRadius(12);
        board.setMaxSelections(5);
        board.setCornerType(LottoBoard.CORNER_TYPE_ROUND);
        board.setTextColor(Color.RED);
        board.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15.0f, metrics));

        content.addView(board);
    }
}
