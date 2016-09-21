package pl.fzymek.lottoboards.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import pl.fzymek.lottoboards.BuildConfig;
import pl.fzymek.lottoboards.R;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        setContentView(R.layout.activity_main);
    }
}
