package com.eightbitpanda.lens;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SnapScannerActivity extends AppCompatActivity {


    SurfaceView cameraScreen;
    SurfaceHolder previewScreenHolder;
    Camera camera;
    boolean underPreview;
    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewScreenHolder);
            } catch (Throwable t) {
                Log.e("Exception", "Exception in setPreviewDisplay", t);
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = findbestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                camera.setParameters(parameters);
                camera.startPreview();
                underPreview = true;
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
// not required
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        underPreview = false;
        cameraScreen = (SurfaceView) findViewById(R.id.cameraDemo);
        previewScreenHolder = cameraScreen.getHolder();
        previewScreenHolder.addCallback(surfaceCallback);
        previewScreenHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void onResume() {
        super.onResume();
        camera = Camera.open();
    }

    @Override
    public void onPause() {
        if (underPreview) {
            camera.stopPreview();
        }
        camera.release();
        camera = null;
        underPreview = false;
        super.onPause();
    }

    private Camera.Size findbestPreviewSize(int width, int height,
                                            Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        return (result);
    }
}
