package com.eightbitpanda.lens;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setActionBarTitle(R.string.title_home);
                    setFragment(1, new HomeFragment());
                    return true;
                case R.id.navigation_history:
                    setActionBarTitle(R.string.title_history);
                    setFragment(1, new HistoryFragment());
                    return true;
                case R.id.navigation_settings:
                    setActionBarTitle(R.string.title_settings);
                    setFragment(1, new SettingsFragment());
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionBarTitle(R.string.title_home);
        setFragment(0, new HomeFragment());
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    public void setActionBarTitle(int title) {
        if (getActionBar() != null)
            getActionBar().setTitle(title);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }

    public void setFragment(int mode, Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (mode == 0)
            fragmentTransaction.add(R.id.content, fragment);
        else if (mode == 1)
            fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commit();
    }

}
