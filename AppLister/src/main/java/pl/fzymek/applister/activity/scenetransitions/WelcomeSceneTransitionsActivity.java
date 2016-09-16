package pl.fzymek.applister.activity.scenetransitions;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;

import pl.fzymek.applister.R;

/**
 * Created by filip on 08.09.2016.
 */
public class WelcomeSceneTransitionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.activity_welcome_scene_transitions);

        if (savedInstanceState == null) {
            Fragment fragment = new WelcomeFragmentStep1();
            getFragmentManager().beginTransaction()
                    .replace(R.id.root, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
