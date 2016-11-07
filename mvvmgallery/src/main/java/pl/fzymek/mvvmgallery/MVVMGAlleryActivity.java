package pl.fzymek.mvvmgallery;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Random;

import pl.fzymek.gettyimagesmodel.gettyimages.GettySearchResult;
import pl.fzymek.mvvmgallery.config.Config;
import pl.fzymek.mvvmgallery.databinding.ActivityMainBinding;
import pl.fzymek.mvvmgallery.gallery.GalleryAdapter;
import pl.fzymek.mvvmgallery.network.GettyImagesService;
import pl.fzymek.mvvmgallery.viewmodel.GettySearchResultViewModel;
import pl.fzymek.mvvmgallery.views.SpaceDecoration;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MVVMGalleryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView.LayoutManager layoutManager;
    ActivityMainBinding viewDataBinding;
    ImageSearchObserver imageSearchObserver = new ImageSearchObserver();
    GettyImagesService imagesService;
    GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        configureTimber();
        createImageService();
        setupToolbar();
        setupRecycler();

        viewDataBinding.content.setOnRefreshListener(this);

        loadData(false);

    }

    private void setupRecycler() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(this);
        } else {
            layoutManager = new GridLayoutManager(this, 3);
        }

        adapter = new GalleryAdapter();
        viewDataBinding.recycler.setLayoutManager(layoutManager);
        viewDataBinding.recycler.addItemDecoration(new SpaceDecoration());
        viewDataBinding.recycler.setAdapter(adapter);
    }

    private void setupToolbar() {
        setSupportActionBar(viewDataBinding.toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void configureTimber() {
        Timber.uprootAll();
        Timber.plant(new Timber.DebugTree());
    }

    private void createImageService() {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Config.GETTYIMAGES_API_URL)
                .build();
        imagesService = retrofit.create(GettyImagesService.class);
    }

    private String getRandomPhrase() {
        String query = Config.QUERIES[new Random().nextInt(Config.QUERIES.length)];
        Timber.d("Using query '%s'", query);
        return query;
    }

    private class ImageSearchObserver implements Observer<GettySearchResult> {

        @Override
        public void onCompleted() {
            Timber.d("onCompleted");
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "Error while loading data");
            showError();
        }

        @Override
        public void onNext(GettySearchResult result) {
            Timber.d("onNext");
            GettySearchResultViewModel viewModel = new GettySearchResultViewModel(result);
            viewDataBinding.setVariable(BR.result, viewModel);
            viewDataBinding.executePendingBindings();
            showData();
        }

    }

    void showProgress() {
        Timber.d("showProgress");
        viewDataBinding.progress.setVisibility(View.VISIBLE);
        viewDataBinding.recycler.setVisibility(View.GONE);
        viewDataBinding.error.setVisibility(View.GONE);
        viewDataBinding.count.setVisibility(View.GONE);
    }

    void showData() {
        viewDataBinding.content.setRefreshing(false);
        viewDataBinding.recycler.setVisibility(View.VISIBLE);
        viewDataBinding.count.setVisibility(View.VISIBLE);
        viewDataBinding.progress.setVisibility(View.GONE);
        viewDataBinding.error.setVisibility(View.GONE);
    }

    void showError() {
        Timber.d("showError");
        viewDataBinding.content.setRefreshing(false);
        viewDataBinding.error.setVisibility(View.VISIBLE);
        viewDataBinding.recycler.setVisibility(View.GONE);
        viewDataBinding.progress.setVisibility(View.GONE);
        viewDataBinding.count.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    private void loadData(boolean pull2Refreh) {
        Timber.d("loadData(%b)", pull2Refreh);
        if (!pull2Refreh) {
            showProgress();
        }
        viewDataBinding.content.setRefreshing(pull2Refreh);

        Observable<GettySearchResult> searchResultObservable = imagesService.getImages(getRandomPhrase());
        imageSearchObserver = new ImageSearchObserver();
        searchResultObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageSearchObserver);
    }
}
