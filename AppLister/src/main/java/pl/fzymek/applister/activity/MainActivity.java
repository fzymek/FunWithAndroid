package pl.fzymek.applister.activity;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.fzymek.applister.R;
import pl.fzymek.applister.controller.MainActivityController;
import pl.fzymek.applister.ui.MainActivityUI;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements MainActivityUI {

    @BindView(R.id.list)
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AppListAdapter adapter;

    Unbinder unbinder;
    MainActivityController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        initController();
    }

    private void initController() {
        controller = new MainActivityController(this);
        controller.setUi(this);
        controller.fetchData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onLoadingStarted() {
        Timber.d("onLoadingStarted");
    }

    @Override
    public void onLoadingFinished() {
        Timber.d("onLoadingFinished");
    }

    @Override
    public void onError(Throwable error) {
        Timber.e(error);
        Snackbar.make(recyclerView.getRootView(), "Error happened :(", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onNextApp(ResolveInfo info) {
        Timber.d("onNextApp: %s", info);
        adapter.add(info);
    }

    private void bindViews() {
        unbinder = ButterKnife.bind(this);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new AppListAdapter(getPackageManager());
        adapter.setOnItemClickListener((info -> {
            Intent details = new Intent(this, InstalledAppDetailsActivity.class);
            details.putExtra(InstalledAppDetailsActivity.APP_PACKAGE_NAME, info.activityInfo.packageName);
            startActivity(details);
        }));

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }
}
