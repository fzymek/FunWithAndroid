package pl.fzymek.applister.activity.transitions;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.fzymek.applister.R;
import pl.fzymek.applister.activity.AppListAdapter;
import pl.fzymek.applister.controller.AppListController;
import pl.fzymek.applister.ui.AppListUI;
import timber.log.Timber;

public class AppListActivity extends AppCompatActivity implements AppListUI {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.list)
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AppListAdapter adapter;
    Unbinder unbinder;
    AppListController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        initController();
    }

    private void initController() {
        controller = new AppListController(this);
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

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        adapter = new AppListAdapter(getPackageManager());
        adapter.setOnItemClickListener((view, info) -> {
            Intent details = new Intent(this, AppDetailsActivity.class);

            ImageView icon = ButterKnife.findById(view, android.R.id.icon);
//            TextView text = ButterKnife.findById(view, android.R.id.text1);

            int screenLocation[] = new int[2];
            icon.getLocationOnScreen(screenLocation);

            details.putExtra(AppDetailsActivity.PACKAGE_NAME, info.activityInfo.packageName);
            details.putExtra(AppDetailsActivity.ORIENTATION, getResources().getConfiguration().orientation);
            details.putExtra(AppDetailsActivity.LEFT, screenLocation[0]);
            details.putExtra(AppDetailsActivity.TOP, screenLocation[1]);
            details.putExtra(AppDetailsActivity.INFO , info);

            startActivity(details);
            overridePendingTransition(0, 0);
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }
}
