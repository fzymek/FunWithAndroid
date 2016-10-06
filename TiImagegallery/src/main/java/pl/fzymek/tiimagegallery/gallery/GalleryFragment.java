package pl.fzymek.tiimagegallery.gallery;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.grandcentrix.thirtyinch.TiFragment;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.fzymek.gettyimagesmodel.gettyimages.Image;
import pl.fzymek.tiimagegallery.R;
import pl.fzymek.tiimagegallery.config.Config;
import pl.fzymek.tiimagegallery.util.SpaceDecoration;
import timber.log.Timber;

public class GalleryFragment extends TiFragment<GalleryPresenter, GalleryView> implements GalleryView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.error)
    TextView error;
    @BindView(R.id.contentView)
    SwipeRefreshLayout contentView;

    GalleryAdapter adapter;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
        contentView.setOnRefreshListener(this);

        Context context = view.getContext();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        }
        adapter = new GalleryAdapter();
        recyclerView.addItemDecoration(new SpaceDecoration());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(false);
    }

    @NonNull
    @Override
    public GalleryPresenter providePresenter() {
        Timber.d("providePresenter");
        return new GalleryPresenter();
    }

    @Override
    public void startLoading(boolean pullToRefresh) {
        progressBar.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        contentView.setRefreshing(pullToRefresh);
    }

    @Override
    public void stopLoading() {
        contentView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        contentView.setRefreshing(false);
    }

    @Override
    public void showError(Throwable err) {
        error.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        contentView.setRefreshing(false);
    }

    @Override
    public void showGallery(List<Image> images) {
        adapter.setData(images);
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    private void loadData(boolean pullToRefresh) {
        String phrase = Config.QUERIES[new Random().nextInt(Config.QUERIES.length)];
        getPresenter().loadData(phrase, pullToRefresh);
    }
}

