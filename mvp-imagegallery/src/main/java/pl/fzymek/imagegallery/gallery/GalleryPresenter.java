package pl.fzymek.imagegallery.gallery;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import pl.fzymek.imagegallery.config.Config;
import pl.fzymek.imagegallery.network.PixabayService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by filip on 29.09.2016.
 */
public class GalleryPresenter extends MvpBasePresenter<GalleryView> {

    PixabayService pixabayService;

    public GalleryPresenter() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.PIXABAY_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        pixabayService = retrofit.create(PixabayService.class);
    }

    public void loadData(String query, boolean pullToRefresh) {
        Timber.d("loadData(%s, %b)", query, pullToRefresh);
        getView().showLoading(pullToRefresh);
        pixabayService.search(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    Timber.d("Response is: %s", response);
                    if (isViewAttached()) {
                        getView().setData(response);
                        getView().showContent();
                    }
                });
    }
}
