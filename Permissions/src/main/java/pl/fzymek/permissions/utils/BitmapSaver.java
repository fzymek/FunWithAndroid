package pl.fzymek.permissions.utils;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by filip on 24.08.2016.
 */
public class BitmapSaver {

    public Observable<File> save(Bitmap bitmap) {
        File dir = PictureUtils.createPictureDirectory();
        File pic = PictureUtils.createPictureFile(dir);

        return createPictureSaverObservable(pic, bitmap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    private
    @NonNull
    Observable<File> createPictureSaverObservable(File file, Bitmap bitmap) {
        return Observable.create(subscriber -> {
            try {
                Timber.d("Writing bitmap to file");
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                subscriber.onNext(file);
                subscriber.onCompleted();
                Timber.d("Bitmap saved");
            } catch (IOException e) {
                Timber.e(e);
                subscriber.onError(e);
            }
        });
    }
}
