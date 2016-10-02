package pl.fzymek.imagegallery.details;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.fzymek.imagegallery.R;
import pl.fzymek.imagegallery.model.gettyimages.Image;


public class DetailsActivity extends MvpActivity<DetailsView, DetailsPresenter> implements DetailsView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
//    @BindView(R.id.image)
//    ImageView image;
//    @BindView(R.id.author)
//    TextView author;
//    @BindView(R.id.date)
//    TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setUpToolbar();
        presenter.loadDetails(getIntent());
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
//        ViewCompat.setTransitionName(image, "transition_" + img.getId());
//        Picasso.with(this).load(img.getDisplayByType(Image.DisplaySizeType.PREVIEW).getUri()).into(image);
//        author.setText(img.getArtist());
//        date.setText(img.getDateCreated());
    }

    @Override
    public void showError(Throwable e) {
        Toast.makeText(this, "Error!" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
