package pl.fzymek.applister.activity.scenetransitions;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.fzymek.applister.R;
import pl.fzymek.applister.activity.AppListAdapter;
import pl.fzymek.applister.controller.AppListController;
import pl.fzymek.applister.ui.AppListUI;

/**
 * Created by filip on 13.09.2016.
 */
public class AppListFragment extends Fragment implements AppListUI {

    AppListController controller;
    Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.list)
    RecyclerView recyclerView;

    LinearLayoutManager layoutManager;
    AppListAdapter adapter;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnterTransition(new Slide());
        setEnterTransition(new Slide());
        controller = new AppListController(getActivity());
        controller.setUi(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        getAppCompatActivity().setSupportActionBar(toolbar);
        getAppCompatActivity().getSupportActionBar().setTitle(R.string.app_name);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());

        PackageManager packageManager = getActivity().getPackageManager();
        adapter = new AppListAdapter(packageManager);
        adapter.setOnItemClickListener((view, info) -> {


        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        controller.fetchData();
    }

    @Override
    public void onLoadingStarted() {

    }

    @Override
    public void onLoadingFinished() {

    }

    @Override
    public void onError(Throwable error) {

    }

    @Override
    public void onNextApp(ResolveInfo info) {
        adapter.add(info);
    }

    private AppCompatActivity getAppCompatActivity() {
        return (AppCompatActivity) getActivity();
    }
}
