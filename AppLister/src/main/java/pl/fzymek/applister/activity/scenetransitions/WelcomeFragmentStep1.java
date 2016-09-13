package pl.fzymek.applister.activity.scenetransitions;


import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.transition.ArcMotion;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pl.fzymek.applister.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragmentStep1 extends Fragment {

    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.next)
    Button next;
    Unbinder unbinder;

    public WelcomeFragmentStep1() {
        // Required empty public constructor
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExitTransition(new Fade());
        setReenterTransition(new Slide());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.welcome_fragment_scene1, container, false);
        unbinder = ButterKnife.bind(this, view);

        //set transition names for views
        ViewCompat.setTransitionName(text, "text");
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
        WelcomeFragmentStep2 fragment = new WelcomeFragmentStep2();
        Fade transition = new Fade();
        transition.setStartDelay(300);
        fragment.setEnterTransition(transition);
        fragment.setExitTransition(new Slide());

        ChangeBounds changeBounds = (ChangeBounds) TransitionInflater.from(getActivity()).inflateTransition(R.transition.next_transition);
        changeBounds.setPathMotion(new ArcMotion());

        TransitionSet set = new TransitionSet();
        set.addTransition(changeBounds);
        set.addTransition(new Fade());
        fragment.setSharedElementEnterTransition(set);

        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.root, fragment)
                .addSharedElement(next, "next") //add shared elements for transaction
                .addSharedElement(text, "text")
                .addToBackStack(null)
                .commit();
    }

}
