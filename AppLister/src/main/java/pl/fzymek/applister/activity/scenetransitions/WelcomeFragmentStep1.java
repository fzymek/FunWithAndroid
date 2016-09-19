package pl.fzymek.applister.activity.scenetransitions;


import android.annotation.TargetApi;
import android.app.Fragment;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.PathMotion;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.fzymek.applister.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragmentStep1 extends Fragment {

    @BindView(R.id.next)
    Button next;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.welcome_fragment_scene1, container, false);
        unbinder = ButterKnife.bind(this, view);

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
    public void onClickBegin(View view) {

        //set behavior for this fragment
        setExitTransition(new Slide(Gravity.START));
        setReenterTransition(new Slide(Gravity.TOP));

        WelcomeFragmentStep2 fragment = new WelcomeFragmentStep2();
        //set behavior for new fragment transitions
        fragment.setEnterTransition(new Fade());
        fragment.setSharedElementEnterTransition(getNextButtonTransition());

        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.root, fragment)
                .addSharedElement(next, "next")
                .addToBackStack(null)
                .commit();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ChangeBounds getNextButtonTransition() {
        ChangeBounds arcMoveTransition = new ChangeBounds();
        //create "zorro" like path
        arcMoveTransition.setPathMotion(new PathMotion() {
            @Override
            public Path getPath(float startX, float startY, float endX, float endY) {
                Path p = new Path();
                p.moveTo(startX, startY);
                p.lineTo(endX, startY);
                p.lineTo(startX, endY);
                p.lineTo(endX, endY);
                return p;
            }
        });
        arcMoveTransition.setDuration(500);
        arcMoveTransition.setInterpolator(new AccelerateDecelerateInterpolator());
        return arcMoveTransition;
    }

}
