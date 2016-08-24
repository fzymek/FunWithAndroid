package pl.fzymek.permissions.application;

import android.app.Application;

import com.karumi.dexter.Dexter;

import timber.log.Timber;

/**
 * Created by filip on 19.08.2016.
 */
public class PermissionsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        Dexter.initialize(this);
    }
}
