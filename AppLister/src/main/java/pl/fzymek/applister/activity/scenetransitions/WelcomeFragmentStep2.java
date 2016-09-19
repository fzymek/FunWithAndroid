package pl.fzymek.applister.activity.scenetransitions;


import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.fzymek.applister.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragmentStep2 extends Fragment {

    Unbinder unbinder;

    @BindView(R.id.next)
    Button next;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.welcome_fragment_scene2, container, false);
        unbinder = ButterKnife.bind(this, view);

        //will be used for shared element transition
        ViewCompat.setTransitionName(next, "next");

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.next)
    protected void onGoClicked(View view) {

        AccelerateInterpolator interpolator = new AccelerateInterpolator(1.5f);
        Slide right = new Slide(Gravity.END);
        right.setInterpolator(interpolator);
        right.addTarget(R.id.text);

        Slide left = new Slide(Gravity.START);
        left.setInterpolator(interpolator);
        left.addTarget(R.id.icon);

        TransitionSet set = new TransitionSet();
        set.setOrdering(TransitionSet.ORDERING_TOGETHER);
        set.addTransition(right).addTransition(left);
        setExitTransition(set);


        AppListFragment fragment = new AppListFragment();
        Bundle args = new Bundle();
        int cX = (int) (view.getX() + view.getWidth() / 2);
        int cY = (int) (view.getY() + view.getHeight() / 2);
        args.putInt("cX", cX);
        args.putInt("cY", cY);
        fragment.setArguments(args);
        fragment.setEnterTransition(new Fade());
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.root, fragment)
                .addToBackStack(null)
                .commit();
    }
}
