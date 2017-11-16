package com.eightbitpanda.lens;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ScannerResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_result);


        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        Bitmap bitmapToScan = null;
        File file;

        file = new File(getCacheDir(), "lensCache");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            bitmapToScan = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmapToScan);


    }
}
