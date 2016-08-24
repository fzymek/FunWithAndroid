package pl.fzymek.permissions.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.fzymek.permissions.R;
import pl.fzymek.permissions.utils.BitmapSaver;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final int TAKE_PICTURE = 0x1;
    private MultiplePermissionsListener anyPermissionDeniedListener;
    private MultiplePermissionsListener permissionListener;

    @BindView(R.id.content)
    View content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        configureDexterListeners();
        setupTakePictureButton();
    }

    private void setupTakePictureButton() {
        //noinspection ConstantConditions
        findViewById(R.id.take_picture_in_app).setOnClickListener(view -> takePictureInApp());
        //noinspection ConstantConditions
        findViewById(R.id.take_picture_via_intent).setOnClickListener(view -> takePictureViaIntent());
    }

    private void takePictureInApp() {
        Intent takePic = new Intent(this, TakePictureActivity.class);
        startActivity(takePic);
    }

    private void takePictureViaIntent() {
        if (Dexter.isRequestOngoing()) {
            return;
        }
        CompositeMultiplePermissionsListener listener = new CompositeMultiplePermissionsListener(permissionListener, anyPermissionDeniedListener);
        Dexter.checkPermissions(listener, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void requestPicture() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicture, TAKE_PICTURE, null);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Cannot take picture")
                    .setMessage("There is no app capable of taking picture.")
                    .setNegativeButton("Cancel", ((dialogInterface, i) -> dialogInterface.dismiss()))
                    .setPositiveButton("Go To Store", (((dialogInterface, i) -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://search?q=camera&c=apps"));
                        startActivity(intent);
                    })));
            builder.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                handleTakePicture(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void handleTakePicture(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap picture = (Bitmap) extras.get("data");
            new BitmapSaver().save(picture).subscribe(this::notifyPictureSaved);
        }
    }

    private void notifyPictureSaved(File picture) {
        Timber.d("notifyPictureSaved");

        Timber.d("Adding taken picture to media collection");
        MediaScannerConnection.scanFile(this, new String[]{picture.getAbsolutePath()}, new String[]{"image/jpg"}, (s, uri) -> {
            Timber.d("Picture %s added to gallery under %s", s, uri);

            Snackbar snackbar = Snackbar.make(content, "" +
                    "Picture saved", Snackbar.LENGTH_SHORT);

            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setDataAndType(uri, "image/*");

            List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(viewIntent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfos.size() > 0) {
                snackbar.setAction("Show", v -> {
                    Timber.d("Opening picture in gallery");
                    if (viewIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(viewIntent);
                    } else {
                        Timber.d("No gallery app");
                    }
                });
            }
            snackbar.show();
        });

    }

    private void configureDexterListeners() {
        permissionListener = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                Timber.d("Accepted: %s", report.getGrantedPermissionResponses());
                Timber.d("Denied: %s", report.getDeniedPermissionResponses());

                if (report.areAllPermissionsGranted()) {
                    Timber.d("All granted, requesting");
                    requestPicture();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                Timber.d("should show rationale");
                token.continuePermissionRequest();
            }
        };
        anyPermissionDeniedListener = DialogOnAnyDeniedMultiplePermissionsListener.Builder
                .withContext(this)
                .withTitle("Camera & file permissions")
                .withMessage("Camera & file permissions are needed for saving pictures")
                .withButtonText("Ok")
                .build();
        Dexter.continuePendingRequestsIfPossible(anyPermissionDeniedListener);
    }


}
