package pl.fzymek.mvvmgallery.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import pl.fzymek.gettyimagesmodel.gettyimages.GettySearchResult;
import pl.fzymek.gettyimagesmodel.gettyimages.Image;
import pl.fzymek.mvvmgallery.gallery.GalleryAdapter;

public class GettySearchResultViewModel extends BaseObservable {

    private GettySearchResult result;

    public GettySearchResultViewModel(GettySearchResult result) {
        this.result = result;
    }

    @Bindable
    public List<Image> getResult() {
        return result.getImages();
    }

    @Bindable
    public int getResultCount() {
        return result.getResultCount();
    }

    @BindingAdapter("bind:result")
    public static void setAdapterData(RecyclerView recyclerView, List<Image> results) {
        GalleryAdapter adapter = (GalleryAdapter) recyclerView.getAdapter();
        adapter.setData(results);
    }
}
