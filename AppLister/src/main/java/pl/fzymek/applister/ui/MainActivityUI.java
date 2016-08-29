package pl.fzymek.applister.ui;

import android.content.pm.ResolveInfo;

/**
 * Created by filip on 29.08.2016.
 */
public interface MainActivityUI {

    void onLoadingStarted();

    void onLoadingFinished();

    void onError(Throwable error);

    void onNextApp(ResolveInfo info);
}
