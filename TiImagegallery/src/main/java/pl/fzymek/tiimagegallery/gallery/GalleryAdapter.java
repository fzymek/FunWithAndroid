package pl.fzymek.tiimagegallery.gallery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;
import pl.fzymek.gettyimagesmodel.gettyimages.Image;
import pl.fzymek.tiimagegallery.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClicked(View view, Image image);
    }

    List<Image> data;
    OnItemClickListener listener;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

    GalleryAdapter() {
    }

    Image getItem(int position) {
        return data.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
    }

    public void setData(List<Image> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
