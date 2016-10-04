package pl.fzymek.imagegallery.details;

import com.hannesdorfmann.mosby.mvp.MvpView;

import pl.fzymek.imagegallery.gallery.GalleryView;
import pl.fzymek.imagegallery.model.gettyimages.Image;

/**
 * Created by filip on 02.10.2016.
 */
public interface DetailsView extends GalleryView {

    void showDetails(Image image);
    void showError(Throwable e);

}
