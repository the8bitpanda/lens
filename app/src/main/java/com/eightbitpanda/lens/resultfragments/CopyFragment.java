package com.eightbitpanda.lens.resultfragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eightbitpanda.lens.R;
import com.eightbitpanda.lens.StaticScannerActivity;
import com.eightbitpanda.lens.helper.HistoryItem;
import com.eightbitpanda.lens.helper.TextRecognizerHelper;
import com.google.android.gms.vision.text.TextBlock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;


public class CopyFragment extends Fragment {


    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_copy, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Looking for Text to Copy");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Bitmap bitmapToScan = TextRecognizerHelper.getBitmapToScan(getActivity());
        if (bitmapToScan != null) {
            SparseArray<TextBlock> textBlocks = TextRecognizerHelper.getTextBlocks(getActivity(), bitmapToScan);
            HashSet<String> textRaw = extractTextRaw(textBlocks);
            showResult(view, textRaw);
        } else {
            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        return view;
    }

    private HashSet<String> extractTextRaw(SparseArray<TextBlock> textBlocks) {
        HashSet<String> textRaw = new HashSet<>();
        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock block = textBlocks.valueAt(i);
            textRaw.add(block.getValue());
        }

        return textRaw;
    }

    private void showResult(final View view, HashSet<String> textClean) {
        ArrayList<String> textCleanList = new ArrayList<>();
        textCleanList.addAll(textClean);

        if (textCleanList.size() == 0) {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "No Text found", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            Intent scannerActivity = new Intent(getActivity(), StaticScannerActivity.class);
            scannerActivity.putExtra("Type", "Copy");
            startActivity(scannerActivity);
        } else {
            progressDialog.dismiss();
            setView(view, textCleanList);
        }


    }


    public void setView(View view, final ArrayList<String> textCleanList) {
        StringBuilder text = new StringBuilder();
        for (String t : textCleanList)
            text.append("\n\n").append(t.trim());

        final ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("lens", text.toString());
        if (clipboardManager != null)
            clipboardManager.setPrimaryClip(clipData);


        LinearLayout copyParent = view.findViewById(R.id.copy_parent);
        final TextView copyMessage = view.findViewById(R.id.copy_message);
        FloatingActionButton retryButton = view.findViewById(R.id.copy_retry);
        FloatingActionButton shareButton = view.findViewById(R.id.copy_share);
        FloatingActionButton searchButton = view.findViewById(R.id.copy_search);
        copyParent.setVisibility(View.VISIBLE);
        copyMessage.setText(text.toString());

        TextRecognizerHelper.saveHistory(getActivity(), new HistoryItem("Copy", text.toString(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())));
        TextRecognizerHelper.clearCache(getActivity());


        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                Intent scannerActivity = new Intent(getActivity(), StaticScannerActivity.class);
                scannerActivity.putExtra("Type", "Copy");
                startActivity(scannerActivity);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = copyMessage.getText().toString();
                if (copyMessage.hasSelection()) {
                    int start = copyMessage.getSelectionStart();
                    int end = copyMessage.getSelectionEnd();
                    String sharedText = text.substring(start, end);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, sharedText + "\n\nScanned with Lens");
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                } else
                    Toast.makeText(getActivity(), "Select something first", Toast.LENGTH_SHORT).show();


            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = copyMessage.getText().toString();
                if (copyMessage.hasSelection()) {
                    int start = copyMessage.getSelectionStart();
                    int end = copyMessage.getSelectionEnd();
                    String searchText = text.substring(start, end);
                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    intent.putExtra(SearchManager.QUERY, searchText);
                    startActivity(intent);
                } else
                    Toast.makeText(getActivity(), "Select something first", Toast.LENGTH_SHORT).show();

            }
        });


    }


}



