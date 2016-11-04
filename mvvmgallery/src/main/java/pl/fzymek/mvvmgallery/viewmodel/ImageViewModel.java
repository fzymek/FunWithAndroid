package pl.fzymek.mvvmgallery.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import pl.fzymek.gettyimagesmodel.gettyimages.Image;

public class ImageViewModel {

    public final ObservableField<String> description = new ObservableField<>();
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> author = new ObservableField<>();
    public final ObservableField<String> imageUrl = new ObservableField<>();

    public ImageViewModel(Image image) {
        description.set(image.getCaption());
        title.set(image.getTitle());
        author.set(image.getArtist());
        imageUrl.set(image.getDisplayByType(Image.DisplaySizeType.PREVIEW).getUri());
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView imageView, String url) {
        Picasso.with(imageView.getContext()).load(url).into(imageView);
    }

}
