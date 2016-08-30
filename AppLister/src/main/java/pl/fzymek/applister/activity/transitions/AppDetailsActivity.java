package pl.fzymek.applister.activity.transitions;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.fzymek.applister.R;

public class AppDetailsActivity extends AppCompatActivity {

    public static final String PACKAGE = "pl.fzymek.applister.activity.transitions";
    public static final String PACKAGE_NAME = PACKAGE + ".package_name";
    public static final String ORIENTATION = PACKAGE + ".orientation";
    public static final String LEFT = PACKAGE + ".left";
    public static final String TOP = PACKAGE + ".top";
    public static final String INFO = PACKAGE + ".info";

    private final static long ANIMATION_DURATION = TimeUnit.SECONDS.toMillis(1);

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(android.R.id.icon)
    ImageView icon;
    @BindView(android.R.id.text1)
    TextView text;
    @BindView(R.id.contentPanel)
    View content;
    Unbinder unbinder;

    int orientation;
    private ResolveInfo info;
    private int thumbnailLeft, leftDelta;
    private int thumbnailTop, topDelta;
    private TimeInterpolator decelerator = new DecelerateInterpolator();
    private AccelerateInterpolator accelerator = new AccelerateInterpolator();
    private ColorDrawable bg = new ColorDrawable(Color.WHITE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installed_app_details);
        setSupportActionBar(toolbar);
        bindViews();
        readArgumentsFromBundle(getIntent());
        setUpFromBundle(savedInstanceState);
    }

    private void readArgumentsFromBundle(Intent intent) {
        Bundle extras = intent.getExtras();
        info = extras.getParcelable(INFO);
        thumbnailLeft = extras.getInt(LEFT);
        thumbnailTop = extras.getInt(TOP);
        orientation = extras.getInt(ORIENTATION);
    }

    private void setUpFromBundle(Bundle savedInstanceState) {
        CharSequence text = info.loadLabel(getPackageManager());
        Drawable drawable = info.loadIcon(getPackageManager());
        this.text.setText(text);
        this.icon.setImageDrawable(drawable);
        this.content.setBackground(bg);

        if (savedInstanceState == null) {
            ViewTreeObserver observer = icon.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    icon.getViewTreeObserver().removeOnPreDrawListener(this);
                    int screenLocation[] = new int[2];
                    icon.getLocationOnScreen(screenLocation);
                    leftDelta = thumbnailLeft - screenLocation[0];
                    topDelta = thumbnailTop - screenLocation[1];

                    runEnterAnimation();

                    return true;
                }
            });
        }
    }

    private void runEnterAnimation() {
        icon.setPivotX(0);
        icon.setPivotY(0);
        icon.setTranslationX(leftDelta);
        icon.setTranslationY(topDelta);

        text.setAlpha(0);


        icon.animate()
                .setDuration(ANIMATION_DURATION)
                .translationX(0)
                .translationY(0)
                .setInterpolator(decelerator)
                .withEndAction(() -> {
                    text.setTranslationY(-text.getHeight());

                    text.animate()
                            .setDuration(ANIMATION_DURATION/2)
                            .translationY(0)
                            .alpha(1)
                            .setInterpolator(accelerator);
                });


        ObjectAnimator backgroundAnim = ObjectAnimator.ofInt(bg, "alpha", 0, 255);
        backgroundAnim.setDuration(ANIMATION_DURATION);
        backgroundAnim.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        runExitAnimation(this::finish);
    }

    private void runExitAnimation(Runnable runnable) {
        final boolean fadeOut;
        if (getResources().getConfiguration().orientation != orientation) {
            icon.setPivotX(icon.getWidth() / 2);
            icon.setPivotY(icon.getHeight() / 2);
            leftDelta = 0;
            topDelta = 0;
            fadeOut = true;
        } else {
            fadeOut = false;
        }

        text.animate()
            .alpha(0)
                .translationY(-text.getHeight())
                .setDuration(ANIMATION_DURATION)
                .setInterpolator(accelerator)
                .withEndAction(() -> {
                    icon.animate()
                            .setDuration(ANIMATION_DURATION)
                            .translationX(leftDelta)
                            .translationY(topDelta)
                            .withEndAction(runnable);

                    if (fadeOut) {
                        icon.animate().alpha(0);
                    }

                    // Fade out background
                    ObjectAnimator bgAnim = ObjectAnimator.ofInt(bg, "alpha", 0);
                    bgAnim.setDuration(ANIMATION_DURATION);
                    bgAnim.start();
                });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }

    private void bindViews() {
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
    }


}
