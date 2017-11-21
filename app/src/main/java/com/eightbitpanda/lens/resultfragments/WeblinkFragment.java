package com.eightbitpanda.lens.resultfragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eightbitpanda.lens.R;
import com.eightbitpanda.lens.helper.TextRecognizerHelper;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;


public class WeblinkFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weblink, container, false);

        Bitmap bitmapToScan = TextRecognizerHelper.getBitmapToScan(getActivity());
        if (bitmapToScan != null) {
            SparseArray<TextBlock> textBlocks = TextRecognizerHelper.getTextBlocks(getActivity(), bitmapToScan);
            ArrayList<String> weblinksRaw = extractWeblinksRaw(textBlocks);
            showResult(view, weblinksRaw);
        } else {
            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        return view;
    }

    private ArrayList<String> extractWeblinksRaw(SparseArray<TextBlock> textBlocks) {
        ArrayList<String> weblinksRaw = new ArrayList<>();
        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock block = textBlocks.valueAt(i);
            if (isUrl(block.getValue())) {
                weblinksRaw.add(block.getValue());
            }
            for (Text line : block.getComponents()) {
                if (isUrl(line.getValue())) {
                    weblinksRaw.add(line.getValue());
                }
                for (Text word : line.getComponents()) {
                    if (isUrl(word.getValue())) {
                        weblinksRaw.add(word.getValue());
                    }
                }
            }
        }

        return weblinksRaw;
    }

    private boolean isUrl(String value) {
        return android.util.Patterns.WEB_URL.matcher(value).matches();
    }

    private void showResult(View view, ArrayList<String> weblinksClean) {
        final String[] weblink = new String[1];

        if (weblinksClean.size() == 0) {
            weblink[0] = null;
            Toast.makeText(getActivity(), "No Weblinks found", Toast.LENGTH_SHORT).show();
        } else if (weblinksClean.size() == 1) {
            weblink[0] = weblinksClean.get(0);
        } else if (weblinksClean.size() > 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Multiple links found");
            final String[] links = (String[]) weblinksClean.toArray();
            builder.setItems(links, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    weblink[0] = links[which];
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if (weblink[0] != null)
            openWeblink(weblink[0]);


    }

    private void openWeblink(String weblink) {
        if (!weblink.startsWith("http://") && !weblink.startsWith("https://"))
            weblink = "http://" + weblink;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(weblink));
        startActivity(browserIntent);
    }

}



