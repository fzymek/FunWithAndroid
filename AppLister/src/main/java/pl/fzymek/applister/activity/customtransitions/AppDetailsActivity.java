package pl.fzymek.applister.activity.customtransitions;

import android.animation.TimeInterpolator;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.fzymek.applister.R;

public class AppDetailsActivity extends AppCompatActivity {

    private static final String PACKAGE = "pl.fzymek.applister.activity.transitions";
    public static final String INFO = PACKAGE + ".info";

    private final static long ANIMATION_DURATION = TimeUnit.MILLISECONDS.toMillis(500);
    private final static TimeInterpolator decelerator = new DecelerateInterpolator();
    private final static TimeInterpolator accelerator = new AccelerateInterpolator();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(android.R.id.icon)
    ImageView icon;
    @BindView(android.R.id.text1)
    TextView text;
    @BindView(R.id.contentPanel)
    View content;
    @BindView(R.id.title_container)
    LinearLayout titleContainer;
    Unbinder unbinder;
    private ResolveInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
        bindViews();
        readArgumentsFromBundle(getIntent());
        setUpFromBundle(savedInstanceState);
    }

    private void readArgumentsFromBundle(Intent intent) {
        Bundle extras = intent.getExtras();
        info = extras.getParcelable(INFO);
    }

    private void setUpFromBundle(Bundle savedInstanceState) {
        CharSequence text = info.loadLabel(getPackageManager());
        Drawable drawable = info.loadIcon(getPackageManager());
        this.text.setText(text);
        this.icon.setImageDrawable(drawable);

        if (savedInstanceState == null) {
            ViewTreeObserver observer = icon.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    icon.getViewTreeObserver().removeOnPreDrawListener(this);
                    int screenLocation[] = new int[2];
                    icon.getLocationOnScreen(screenLocation);

                    runEnterAnimation();

                    return true;
                }
            });
        }
    }

    private void runEnterAnimation() {

        //prepare view for animation
        titleContainer.setTranslationY(-titleContainer.getHeight());
        icon.setTranslationX(-icon.getWidth());
        text.setAlpha(0);

        //animate title container
        titleContainer.animate()
                .setDuration(ANIMATION_DURATION)
                .translationY(0)
                .setInterpolator(decelerator)
                .withEndAction(() -> {
                    //animate icon move
                    icon.animate()
                            .setDuration(ANIMATION_DURATION)
                            .translationX(0)
                            .setInterpolator(decelerator)
                            .withEndAction(() -> {
                                //animate text
                                text.setTranslationY(-text.getHeight());
                                text.animate()
                                        .setDuration(ANIMATION_DURATION)
                                        .translationY(0)
                                        .alpha(1)
                                        .setInterpolator(decelerator);
                            });

                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        //run enter animations in reverse

        //fade & move text
        text.animate()
                .setDuration(ANIMATION_DURATION)
                .translationY(-text.getHeight())
                .alpha(0)
                .setInterpolator(accelerator)
                .withEndAction(() -> {
                    //move icon
                    icon.animate()
                            .setDuration(ANIMATION_DURATION)
                            .translationX(-icon.getWidth())
                            .setInterpolator(accelerator)
                            .withEndAction(() -> {
                                //hide container
                                titleContainer.animate()
                                        .setDuration(ANIMATION_DURATION)
                                        .translationY(-titleContainer.getHeight())
                                        .setInterpolator(accelerator)
                                        .withEndAction(runnable);
                            });

                });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void bindViews() {
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


}
