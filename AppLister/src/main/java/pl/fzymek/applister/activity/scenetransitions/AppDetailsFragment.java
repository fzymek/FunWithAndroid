package pl.fzymek.applister.activity.scenetransitions;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.fzymek.applister.R;

/**
 * Created by filip on 16.09.2016.
 */
public class AppDetailsFragment extends Fragment implements BackListener {

    private static final String PACKAGE = "pl.fzymek.applister.activity.scenetransitions";
    public static final String INFO = PACKAGE + ".info";

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
    @BindView(R.id.package_name)
    TextView packageName;
    Unbinder unbinder;
    private ResolveInfo info;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info = getArguments().getParcelable(INFO);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_app_details, container, false);
        bindViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupActionBar();

        CharSequence text = info.loadLabel(getPackageManager());
        Drawable drawable = info.loadIcon(getPackageManager());
        this.text.setText(text);
        this.icon.setImageDrawable(drawable);
        this.packageName.setText(info.activityInfo.name);
    }

    private PackageManager getPackageManager() {
        return getAppCompatActivity().getPackageManager();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void bindViews(View view) {
        unbinder = ButterKnife.bind(this, view);
        ViewCompat.setTransitionName(icon, "icon");
        ViewCompat.setTransitionName(text, "text");

        content.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                view.removeOnLayoutChangeListener(this);

                int cx = v.getWidth() / 2;
                int cy = v.getHeight() / 2;
                float radius = (float) Math.hypot(v.getWidth(), v.getHeight());

                Animator circularReveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
                circularReveal.setDuration(500);
                circularReveal.start();
            }
        });
    }

    private void setupActionBar() {
        getAppCompatActivity().setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getAppCompatActivity().getSupportActionBar().setTitle(R.string.app_name);
        getAppCompatActivity().getSupportActionBar().setHomeButtonEnabled(false);
        getAppCompatActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private AppCompatActivity getAppCompatActivity() {
        return (AppCompatActivity) getActivity();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed(Runnable action) {
        int cx = content.getWidth() / 2;
        int cy = content.getHeight() / 2;
        float radius = (float) Math.hypot(content.getWidth(), content.getHeight());

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(content, cx, cy, radius, 0);
        circularReveal.setDuration(500);
        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                content.setVisibility(View.INVISIBLE);
                content.post(action);
            }
        });
        circularReveal.start();
    }
}
