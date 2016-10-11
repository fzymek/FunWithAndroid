package pl.fzymek.tiimagegallery.details;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.grandcentrix.thirtyinch.TiFragment;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.fzymek.gettyimagesmodel.gettyimages.Image;
import pl.fzymek.tiimagegallery.R;


public class DetailsFragment extends TiFragment<DetailsPresenter, DetailsView> implements DetailsView {

    final static String IMAGE_ARG = "image";

    @BindView(R.id.image)
    ImageView image;
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

    public static DetailsFragment newInstance(Image img) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(IMAGE_ARG, img);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    private void setUpToolbar() {
        getAppCompatActivity().setSupportActionBar(toolbar);
        getAppCompatActivity().getSupportActionBar().setHomeButtonEnabled(false);
        getAppCompatActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setUpToolbar();
    }

    AppCompatActivity getAppCompatActivity() {
        return (AppCompatActivity) getActivity();
    }

    @NonNull
    @Override
    public DetailsPresenter providePresenter() {
        return new DetailsPresenter();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().loadDetails(getArguments());
    }

    @Override
    public void showImage(Image img) {
        collapsingToolbar.setTitle(img.getTitle());

        //setup transition
        ViewCompat.setTransitionName(image, "transition_"+img.getId());
        //load image
        Glide.with(getView().getContext()).load(img.getDisplayByType(Image.DisplaySizeType.PREVIEW).getUri()).into(image);

        //set data
        artist.setText(img.getArtist());
        dateCreated.setText(img.getDateCreated());
        String tmp[] = new String[15];
        Arrays.fill(tmp, img.getCaption());
        description.setText(TextUtils.join(". ", tmp));

    }
}
