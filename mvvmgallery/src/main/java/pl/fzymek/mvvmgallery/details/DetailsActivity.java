package pl.fzymek.mvvmgallery.details;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import pl.fzymek.gettyimagesmodel.gettyimages.Image;
import pl.fzymek.mvvmgallery.R;
import pl.fzymek.mvvmgallery.databinding.ActivityDetailsBinding;
import pl.fzymek.mvvmgallery.viewmodel.ImageViewModel;

public class DetailsActivity extends AppCompatActivity {

    ActivityDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        setupToolbar();

        Image image = getIntent().getParcelableExtra("image");
        binding.setVariable(pl.fzymek.mvvmgallery.BR.image, new ImageViewModel(image));
        binding.executePendingBindings();

    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
