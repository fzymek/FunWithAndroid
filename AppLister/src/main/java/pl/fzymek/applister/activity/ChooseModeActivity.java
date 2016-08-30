package pl.fzymek.applister.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.fzymek.applister.R;
import pl.fzymek.applister.activity.transitions.AppListActivity;

public class ChooseModeActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mode);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

    }

    @OnClick(R.id.use_transitions_btn)
    void onUseTransitionsClick(View v) {
        startActivity(new Intent(this, AppListActivity.class));
    }


    @OnClick(R.id.use_scene_transitions_btn)
    void onUseSceneTransitionsClick(View v) {
        startActivity(new Intent(this, AppListActivity.class));
    }
}
