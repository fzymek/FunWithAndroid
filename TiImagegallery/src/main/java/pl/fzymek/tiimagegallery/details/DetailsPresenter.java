package pl.fzymek.tiimagegallery.details;

import android.os.Bundle;

import net.grandcentrix.thirtyinch.TiPresenter;

class DetailsPresenter extends TiPresenter<DetailsView>{

    void loadDetails(Bundle args){
        getView().showImage(args.getParcelable(DetailsFragment.IMAGE_ARG));
    }

}
