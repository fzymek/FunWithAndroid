package pl.fzymek.tiimagegallery.gallery;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.fzymek.gettyimagesmodel.gettyimages.Image;
import pl.fzymek.tiimagegallery.R;
import timber.log.Timber;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClicked(View view, Image image);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.card)
        CardView card;
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.title)
        TextView title;
        TextView artist;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onItemClicked(view, getItem(getAdapterPosition()));
            }
        }
    }
    private List<Image> data = Collections.emptyList();
    private OnItemClickListener listener;

    GalleryAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Image image = getItem(position);
        String uri = image.getDisplayByType(Image.DisplaySizeType.PREVIEW).getUri();

        Timber.d("onBindViewHolder %s", image);

        holder.title.setText(image.getTitle());
        String artist = image.getArtist();
        View authorContent = holder.itemView.findViewById(R.id.author_content);
        authorContent.setVisibility(View.VISIBLE);
        TextView author = (TextView) authorContent.findViewById(R.id.artist);
        author.setText(artist);

        Glide.with(holder.image.getContext())
                .load(uri)
                .centerCrop()
                .crossFade()
                .into(holder.image);
    }

    Image getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<Image> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
