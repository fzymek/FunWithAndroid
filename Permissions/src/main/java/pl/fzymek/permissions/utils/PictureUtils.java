package pl.fzymek.permissions.utils;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by filip on 24.08.2016.
 */
public class PictureUtils {


    @NonNull
    public static File createPictureFile(File picsDir) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        return new File(picsDir, "Pic_" + dateFormat.format(new Date()) + ".jpg");
    }

    @NonNull
    public static File createPictureDirectory() {
        File picsDir = new File(Environment.getExternalStorageDirectory(), "PermissionsApp");
        if (!picsDir.exists()) {
            Timber.d("Creating directory %s ", picsDir.getAbsolutePath());
            //noinspection ResultOfMethodCallIgnored
            picsDir.mkdirs();
        }
        return picsDir;
    }
}
