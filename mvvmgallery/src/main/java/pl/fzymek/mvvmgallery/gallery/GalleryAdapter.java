package pl.fzymek.mvvmgallery.gallery;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pl.fzymek.gettyimagesmodel.gettyimages.Image;
import pl.fzymek.mvvmgallery.R;
import pl.fzymek.mvvmgallery.databinding.HitCardBinding;
import pl.fzymek.mvvmgallery.viewmodel.ImageViewModel;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryItemViewHolder> {

    public interface OnItemClickListener {
        void onClick(int position, Image data);
    }

    private List<Image> data = new ArrayList<>();
    OnItemClickListener listener;

    class GalleryItemViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder implements View.OnClickListener {
        final T binding;

        GalleryItemViewHolder(T binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(getAdapterPosition(), getItem(getAdapterPosition()).getData());
            }
        }
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public GalleryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HitCardBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.hit_card, parent, false);
        //noinspection unchecked
        return new GalleryItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(GalleryItemViewHolder holder, int position) {
        HitCardBinding binding = (HitCardBinding) holder.binding;
        binding.setVariable(pl.fzymek.mvvmgallery.BR.image, getItem(position));
        binding.executePendingBindings();
    }

    private ImageViewModel getItem(int position) {
        return new ImageViewModel(data.get(position));
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
