package pl.fzymek.tiimagegallery.gallery;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import pl.fzymek.tiimagegallery.details.DetailsFragment;
import pl.fzymek.tiimagegallery.util.ImageTransition;
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
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    GalleryAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new GalleryAdapter();
        adapter.setListener((v, image) -> {
            Timber.d("clicked: %s", v);
            openDetails(v, image);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
        contentView.setOnRefreshListener(this);

        Context context = view.getContext();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(context);
        } else {
            layoutManager = new GridLayoutManager(context, 3);
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceDecoration());
        recyclerView.setAdapter(adapter);
    }

    void openDetails(View view, Image image) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DetailsFragment fragment = DetailsFragment.newInstance(image);
        ImageTransition sharedElementEnterTransition = new ImageTransition();
        fragment.setSharedElementEnterTransition(sharedElementEnterTransition);
//        setSharedElementReturnTransition(sharedElementEnterTransition);
        fm.beginTransaction()
                .replace(R.id.content, fragment)
                .addSharedElement(view.findViewById(R.id.image), "transition_"+image.getId())
                .addToBackStack(null)
                .commit();

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

