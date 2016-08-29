package pl.fzymek.applister.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.Collections;
import java.util.List;

import pl.fzymek.applister.ui.MainActivityUI;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

/**
 * Created by filip on 29.08.2016.
 */
public class MainActivityController {

    protected Activity activity;
    private MainActivityUI ui;
    private final InstalledAppSubscriber subscriber = new InstalledAppSubscriber();

    public MainActivityController(Activity activity) {
        this.activity = activity;
    }

    public void setUi(MainActivityUI ui) {
        this.ui = ui;
    }

    public void fetchData() {
        ui.onLoadingStarted();
        Observable<ResolveInfo> installedAppsObservable = createInstalledAppsObservable();
        installedAppsObservable.subscribe(subscriber);
    }

    private Observable<ResolveInfo> createInstalledAppsObservable() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> infos = activity.getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        PackageManager packageManager = activity.getPackageManager();
        Collections.sort(infos, (l, r) -> l.loadLabel(packageManager).toString().compareTo(r.loadLabel(packageManager).toString()));
        Timber.d("Number of apps: %d", infos.size());
        return Observable.from(infos);

    }

    private class InstalledAppSubscriber extends Subscriber<ResolveInfo> {
        @Override
        public void onCompleted() {
            Timber.d("onCompleted");
            ui.onLoadingFinished();
        }

        @Override
        public void onError(Throwable e) {
            Timber.d("onError");
            ui.onError(e);
        }

        @Override
        public void onNext(ResolveInfo info) {
            Timber.d("onNext");
            if (isUnsubscribed()) {
                return;
            }
            ui.onNextApp(info);
        }
    }
}
