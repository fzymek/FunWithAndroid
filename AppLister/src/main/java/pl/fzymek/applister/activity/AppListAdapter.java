package pl.fzymek.applister.activity;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.fzymek.applister.R;
import timber.log.Timber;

/**
 * Created by filip on 29.08.2016.
 */
public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppDataVH> {

    final List<ResolveInfo> apps = new ArrayList<>();
    private final PackageManager pm;
    private OnItemClickListener onItemClickListener;

    public AppListAdapter(PackageManager pm) {
        this.pm = pm;
    }

    @Override
    public AppDataVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_card_item, parent, false);
        return new AppDataVH(v);
    }

    @Override
    public void onBindViewHolder(AppDataVH holder, int position) {
        Timber.d("onBindViewHolder: %s, %s", holder, position);
        ResolveInfo info = apps.get(position);
        Timber.d("Info: %s", info);
        holder.icon.setImageDrawable(info.loadIcon(pm));
        holder.text.setText(info.loadLabel(pm));
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public void add(ResolveInfo info) {
        apps.add(info);
        int size = apps.size();
        notifyItemInserted(size);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class AppDataVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(android.R.id.icon)
        ImageView icon;
        @BindView(android.R.id.text1)
        TextView text;

        public AppDataVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClicked(view, apps.get(getAdapterPosition()));
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, ResolveInfo info);
    }
}
