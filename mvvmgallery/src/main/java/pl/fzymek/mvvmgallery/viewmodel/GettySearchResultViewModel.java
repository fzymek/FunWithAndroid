package pl.fzymek.mvvmgallery.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.List;

import pl.fzymek.gettyimagesmodel.gettyimages.GettySearchResult;
import pl.fzymek.gettyimagesmodel.gettyimages.Image;

public class GettySearchResultViewModel extends BaseObservable {

    private GettySearchResult result;

    public GettySearchResultViewModel(GettySearchResult result) {
        this.result = result;
        notifyChange();
    }

    @Bindable
    public int getResultCount() {
        return result.getResultCount();
    }

    @Bindable
    public List<Image> getResult() {
        return result.getImages();
    }
}
