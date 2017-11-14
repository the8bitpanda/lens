package com.eightbitpanda.lens;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eightbitpanda.lens.ui.staticscanner.ImageSurfaceView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticScannerActivity extends AppCompatActivity {

    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private Camera camera;

    private FrameLayout cameraPreviewLayout;
    // private ImageButton captureButton;
    private TextRecognizer detector;
    private String type;
    PictureCallback pictureCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bitmap == null) {
                Toast.makeText(StaticScannerActivity.this, "Captured image is empty", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(StaticScannerActivity.this, "Captured", Toast.LENGTH_LONG).show();
                scanBitmap(bitmap);
            }
        }
    };
    //private ImageView capturedImageHolder;
    private TextView helpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_scanner);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cameraPreviewLayout = (FrameLayout) findViewById(R.id.camera_preview);
        // captureButton = (ImageButton) findViewById(R.id.capture_button);
        detector = new TextRecognizer.Builder(getApplicationContext()).build();

        Bundle extras = getIntent().getExtras();
        type = extras.getString("Type");
        setActionBarTitle(getActionBarTitle(type));
        helpText = (TextView) findViewById(R.id.help_text);
        setHelpText(getHelpText(type), helpText);


        //capturedImageHolder = (ImageView) findViewById(R.id.captured_image);
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            setUpCamera();
        } else {
            requestCameraPermission();
        }


    }

    private String getActionBarTitle(String type) {
        switch (type) {
            case "Weblink":
                return "Looking for Weblinks";
            case "Call":
                return "Looking for Phone Numbers";
            case "Business Card":
                return "Scanning Business Card";
            case "Translate":
                return "Translate";
            case "Search":
                return "Search";
        }
        return "";
    }

    private void setActionBarTitle(String title) {
        if (getActionBar() != null)
            getActionBar().setTitle(title);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }

    private String getHelpText(String type) {
        switch (type) {
            case "Weblink":
                return "Place the scanner directly over the Weblink you want to open and tap";
            case "Call":
                return "Place the scanner directly over the Phone Number you want to call and tap";
            case "Business Card":
                return "Place the scanner directly over the Business Card you want to save and tap";
            case "Translate":
                return "Place the scanner directly over the text you want to Translate and tap";
            case "Search":
                return "Place the scanner directly over the text you want to Search and tap";
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

        cameraPreviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StaticScannerActivity.this, "Called", Toast.LENGTH_SHORT).show();
                camera.takePicture(null, null, pictureCallback);
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
            mCamera.setParameters(params);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCamera;
    }

    private void scanBitmap(Bitmap bitmap) {
        if (detector.isOperational() && bitmap != null) {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = detector.detect(frame);
            for (int i = 0; i < items.size(); ++i) {

                TextBlock item = items.valueAt(i);
                Toast.makeText(this, "Detected: " + item.getValue(), Toast.LENGTH_SHORT).show();

                if (item != null && item.getValue() != null && validItemType(item.getValue())) {
                    Toast.makeText(this, "Detected: " + item.getValue(), Toast.LENGTH_SHORT).show();
                    success(type, item.getValue());
                    break;

                }
            }
        } else {
            Toast.makeText(this, "Could not set up the detector!", Toast.LENGTH_SHORT).show();
        }
    }

    private void success(String type, String itemValue) {
        switch (type) {
            case "Weblink":
                if (!itemValue.startsWith("http://") && !itemValue.startsWith("https://"))
                    itemValue = "http://" + itemValue;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemValue));
                startActivity(browserIntent);
                break;
            case "Call":
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + itemValue));
                startActivity(intent);

                break;
            case "Bussiness Card":
                break;
            case "Translate":
                break;
            case "Search":
                break;
        }
    }

    private boolean validItemType(String itemValue) {
        switch (type) {
            case "Weblink":
                String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
                Pattern p = Pattern.compile(URL_REGEX);
                Matcher m = p.matcher(itemValue);
                return m.find();
            case "Call":
                return !TextUtils.isEmpty(itemValue) && Patterns.PHONE.matcher(itemValue).matches();
            case "Bussiness Card":
                return true;
            case "Translate":
                return true;
            case "Search":
                return true;
        }
        return false;
    }


    private void requestCameraPermission() {

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpCamera();
    }


}
