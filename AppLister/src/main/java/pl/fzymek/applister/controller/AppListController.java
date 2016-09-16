package pl.fzymek.applister.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.Collections;
import java.util.List;

import pl.fzymek.applister.ui.AppListUI;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by filip on 29.08.2016.
 */
public class AppListController {

    protected Activity activity;
    private AppListUI ui;

    public AppListController(Activity activity) {
        this.activity = activity;
    }

    public void setUi(AppListUI ui) {
        this.ui = ui;
    }

    public void fetchData() {
        ui.onLoadingStarted();
        Observable<ResolveInfo> installedAppsObservable = createInstalledAppsObservable();
        installedAppsObservable.subscribe(new InstalledAppSubscriber());
    }

    private Observable<ResolveInfo> createInstalledAppsObservable() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> infos = activity.getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        PackageManager packageManager = activity.getPackageManager();
        Collections.sort(infos, (l, r) -> l.loadLabel(packageManager).toString().compareTo(r.loadLabel(packageManager).toString()));
        return Observable.from(infos);

    }

    private class InstalledAppSubscriber extends Subscriber<ResolveInfo> {
        @Override
        public void onCompleted() {
            ui.onLoadingFinished();
        }

        @Override
        public void onError(Throwable e) {
            ui.onError(e);
        }

        @Override
        public void onNext(ResolveInfo info) {
            if (isUnsubscribed()) {
                return;
            }
            ui.onNextApp(info);
        }
    }
}
