package pl.fzymek.imagegallery.gallery;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.fzymek.imagegallery.model.Hit;
import pl.fzymek.mvp_catgallery.R;
import timber.log.Timber;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    List<Hit> data = new ArrayList<>();

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        ImageView imageView;
        @BindView(R.id.title)
        TextView textView;

        public GalleryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setData(List<Hit> data) {
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
        Hit hit = getItem(position);
        Timber.d("onBindViewHolder() called with: holder = [%s], position = [%s]", holder, hit);
        holder.textView.setText("Likes: " + hit.getLikes());
        Picasso.with(holder.imageView.getContext()).load(hit.getPreviewURL()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    Hit getItem(int position) {
        return data.get(position);
    }
}
