package com.eightbitpanda.lens.resultfragments;

import android.app.Fragment;
import android.app.ProgressDialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eightbitpanda.lens.R;
import com.eightbitpanda.lens.StaticScannerActivity;
import com.eightbitpanda.lens.helper.TextRecognizerHelper;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
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
        String text = "";
        for (String t : textCleanList)
            text = text + "\n---\n" + t.trim();

        final ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("lens", text);
        if (clipboardManager != null)
            clipboardManager.setPrimaryClip(clipData);


        RelativeLayout copyParent = view.findViewById(R.id.copy_parent);
        TextView copyMessage = view.findViewById(R.id.copy_message);
        FloatingActionButton retryButton = view.findViewById(R.id.copy_retry);
        FloatingActionButton shareButton = view.findViewById(R.id.copy_share);
        copyParent.setVisibility(View.VISIBLE);
        copyMessage.setText(text);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                Intent scannerActivity = new Intent(getActivity(), StaticScannerActivity.class);
                scannerActivity.putExtra("Type", "Copy");
                startActivity(scannerActivity);
            }
        });

        final String finalText = text;
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sharedText;
                if (clipboardManager != null) {
                    ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                    sharedText = item.getText().toString();
                } else
                    sharedText = finalText;

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, sharedText + "\n\nScanned with Lens");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });


    }


}



