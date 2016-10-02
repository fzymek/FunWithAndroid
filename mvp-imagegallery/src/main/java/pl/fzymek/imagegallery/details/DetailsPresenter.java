package pl.fzymek.imagegallery.details;

import android.content.Intent;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

import pl.fzymek.imagegallery.model.gettyimages.Image;

/**
 * Created by filip on 02.10.2016.
 */
public class DetailsPresenter extends MvpBasePresenter<DetailsView> {



    public void loadDetails(Intent intent) {
        if (!intent.hasExtra("item")) {
            if (isViewAttached()) {
                getView().showError(new Exception());
            }
        }

        Image img = intent.getParcelableExtra("item");
        if (isViewAttached()){
            getView().showDetails(img);
        }
    }

}
