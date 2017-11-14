package com.eightbitpanda.lens;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class ScannerResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_result);


        ImageView imageView = (ImageView) findViewById(R.id.imageView);


    }
}
