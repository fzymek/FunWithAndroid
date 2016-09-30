package pl.fzymek.imagegallery.gallery;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.fzymek.imagegallery.model.gettyimages.Image;
import pl.fzymek.mvp_catgallery.R;
import timber.log.Timber;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card)
        CardView card;
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.artist)
        TextView artist;
        @BindView(R.id.author_content)
        LinearLayout authorConetnt;

        public GalleryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    List<Image> data = new ArrayList<>();

    public void setData(List<Image> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.d("onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hit_card, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {
        Image image = getItem(position);
        String uri = image.getDisplayByType(Image.DisplaySizeType.PREVIEW).getUri();

        Timber.d("onBindViewHolder %s", image);

        holder.title.setText(image.getTitle());
        holder.artist.setText(image.getArtist());
        Picasso.with(holder.image.getContext()).load(uri).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    Image getItem(int position) {
        return data.get(position);
    }

    public List<Image> getData() {
        return data;
    }
}
