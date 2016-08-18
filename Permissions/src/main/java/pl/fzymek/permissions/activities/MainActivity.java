package pl.fzymek.permissions.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import pl.fzymek.permissions.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupTakePictureButton();
    }

    private void setupTakePictureButton() {
        //noinspection ConstantConditions
        findViewById(R.id.take_picture).setOnClickListener(view -> takePicture());
    }

    private void takePicture() {
        Intent takePic = new Intent(this, TakePictureActivity.class);
        startActivity(takePic);
    }

}
