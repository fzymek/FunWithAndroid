package pl.fzymek.permissions.views;

import android.content.Context;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder surfaceHolder;
    Camera camera;
    Camera.Size previewSize;
    View cameraView;
    List<Camera.Size> supportedPreviewSizes;
    List<String> supportedFlashModes;

    public CameraPreview(Context context, Camera camera, View cameraView) {
        super(context);

        this.cameraView = cameraView;
        setCamera(camera);
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
        this.surfaceHolder.setKeepScreenOn(true);

    }

    public void startPreview() {
        try {
            this.camera.setPreviewDisplay(surfaceHolder);
            this.camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            this.camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (surfaceHolder.getSurface() == null) return;

        Camera.Parameters parameters = this.camera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        if (previewSize != null) {
            Camera.Size size = previewSize;
            parameters.setPreviewSize(previewSize.width, previewSize.height);
        }

        this.camera.setParameters(parameters);
        this.camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (this.camera != null) {
            this.camera.stopPreview();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int w = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int h = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(w, h);

        if (supportedPreviewSizes != null) {
            previewSize = getOptimalPreviewSize(supportedPreviewSizes, w, h);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        if (changed) {
            final int width = right - left;
            final int height = bottom - top;

            int previewWidth = width;
            int previewHeight = height;

            if (previewSize != null){
                Display display = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

                switch (display.getRotation())
                {
                    case Surface.ROTATION_0:
                        previewWidth = this.previewSize.height;
                        previewHeight = this.previewSize.width;
                        this.camera.setDisplayOrientation(90);
                        break;
                    case Surface.ROTATION_90:
                        previewWidth = this.previewSize.width;
                        previewHeight = this.previewSize.height;
                        break;
                    case Surface.ROTATION_180:
                        previewWidth = this.previewSize.height;
                        previewHeight = this.previewSize.width;
                        break;
                    case Surface.ROTATION_270:
                        previewWidth = this.previewSize.width;
                        previewHeight = this.previewSize.height;
                        this.camera.setDisplayOrientation(180);
                        break;
                }
            }

            final int scaledChildHeight = previewHeight * width / previewWidth;
            this.cameraView.layout(0, height - scaledChildHeight, width, height);
        }
    }

    private void setCamera(Camera camera) {
        this.camera = camera;
        supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
        supportedFlashModes = camera.getParameters().getSupportedFlashModes();

        if (supportedFlashModes != null && supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
            Camera.Parameters parameters = this.camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            this.camera.setParameters(parameters);
        }

        requestLayout();
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height)
    {
        // Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
        Camera.Size optimalSize = null;

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) height / width;

        // Try to find a size match which suits the whole screen minus the menu on the left.
        for (Camera.Size size : sizes){

            if (size.height != width) continue;
            double ratio = (double) size.width / size.height;
            if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE){
                optimalSize = size;
            }
        }

        // If we cannot find the one that matches the aspect ratio, ignore the requirement.
        if (optimalSize == null) {
            // TODO : Backup in case we don't get a size.
        }

        return optimalSize;
    }

    public void release() {
        camera = null;
    }
}
