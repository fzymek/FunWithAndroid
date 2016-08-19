package pl.fzymek.permissions.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.fzymek.permissions.R;
import timber.log.Timber;

public class TakePictureActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    @BindView(R.id.camera_surface)
    SurfaceView cameraSurface;

    @BindView(R.id.take_picture_fab)
    FloatingActionButton takePicture;

    EasyCamera camera;
    EasyCamera.CameraActions cameraActions;
    Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        unbinder = ButterKnife.bind(this);

        takePicture.setOnClickListener(view -> {
            cameraActions.takePicture(EasyCamera.Callbacks.create().withRestartPreviewAfterCallbacks(true)
                    .withJpegCallback(
                            (bytes, camAction) -> {
                                File picture = savePicture(bytes);

                                MediaScannerConnection.scanFile(TakePictureActivity.this, new String[]{picture.getAbsolutePath()}, new String[]{"image/jpg"}, (s, uri) -> {
                                    Timber.d("saved path: " + s + " as uri: " + uri);

                                    Snackbar snackbar = Snackbar.make(view, "" +
                                            "Picture saved", Snackbar.LENGTH_SHORT);

                                    Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                                    viewIntent.setDataAndType(uri, "image/*");

                                    List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(viewIntent, PackageManager.MATCH_DEFAULT_ONLY);
                                    if (resolveInfos.size() > 0) {
                                        snackbar.setAction("Show", v -> {
                                            startActivity(viewIntent);
                                        });
                                    }
                                    snackbar.show();
                                });

                            }));
        });
        initCamera();
    }

    @NonNull
    private File savePicture(byte[] bytes) {
        File picsDir = new File(Environment.getExternalStorageDirectory(), "PermissionsApp");
        if (!picsDir.exists()) {
            picsDir.mkdirs();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        File picture = new File(picsDir, "Pic_" + dateFormat.format(new Date()) + ".jpg");

        try {
            FileOutputStream os = new FileOutputStream(picture);
            os.write(bytes);
            os.close();
        } catch (IOException e) {
            Timber.e("Error saving picture");
            e.printStackTrace();
        }
        return picture;
    }

    private void initCamera() {
        Timber.d("Opening camera");
        if (camera == null) {
            camera = DefaultEasyCamera.open();
            camera.alignCameraAndDisplayOrientation(getWindowManager());
        }
        Timber.d("Camera: " + camera);
        cameraSurface.getHolder().addCallback(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSurface.getHolder().removeCallback(this);
        if (camera != null) {
            camera.stopPreview();
            camera.close();
            camera = null;
        }
        unbinder.unbind();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Timber.d("Surface created: " + surfaceHolder + ", " + this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Timber.d("Surface changed, starting preview: " + surfaceHolder + ", " + this);

        try {
            cameraActions = camera.startPreview(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Timber.d("Surface destroyed, stopping preview: " + surfaceHolder + ", " + this);
        camera.stopPreview();
    }
}
