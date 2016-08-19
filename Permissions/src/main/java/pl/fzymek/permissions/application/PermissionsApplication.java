package pl.fzymek.permissions.application;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

/**
 * Created by filip on 19.08.2016.
 */
public class PermissionsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        Timber.plant(new Timber.DebugTree());
    }
}
