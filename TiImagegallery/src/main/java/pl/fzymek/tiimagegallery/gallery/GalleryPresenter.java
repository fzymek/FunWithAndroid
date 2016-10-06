package pl.fzymek.tiimagegallery.gallery;

import net.grandcentrix.thirtyinch.TiConfiguration;
import net.grandcentrix.thirtyinch.TiPresenter;

import pl.fzymek.gettyimagesmodel.gettyimages.GettySearchResult;
import pl.fzymek.tiimagegallery.config.Config;
import pl.fzymek.tiimagegallery.network.GettyImagesService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;


class GalleryPresenter extends TiPresenter<GalleryView> {

    private final static TiConfiguration PRESENTER_CONFIGURATION = new TiConfiguration.Builder()
            .setRetainPresenterEnabled(true)
            .build();

    private GettyImagesService service;
    private GettySearchResult result;
    private Throwable error;
    private boolean isRequestPending = false;

    GalleryPresenter() {
        super(PRESENTER_CONFIGURATION);
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
        updateView();
    }

    public void loadData(String phrase, boolean pullToRefresh) {

        if (pullToRefresh) {
            clearData();
        }

        if (!hasData() && !isRequestPending) {
            Timber.d("loadData %s", phrase);
            getView().startLoading(pullToRefresh);
            isRequestPending = true;
            service.getImages(phrase)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            result -> {
                                isRequestPending = false;
                                GalleryPresenter.this.result = result;
                                updateView();
                            },
                            error -> {
                                isRequestPending = false;
                                GalleryPresenter.this.error = error;
                                updateView();
                            }
                    );
        }
    }

    private void clearData() {
        error = null;
        result = null;
    }

    private boolean hasData() {
        boolean hasData = error != null || result != null;
        Timber.d("hasData %b", hasData);
        return hasData;
    }

    private void updateView() {
        Timber.d("updateView");
        if (isAwake()) {
            if (error != null) {
                Timber.d("showError");
                getView().showError(error);
            }
            if (result != null) {
                Timber.d("showGallery");
                getView().showGallery(result.getImages());
                getView().stopLoading();
            }
        }
    }
}
