package pl.fzymek.permissions.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaScannerConnection;
import android.os.Bundle;
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
import android.view.View;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.fzymek.permissions.R;
import timber.log.Timber;

import static pl.fzymek.permissions.utils.PictureUtils.createPictureDirectory;
import static pl.fzymek.permissions.utils.PictureUtils.createPictureFile;

public class TakePictureActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 0x1;

    @BindView(R.id.camera_surface)
    SurfaceView cameraSurface;

    @BindView(R.id.take_picture_fab)
    FloatingActionButton takePicture;

    EasyCamera camera;
    EasyCamera.CameraActions cameraActions;
    Unbinder unbinder;
    private PermissionListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        setContentView(R.layout.activity_take_picture);
        configureDexter();
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

                                if (Dexter.isRequestOngoing()) return;

                                listener = new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {
                                        Timber.d("Permission granted");
                                        File picture = savePicture(bytes);
                                        indexPictureInGallery(view, picture);
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {
                                        Timber.d("Permission denied");
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(TakePictureActivity.this)
                                                .setTitle("Permission request")
                                                .setMessage("We need your permission to save files")
                                                .setNeutralButton("OK", (dialogInterface, i) -> {
                                                    dialogInterface.dismiss();
                                                    token.continuePermissionRequest();
                                                });
                                        builder.show();
                                    }
                                };
                                Dexter.checkPermission(listener, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                            }));
        });

        initCamera();
    }

    private void configureDexter() {
        Dexter.continuePendingRequestIfPossible(listener);
    }

    private void indexPictureInGallery(View view, File picture) {
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Timber.d("onRequestPermissionsResult() called with: requestCode = [%d], permissions = [%s], grantResults = [%s]", requestCode, Arrays.toString(permissions), Arrays.toString(grantResults));
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                handleCameraPermissionResult(permissions, grantResults);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void handleCameraPermissionResult(String[] permissions, int[] grantResults) {
        Timber.d("handleCameraPermissionResult() called with: permissions = [%s], grantResults = [%s]", Arrays.toString(permissions), Arrays.toString(grantResults));
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Timber.d("Permission granted");
            openCamera();
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
        openCamera();
    }

    private boolean hasPermission(@NonNull String permission) {
        Timber.d("Checking for permission: %s", permission);
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(@NonNull String permission) {
        Timber.d("Requesting permission: %s", permission);
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            //show permission explanation
            Timber.d("Showing permissions rationale");
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Camera needed")
                    .setMessage("Camera permission is needed in order to take pictures.")
                    .setNeutralButton("Ok", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    }).setOnDismissListener((dialogInterface -> {
                        ActivityCompat.requestPermissions(this, new String[]{permission}, CAMERA_PERMISSION_REQUEST_CODE);
                    }));
            builder.show();
        } else {
            Timber.d("Requesting permissions");
            ActivityCompat.requestPermissions(this, new String[]{permission}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void openCamera() {
        Timber.d("Opening camera");
        if (camera == null) {
            camera = DefaultEasyCamera.open();
            camera.alignCameraAndDisplayOrientation(getWindowManager());
        }
        cameraSurface.getHolder().addCallback(this);
        startCameraPreview();

    }

    private void startCameraPreview() {
        try {
            Timber.d("Starting camera preview");
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
        File picsDir = createPictureDirectory();

        File picture = createPictureFile(picsDir);

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
