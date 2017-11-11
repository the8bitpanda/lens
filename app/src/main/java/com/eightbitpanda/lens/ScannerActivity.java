package com.eightbitpanda.lens;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        Bundle extras = getIntent().getExtras();
        String type = extras.getString("Type");
        if (getActionBar() != null)
            getActionBar().setTitle(type);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(type);
    }
}
