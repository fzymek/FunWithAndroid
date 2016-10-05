package pl.fzymek.imagegallery.details;

import pl.fzymek.gettyimagesmodel.gettyimages.Image;
import pl.fzymek.imagegallery.gallery.GalleryView;

/**
 * Created by filip on 02.10.2016.
 */
public interface DetailsView extends GalleryView {

    void showDetails(Image image);
    void showError(Throwable e);

}
