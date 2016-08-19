package pl.fzymek.permissions.activities;

import android.Manifest;
import android.content.DialogInterface;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.fzymek.permissions.R;
import timber.log.Timber;

public class TakePictureActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 0x1;

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

            if (cameraActions == null) {
                initCamera();
                return;
            }

            cameraActions.takePicture(EasyCamera.Callbacks.create().withRestartPreviewAfterCallbacks(true)
                    .withJpegCallback(
                            (bytes, camAction) -> {
                                Timber.d("Take picture clicked");
                                File picture = savePicture(bytes);

                                Timber.d("Adding taken picture to media collection");
                                MediaScannerConnection.scanFile(TakePictureActivity.this, new String[]{picture.getAbsolutePath()}, new String[]{"image/jpg"}, (s, uri) -> {
                                    Timber.d("Picture %s added to gallery under %s", s, uri);

                                    Snackbar snackbar = Snackbar.make(view, "" +
                                            "Picture saved", Snackbar.LENGTH_SHORT);

                                    Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                                    viewIntent.setDataAndType(uri, "image/*");

                                    List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(viewIntent, PackageManager.MATCH_DEFAULT_ONLY);
                                    if (resolveInfos.size() > 0) {
                                        snackbar.setAction("Show", v -> {
                                            Timber.d("Opening picture in gallery");
                                            startActivity(viewIntent);
                                        });
                                    }
                                    snackbar.show();
                                });

                            }));
        });

        initCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                handleCameraPermissionResult(permissions, grantResults);
                break;
        }
    }

    private void handleCameraPermissionResult(String[] permissions, int[] grantResults) {
        Timber.d("handleCameraPermissionResult() called with: permissions = [%s], grantResults = [%s]", Arrays.toString(permissions), Arrays.toString(grantResults));
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Timber.d("Permission granted");
            startCameraPreview();
        } else {
            Timber.d("Permission denied");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCameraPreview();
        unbinder.unbind();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
    }

    private void initCamera() {

        if (!hasPermission(Manifest.permission.CAMERA)) {
            requestPermission(Manifest.permission.CAMERA);
            return;
        }
        startCameraPreview();
    }

    private boolean hasPermission(@NonNull String permission) {
        Timber.d("Checking for permission: %s", permission);
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(@NonNull String permission) {
        Timber.d("Requesting permission: %s", permission);
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)) {
            //show permission explanation

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Camera needed")
                    .setMessage("Camera permission is needed in order to take pictures.")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            builder.show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void startCameraPreview() {
        Timber.d("Opening camera");
        if (camera == null) {
            camera = DefaultEasyCamera.open();
            camera.alignCameraAndDisplayOrientation(getWindowManager());
        }
        cameraSurface.getHolder().addCallback(this);
        try {
            cameraActions = camera.startPreview(cameraSurface.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopCameraPreview() {
        cameraSurface.getHolder().removeCallback(this);
        if (camera != null) {
            camera.stopPreview();
            camera.close();
            camera = null;
        }
    }

    @NonNull
    private File savePicture(byte[] bytes) {
        File picsDir = new File(Environment.getExternalStorageDirectory(), "PermissionsApp");
        if (!picsDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            Timber.d("Creating directory %s ", picsDir.getAbsolutePath());
            picsDir.mkdirs();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        File picture = new File(picsDir, "Pic_" + dateFormat.format(new Date()) + ".jpg");

        try {
            Timber.d("Saving file %s", picture.getAbsolutePath());
            FileOutputStream os = new FileOutputStream(picture);
            os.write(bytes);
            os.close();
        } catch (IOException e) {
            Timber.e("Error saving picture");
            e.printStackTrace();
        }

        return picture;
    }
}
