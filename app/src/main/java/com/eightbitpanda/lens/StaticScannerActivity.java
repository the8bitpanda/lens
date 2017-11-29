package com.eightbitpanda.lens;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eightbitpanda.lens.ui.staticscanner.ImageSurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class StaticScannerActivity extends AppCompatActivity {

    private static final int RC_HANDLE_CAMERA_PERM = 2, RC_HANDLE_GALLERY_PERM = 3, RC_GALLERY_IMAGE = 4;
    String type;
    private ProgressDialog progressDialog;
    PictureCallback pictureCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bitmap != null) {
                //Call Result Activity and pass the bitmap to scan it there

                callToScannerResultActivity(bitmap, true);
            }
        }
    };
    private Camera camera;
    private FrameLayout cameraPreviewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_scanner);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cameraPreviewLayout = (FrameLayout) findViewById(R.id.camera_preview);
        progressDialog = new ProgressDialog(this);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        type = extras.getString("Type");
        setActionBarTitle();

        TextView helpText = (TextView) findViewById(R.id.help_text);
        setHelpText(getHelpText(), helpText);


        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            setUpCamera();
        } else {
            requestCameraPermission(helpText);
        }


    }

    private void setActionBarTitle() {
        if (getActionBar() != null)
            getActionBar().setTitle("Oh Snap");
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Oh Snap");
    }

    private String getHelpText() {
        switch (type) {
            case "Weblink":
                return "Place the scanner directly over the Weblink you want to open in portrait mode and tap the scan button";
            case "Call":
                return "Place the scanner directly over the Phone Number you want to call in portrait mode and tap the scan button";
            case "Business Card":
                return "Place the scanner directly over the Business Card you want to save in portrait mode and tap the scan button";
            case "Translate":
                return "Place the scanner directly over the text you want to Translate in portrait mode and tap the scan button";
            case "Copy":
                return "Place the scanner directly over the text you want to Copy in portrait mode and tap the scan button";
        }
        return "";
    }

    private void setHelpText(String text, TextView helpText) {
        helpText.setText(text);
    }

    private void setUpCamera() {
        camera = getCamera();
        ImageSurfaceView mImageSurfaceView = new ImageSurfaceView(this, camera);
        cameraPreviewLayout.addView(mImageSurfaceView);

        final ImageView captureButton = new ImageView(this);
        captureButton.setImageResource(R.drawable.ic_camera_white_72dp);
        cameraPreviewLayout.addView(captureButton, getParams(Gravity.CENTER_HORIZONTAL, 0, 0, 16));

        ImageView galleryButton = new ImageView(this);
        galleryButton.setImageResource(R.drawable.ic_photo_size_select_actual_white_36dp);
        cameraPreviewLayout.addView(galleryButton, getParams(Gravity.START, 32, 0, 16));

        final ImageView flashButton = new ImageView(this);
        flashButton.setImageResource(R.drawable.ic_flash_off_white_36dp);
        cameraPreviewLayout.addView(flashButton, getParams(Gravity.END, 0, 32, 16));

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int perm = ActivityCompat.checkSelfPermission(StaticScannerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (perm == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    requestGalleryPermission();
                }
            }
        });
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(StaticScannerActivity.this, "Scanning", Toast.LENGTH_SHORT).show();
                    v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                    Animation rotation = AnimationUtils.loadAnimation(v.getContext(), R.anim.rotate);
                    rotation.setFillAfter(true);
                    captureButton.startAnimation(rotation);
                    captureButton.setEnabled(false);
                    camera.takePicture(null, null, pictureCallback);
                } catch (Exception e) {
                    Toast.makeText(StaticScannerActivity.this, "Camera is busy", Toast.LENGTH_SHORT).show();
                }

            }
        });

        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera.Parameters params = camera.getParameters();
                if (Camera.Parameters.FLASH_MODE_ON.equals(params.getFlashMode())) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    flashButton.setImageResource(R.drawable.ic_flash_off_white_36dp);
                    camera.setParameters(params);
                } else {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    camera.setParameters(params);
                    flashButton.setImageResource(R.drawable.ic_flash_on_white_36dp);

                }

            }
        });
    }

    private Camera getCamera() {
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
            Camera.Parameters params = mCamera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

            List<Camera.Size> sizes = params.getSupportedPictureSizes();
            Camera.Size mSize = null;
            for (int i = sizes.size() - 1; i >= 0; i--) {

                if (sizes.get(i).width >= 1024 && sizes.get(i).height >= 768) {
                    mSize = sizes.get(i);
                    break;
                }
            }
            if (mSize != null) {
                params.setPictureSize(mSize.width, mSize.height);
            }


            mCamera.setParameters(params);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCamera;
    }

    private FrameLayout.LayoutParams getParams(int gravity, int lM, int rM, int bM) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | gravity);
        lp.setMargins(lM, 0, rM, bM);
        return lp;
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_GALLERY_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                callToScannerResultActivity(bitmap, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void callToScannerResultActivity(Bitmap bitmapToScan, boolean newImage) {
        if (progressDialog != null) {
            progressDialog.setMessage("Preparing");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
        File file;
        FileOutputStream outputStream;
        try {
            file = new File(getCacheDir(), "lensCache");
            outputStream = new FileOutputStream(file);

            if (newImage) {
                ExifInterface exif = new ExifInterface(file.toString());
                if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6"))
                    bitmapToScan = rotate(bitmapToScan, 90);
                if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8"))
                    bitmapToScan = rotate(bitmapToScan, 270); //270
                if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3"))
                    bitmapToScan = rotate(bitmapToScan, 180); //180
                if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0"))
                    bitmapToScan = rotate(bitmapToScan, 90);
            }

            bitmapToScan.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (progressDialog != null)
            progressDialog.dismiss();
        Intent intent = new Intent(this, ScannerResultActivity.class);
        intent.putExtra("Type", type);
        startActivity(intent);
        this.finish();
    }

    private Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix mtx = new Matrix();
        mtx.setRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);

    }

    private void requestCameraPermission(TextView helpText) {

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(helpText, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    private void requestGalleryPermission() {

        final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_GALLERY_PERM);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == RC_HANDLE_CAMERA_PERM) {

            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We have permission, so create the camerasource
                setUpCamera();
                return;
            }
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Lens")
                    .setMessage(R.string.no_camera_permission)
                    .setPositiveButton(R.string.ok, listener)
                    .show();

        } else if (requestCode == RC_HANDLE_GALLERY_PERM) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Need permission to access gallery", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpCamera();
    }
}
