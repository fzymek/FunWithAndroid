package pl.fzymek.applister.activity.scenetransitions;


import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
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
public class WelcomeFragmentStep2 extends Fragment {

    Unbinder unbinder;

    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.next)
    Button next;

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
        AppListFragment fragment = new AppListFragment();
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.root, fragment)
                .addToBackStack(null)
                .commit();
    }
}
