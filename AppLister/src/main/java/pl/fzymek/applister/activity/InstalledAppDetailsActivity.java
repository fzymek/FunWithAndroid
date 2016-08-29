package pl.fzymek.applister.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.fzymek.applister.R;
import timber.log.Timber;

public class InstalledAppDetailsActivity extends AppCompatActivity {

    public static final String APP_PACKAGE_NAME = "pl.fzymek.applister.activity.InstalledAppDetailsActivity.APP_PACKAGE_NAME";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(android.R.id.icon)
    ImageView icon;
    @BindView(android.R.id.text1)
    TextView text;
    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installed_app_details);
        setSupportActionBar(toolbar);
        bindViews();
        setUpFromBundle(getIntent());
    }

    private void setUpFromBundle(Intent intent) {
        String packageName = intent.getStringExtra(APP_PACKAGE_NAME);
        Timber.d("Requested package name: %s", packageName);
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Timber.d("App Info: %s", info);
            CharSequence text = info.loadLabel(getPackageManager());
            Drawable drawable = info.loadIcon(getPackageManager());
            Timber.d("Resolved app name: %s, drawable: %s", text, drawable);

            this.text.setText(text);
            this.icon.setImageDrawable(drawable);

        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
            Snackbar.make(toolbar.getRootView(), "Cannot load details", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void bindViews() {
        unbinder = ButterKnife.bind(this);
    }


}
