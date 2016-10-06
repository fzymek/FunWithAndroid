package pl.fzymek.tiimagegallery.gallery;

import net.grandcentrix.thirtyinch.TiView;
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

import java.util.List;

import pl.fzymek.gettyimagesmodel.gettyimages.Image;


public interface GalleryView extends TiView {

//    @CallOnMainThread
    void startLoading(boolean pullToRefresh);
//    @CallOnMainThread
    void stopLoading();
//    @CallOnMainThread
    void showError(Throwable error);
//    @CallOnMainThread
    void showGallery(List<Image> images);

}
