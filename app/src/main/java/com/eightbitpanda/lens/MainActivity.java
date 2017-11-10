package com.eightbitpanda.lens;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeActionBarTitle(R.string.title_home);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    changeActionBarTitle(R.string.title_home);
                    return true;
                case R.id.navigation_history:
                    changeActionBarTitle(R.string.title_history);
                    return true;
                case R.id.navigation_settings:
                    changeActionBarTitle(R.string.title_settings);
                    return true;
            }
            return false;
        }

    };

    public void changeActionBarTitle(int title) {
        if (getActionBar() != null)
            getActionBar().setTitle(title);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }

}
