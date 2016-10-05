package pl.fzymek.tiimagegallery.gallery;

import net.grandcentrix.thirtyinch.TiView;

import java.util.List;

import pl.fzymek.gettyimagesmodel.gettyimages.Image;

public interface GalleryView extends TiView {

    void startLoading(boolean pullToRefresh);
    void stopLoading();
    void showError(Throwable error);
    void showGallery(List<Image> images);

}
