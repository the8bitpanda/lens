package com.eightbitpanda.lens.resultfragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eightbitpanda.lens.R;
import com.eightbitpanda.lens.helper.TextRecognizerHelper;
import com.google.android.gms.vision.text.TextBlock;


public class CopyFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_copy, container, false);


        Bitmap bitmapToScan = TextRecognizerHelper.getBitmapToScan(getActivity());
        if (bitmapToScan != null) {
            SparseArray<TextBlock> textBlocks = TextRecognizerHelper.getTextBlocks(getActivity(), bitmapToScan);
        }
        return view;
    }


}



