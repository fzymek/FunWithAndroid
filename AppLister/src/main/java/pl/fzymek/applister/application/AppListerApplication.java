package pl.fzymek.applister.application;

import android.app.Application;

import com.karumi.dexter.Dexter;

import timber.log.Timber;

public class AppListerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Dexter.initialize(this);
        Timber.plant(new Timber.DebugTree());
    }
}
