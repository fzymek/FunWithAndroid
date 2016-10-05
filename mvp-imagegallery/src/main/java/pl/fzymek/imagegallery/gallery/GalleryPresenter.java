package pl.fzymek.imagegallery.gallery;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import pl.fzymek.imagegallery.config.Config;
import pl.fzymek.imagegallery.network.GettyImagesService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by filip on 29.09.2016.
 */
public class GalleryPresenter extends MvpBasePresenter<GalleryView> {

    GettyImagesService gettyImagesService;

    GalleryPresenter() {
        initGettyImagesService();
    }

    private void initGettyImagesService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.GETTYIMAGES_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        gettyImagesService = retrofit.create(GettyImagesService.class);

    }

    void loadData(String query, boolean pullToRefresh) {
        Timber.d("loadData(%s, %b)", query, pullToRefresh);
        getView().showLoading(pullToRefresh);

        gettyImagesService.getImages(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gettySearchResult -> {
                    if (isViewAttached()) {
                        getView().setData(gettySearchResult.getImages());
                        getView().showContent();
                    }
                }, throwable -> {
                    if (isViewAttached()) {
                        getView().showError(throwable, pullToRefresh);
                    }
                });

    }
}
