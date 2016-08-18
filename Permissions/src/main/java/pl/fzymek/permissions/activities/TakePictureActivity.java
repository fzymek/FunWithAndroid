package pl.fzymek.permissions.activities;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.fzymek.permissions.R;
import pl.fzymek.permissions.utils.SystemUI;
import pl.fzymek.permissions.views.CameraPreview;

public class TakePictureActivity extends AppCompatActivity {

    View mDecorView;
    @BindView(R.id.take_picture_btn)
    ImageButton takePictureBtn;
    @BindView(R.id.camera_surface)
    FrameLayout cameraView;
    Camera camera;
    CameraPreview cameraPreview;
    Unbinder viewUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        bindViews();
        SystemUI.hide(getWindow().getDecorView());

        setupCamera();
    }

    private void setupCamera() {
        releaseCameraAndPreview();
        camera = getCamera();

        cameraPreview = new CameraPreview(this, camera, cameraView);
        cameraView.addView(cameraPreview);
        cameraPreview.startPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCameraAndPreview();
        viewUnbinder.unbind();
    }

    private void releaseCameraAndPreview() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if (cameraPreview != null) {
            cameraPreview.destroyDrawingCache();
            cameraPreview.release();
        }
    }

    private void bindViews() {
        mDecorView = getWindow().getDecorView();
        viewUnbinder = ButterKnife.bind(this);
        takePictureBtn.setOnClickListener(view -> takePicture());
    }

    private void takePicture() {

    }

    private Camera getCamera() {
        if (camera == null) {
            camera = Camera.open();
        }
        return camera;
    }

}
