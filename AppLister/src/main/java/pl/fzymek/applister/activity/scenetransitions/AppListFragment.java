package pl.fzymek.applister.activity.scenetransitions;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.fzymek.applister.R;
import pl.fzymek.applister.activity.AppListAdapter;
import pl.fzymek.applister.controller.AppListController;
import pl.fzymek.applister.ui.AppListUI;
import timber.log.Timber;

/**
 * Created by filip on 13.09.2016.
 */
public class AppListFragment extends Fragment implements AppListUI, BackListener {

    AppListController controller;
    Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.list)
    RecyclerView recyclerView;

    LinearLayoutManager layoutManager;
    AppListAdapter adapter;
    int cx, cy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new AppListController(getActivity());
        controller.setUi(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        Timber.d("onCreateView");

        if (savedInstanceState == null) {
            createCircularReveal(view);
        }

        return view;
    }

    private void createCircularReveal(View view) {
        //noinspection ConstantConditions
        if (getArguments().containsKey("cX") && getArguments().containsKey("cY")) {
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    //noinspection ConstantConditions
                    v.removeOnLayoutChangeListener(this);

                    cx = getArguments().getInt("cX");
                    cy = getArguments().getInt("cY");
                    getArguments().remove("cX");
                    getArguments().remove("cY");
                    float radius = (float) Math.hypot(getView().getWidth(), getView().getHeight());

                    Animator circularReveal = ViewAnimationUtils.createCircularReveal(getView(), cx, cy, 0, radius);
                    circularReveal.setDuration(600);
                    circularReveal.start();

                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        Timber.d("onDestroyView");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        Timber.d("onViewCreated");

        getAppCompatActivity().setSupportActionBar(toolbar);
        getAppCompatActivity().getSupportActionBar().setTitle(R.string.app_name);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());

        PackageManager packageManager = getActivity().getPackageManager();
        adapter = new AppListAdapter(packageManager);
        adapter.setOnItemClickListener((view, info) -> {
            AppDetailsFragment fragment = new AppDetailsFragment();
            Bundle args = new Bundle();
            args.putParcelable(AppDetailsFragment.INFO, info);
            fragment.setArguments(args);


            View icon = view.findViewById(android.R.id.icon);
            View text = view.findViewById(android.R.id.text1);
            ViewCompat.setTransitionName(icon, "icon");
            ViewCompat.setTransitionName(text, "text");

            Transition transition = TransitionInflater.from(view.getContext()).inflateTransition(R.transition.open_details_shared_fragment);
            fragment.setSharedElementEnterTransition(transition);
            fragment.setSharedElementReturnTransition(transition);

            getAppCompatActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.root, fragment)
                    .addSharedElement(icon, "icon")
                    .addSharedElement(text, "text")
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume");
        controller.fetchData();
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("onPause");
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
    }

    @Override
    public void onNextApp(ResolveInfo info) {
        Timber.d("onNextApp: %s", info);
        adapter.add(info);
    }

    private AppCompatActivity getAppCompatActivity() {
        return (AppCompatActivity) getActivity();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed(Runnable action) {
        if (cx != 0 && cy != 0) {
            float radius = (float) Math.hypot(getView().getWidth(), getView().getHeight());

            Animator circularReveal = ViewAnimationUtils.createCircularReveal(getView(), cx, cy, radius, 0);
            circularReveal.setDuration(600);
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    getView().setVisibility(View.GONE);
                    getView().post(action);
                }
            });
            circularReveal.start();
        } else {
            getView().post(action);
        }
    }
}
