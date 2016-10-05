package pl.fzymek.imagegallery.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceActivity;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.fzymek.gettyimagesmodel.gettyimages.Image;
import pl.fzymek.imagegallery.R;
import pl.fzymek.imagegallery.config.Config;
import pl.fzymek.imagegallery.gallery.GalleryAdapter;
import pl.fzymek.imagegallery.views.SpaceDecoration;
import timber.log.Timber;


public class DetailsActivity extends MvpLceActivity<SwipeRefreshLayout, List<Image>, DetailsView, DetailsPresenter> implements DetailsView {

    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.artist)
    TextView artist;
    @BindView(R.id.date_created)
    TextView dateCreated;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.recyclerView)
    RecyclerView relatedImagesRecycler;
    LinearLayoutManager layoutManager;
    GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setUpToolbar();

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapter = new GalleryAdapter(false, R.layout.hit_card_related);
        relatedImagesRecycler.setLayoutManager(layoutManager);
        relatedImagesRecycler.setAdapter(adapter);
        relatedImagesRecycler.addItemDecoration(new SpaceDecoration());

        loadData(false);
    }

    @NonNull
    @Override
    public DetailsPresenter createPresenter() {
        return new DetailsPresenter();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showDetails(Image img) {
        Timber.d("showDetails: %s", img);
        ViewCompat.setTransitionName(image, "transition_" + img.getId());
        Picasso.with(this).load(img.getDisplayByType(Image.DisplaySizeType.PREVIEW).getUri()).into(image);
        collapsingToolbar.setTitle(img.getTitle());
        artist.setText(img.getArtist());
        dateCreated.setText(img.getDateCreated());

        //make description longer
        String caption = img.getCaption();
        String desc[] = new String[15];
        Arrays.fill(desc, caption);
        description.setText(TextUtils.join(" ", desc));
    }

    @Override
    public void showError(Throwable e) {
        Timber.d("showError");
        Toast.makeText(this, "Error!" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return e.getMessage();
    }

    @Override
    public void setData(List<Image> data) {
        Timber.d("setData %s", data);
        adapter.setData(data);
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        String query = Config.QUERIES[new Random().nextInt(Config.QUERIES.length)];
        Intent intent = getIntent();
        Timber.d("loadData (%s, %s)", intent, query);
        presenter.loadDetails(intent, query);
    }
}
