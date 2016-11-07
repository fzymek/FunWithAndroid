package pl.fzymek.mvvmgallery.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.support.design.widget.CollapsingToolbarLayout;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Arrays;

import pl.fzymek.gettyimagesmodel.gettyimages.Image;
import pl.fzymek.mvvmgallery.R;

public class ImageViewModel {

    public final ObservableField<String> description = new ObservableField<>();
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> author = new ObservableField<>();
    public final ObservableField<String> imageUrl = new ObservableField<>();
    public final ObservableField<String> dateCreated = new ObservableField<>();

    private Image data;

    public ImageViewModel(Image image) {
        this.data = image;
        description.set(image.getCaption());
        title.set(image.getTitle());
        author.set(image.getArtist());
        dateCreated.set(image.getDateCreated());
        imageUrl.set(image.getDisplayByType(Image.DisplaySizeType.PREVIEW).getUri());
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView imageView, String url) {
        Picasso.with(imageView.getContext()).load(url).into(imageView);
    }

    @BindingAdapter({"bind:collapsingText"})
    public static void setCollapsingText(CollapsingToolbarLayout collapsingToolbarLayout, String text) {
        collapsingToolbarLayout.setTitle(text);
    }

    @BindingAdapter({"bind:description"})
    public static void setDescriptions(TextView textView, String text) {
        //make description longer
        String desc;
        if (text == null) {
            desc = textView.getContext().getString(R.string.large_text);
        } else {
            String tmp[] = new String[15];
            Arrays.fill(tmp, text);
            desc = TextUtils.join(" ", tmp);
        }
        textView.setText(desc);
    }

    public Image getData() {
        return data;
    }
}
