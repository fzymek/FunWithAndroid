package pl.fzymek.imagegallery.gallery;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.fzymek.gettyimagesmodel.gettyimages.Image;
import pl.fzymek.imagegallery.R;
import pl.fzymek.imagegallery.config.Config;
import pl.fzymek.imagegallery.details.DetailsActivity;
import pl.fzymek.imagegallery.views.SpaceDecoration;
import timber.log.Timber;

public class GalleryActivity extends MvpLceViewStateActivity<SwipeRefreshLayout, List<Image>, GalleryView, GalleryPresenter>
        implements GalleryView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    RecyclerView.LayoutManager layoutManager;
    GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Timber.uprootAll();
        Timber.plant(new Timber.DebugTree());
        ButterKnife.bind(this);

        setUpToolbar();

        contentView.setOnRefreshListener(this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(this);
        } else {
            layoutManager = new GridLayoutManager(this, 3);
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceDecoration());

        adapter = new GalleryAdapter(true, R.layout.hit_card);
        adapter.setItemClickListener((view, item) -> {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("item", item);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, new Pair(view.findViewById(R.id.image), "transition_" + item.getId()));
            startActivity(intent, optionsCompat.toBundle());
        });
        recyclerView.setAdapter(adapter);
        setRetainInstance(true);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onNewViewStateInstance() {
        Timber.d("onNewViewStateInstance");
        loadData(false);
    }

    @NonNull
    @Override
    public GalleryPresenter createPresenter() {
        Timber.d("createPresenter");
        return new GalleryPresenter();
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        Timber.d("getErrorMessage %s", e.getMessage());
        return e.getMessage();
    }

    @Override
    public void setData(List<Image> data) {
        Timber.d("setData: %s", data);
        adapter.setData(data);
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        Timber.d("loadData: %b", pullToRefresh);
        presenter.loadData(Config.QUERIES[new Random().nextInt(Config.QUERIES.length)], pullToRefresh);
    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        contentView.setRefreshing(false);
    }

    @Override
    public LceViewState<List<Image>, GalleryView> createViewState() {
        return new RetainingLceViewState<>();
    }

    @Override
    public List<Image> getData() {
        return adapter.getData();
    }

    @Override
    public void onRefresh() {
        Timber.d("onRefresh");
        loadData(true);
    }

}
