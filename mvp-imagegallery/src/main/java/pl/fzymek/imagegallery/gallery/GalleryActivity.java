package pl.fzymek.imagegallery.gallery;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.fzymek.imagegallery.model.SearchResponse;
import pl.fzymek.mvp_catgallery.R;
import timber.log.Timber;

public class GalleryActivity extends MvpLceActivity<SwipeRefreshLayout, SearchResponse, GalleryView, GalleryPresenter> implements GalleryView, SwipeRefreshLayout.OnRefreshListener {

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

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int spacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, getResources().getDisplayMetrics());
                if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = spacing;
                }
                outRect.left = spacing;
                outRect.right = spacing;
            }
        });

        adapter = new GalleryAdapter();
        recyclerView.setAdapter(adapter);
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
        return null;
    }

    @Override
    public void setData(SearchResponse data) {
        Timber.d("setData: %s", data);
        adapter.setData(data.getHits());
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        Timber.d("loadData: %b", pullToRefresh);
        presenter.loadData("panda", pullToRefresh);
    }

    @Override
    public void onRefresh() {
        Timber.d("onRefresh");
        loadData(true);
    }

}
