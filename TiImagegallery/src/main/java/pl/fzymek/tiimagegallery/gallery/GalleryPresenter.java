package pl.fzymek.tiimagegallery.gallery;

import net.grandcentrix.thirtyinch.TiPresenter;

import java.util.Random;

import pl.fzymek.tiimagegallery.config.Config;
import pl.fzymek.tiimagegallery.network.GettyImagesService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class GalleryPresenter extends TiPresenter<GalleryView> {

    GettyImagesService service;

    protected GalleryPresenter() {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Config.GETTYIMAGES_API_URL)
                .build();


        service = retrofit.create(GettyImagesService.class);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        Timber.d("onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy");
    }

    @Override
    protected void onSleep() {
        super.onSleep();
        Timber.d("onSleep");
    }

    @Override
    protected void onWakeUp() {
        super.onWakeUp();
        Timber.d("onWakeUp");
    }

    public void loadData(String phrase, boolean pullToRefresh) {
        getView().startLoading(pullToRefresh);

        Timber.d("loadData %s", phrase);

        service.getImages(phrase)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            getView().showGallery(result.getImages());
                            getView().stopLoading();
                        },
                         error -> {
                             getView().showError(error);
                         }
                );
    }
}
