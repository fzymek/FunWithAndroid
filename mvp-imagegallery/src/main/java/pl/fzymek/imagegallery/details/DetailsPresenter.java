package pl.fzymek.imagegallery.details;

import android.content.Intent;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import pl.fzymek.imagegallery.config.Config;
import pl.fzymek.imagegallery.model.gettyimages.Image;
import pl.fzymek.imagegallery.network.GettyImagesService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by filip on 02.10.2016.
 */
public class DetailsPresenter extends MvpBasePresenter<DetailsView> {

    private final GettyImagesService gettyImagesService;

    DetailsPresenter() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.GETTYIMAGES_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        gettyImagesService = retrofit.create(GettyImagesService.class);
    }

    void loadDetails(Intent intent, String query) {
        if (!intent.hasExtra("item")) {
            if (isViewAttached()) {
                getView().showError(new Exception("Error transfering data!"));
            }
        }

        Image img = intent.getParcelableExtra("item");
        if (isViewAttached()) {
            getView().showDetails(img);
        }

        Timber.d("calling getty service for query: %s", query);
        gettyImagesService.getImages(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    Timber.d("result of query: %s", result);
                            if (isViewAttached()) {
                                getView().setData(result.getImages());
                                getView().showContent();
                            }
                        },
                        error -> {
                            Timber.d("Error!: %s", error.getMessage());
                            if (isViewAttached()) {
                                getView().showError(error);
                            }
                        });
    }

}
