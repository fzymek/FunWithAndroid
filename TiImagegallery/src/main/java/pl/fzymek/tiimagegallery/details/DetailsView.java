package pl.fzymek.tiimagegallery.details;

import net.grandcentrix.thirtyinch.TiView;

import pl.fzymek.gettyimagesmodel.gettyimages.Image;

interface DetailsView extends TiView {

    void showImage(Image image);

}
