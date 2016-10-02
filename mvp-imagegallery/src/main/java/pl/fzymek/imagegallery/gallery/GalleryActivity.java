package pl.fzymek.imagegallery.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.View;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.fzymek.imagegallery.R;
import pl.fzymek.imagegallery.details.DetailsActivity;
import pl.fzymek.imagegallery.model.gettyimages.Image;
import pl.fzymek.imagegallery.views.SpaceDecoration;
import timber.log.Timber;

public class GalleryActivity extends MvpLceViewStateActivity<SwipeRefreshLayout, List<Image>, GalleryView, GalleryPresenter> implements GalleryView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    LinearLayoutManager layoutManager;
    GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Timber.uprootAll();
        Timber.plant(new Timber.DebugTree());
        ButterKnife.bind(this);
        contentView.setOnRefreshListener(this);

//        getWindow().setExitTransition(new Explode());

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceDecoration());

        adapter = new GalleryAdapter();
        adapter.setItemClickListener((view, item) -> {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("item", item);
//            Toast.makeText(this, ""+item, Toast.LENGTH_SHORT).show();
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, new Pair(view.findViewById(R.id.image), "transition_" + item.getId()));
            startActivity(intent, optionsCompat.toBundle());
        });
        recyclerView.setAdapter(adapter);
        setRetainInstance(true);
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
        Timber.d("getErrorMessage");
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

        String[] data = {
            "hot girls", "kitten", "puppy", "panda", "nightmare", "slam dunk", "beach", "sunrise", "party"
        };
        presenter.loadData(data[new Random().nextInt(data.length)], pullToRefresh);
    }

    @Override
    public void showContent() {
        super.showContent();
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
