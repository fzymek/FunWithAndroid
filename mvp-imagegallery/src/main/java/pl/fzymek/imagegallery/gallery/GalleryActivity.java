package pl.fzymek.imagegallery.gallery;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceActivity;

import butterknife.BindView;
import pl.fzymek.imagegallery.model.SearchResponse;
import pl.fzymek.mvp_catgallery.R;

public class GalleryActivity extends MvpLceActivity<SwipeRefreshLayout, SearchResponse, GalleryView, GalleryPresenter> {

    @BindView(R.id.loadingView)
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GalleryAdapter();
        recyclerView.setAdapter(adapter);
    }

    @NonNull
    @Override
    public GalleryPresenter createPresenter() {
        return new GalleryPresenter();
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return null;
    }

    @Override
    public void setData(SearchResponse data) {
        adapter.setData(data.getHits());
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadData("panda");
    }
}
