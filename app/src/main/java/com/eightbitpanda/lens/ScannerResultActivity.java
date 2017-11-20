package com.eightbitpanda.lens;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.eightbitpanda.lens.resultfragments.BusinessCardFragment;
import com.eightbitpanda.lens.resultfragments.CallFragment;
import com.eightbitpanda.lens.resultfragments.CopyFragment;
import com.eightbitpanda.lens.resultfragments.TranslateFragment;
import com.eightbitpanda.lens.resultfragments.WeblinkFragment;

public class ScannerResultActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_result);

        Bundle data = getIntent().getExtras();
        assert data != null;
        String type = data.getString("Type");
        setFragment(getFragment(type));

    }

    private Fragment getFragment(String type) {

        switch (type) {
            case "Weblink":
                return new WeblinkFragment();
            case "Call":
                return new CallFragment();
            case "Business Card":
                return new BusinessCardFragment();
            case "Translate":
                return new TranslateFragment();
            case "Copy":
                return new CopyFragment();
        }
        return null;
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.scanner_result, fragment);
        fragmentTransaction.commit();
    }


}
