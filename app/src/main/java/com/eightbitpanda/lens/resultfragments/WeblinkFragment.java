package com.eightbitpanda.lens.resultfragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.HashSet;


public class WeblinkFragment extends Fragment {


    ProgressDialog progressDialog;
    private CountDownTimer countDownTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weblink, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Looking for Weblinks");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Bitmap bitmapToScan = TextRecognizerHelper.getBitmapToScan(getActivity());
        if (bitmapToScan != null) {
            SparseArray<TextBlock> textBlocks = TextRecognizerHelper.getTextBlocks(getActivity(), bitmapToScan);
            HashSet<String> weblinksRaw = extractWeblinksRaw(textBlocks);
            showResult(view, weblinksRaw);
        } else {
            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        return view;
    }

    private HashSet<String> extractWeblinksRaw(SparseArray<TextBlock> textBlocks) {
        HashSet<String> weblinksRaw = new HashSet<>();
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

    private void showResult(final View view, HashSet<String> weblinksClean) {
        ArrayList<String> weblinksCleanList = new ArrayList<>();
        weblinksCleanList.addAll(weblinksClean);
        final String[] weblink = new String[1];

        if (weblinksCleanList.size() == 0) {
            weblink[0] = null;
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "No Weblinks found", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            Intent scannerActivity = new Intent(getActivity(), StaticScannerActivity.class);
            scannerActivity.putExtra("Type", "Weblink");
            startActivity(scannerActivity);
        } else if (weblinksCleanList.size() == 1) {
            weblink[0] = weblinksCleanList.get(0);
            progressDialog.dismiss();
            setView(view, weblink[0]);
        } else if (weblinksCleanList.size() > 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Multiple Weblinks found");
            final String[] links = getArray(weblinksCleanList);
            builder.setItems(links, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    weblink[0] = links[which];
                    progressDialog.dismiss();
                    setView(view, weblink[0]);
                }
            });


            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getActivity().finish();
                    Intent scannerActivity = new Intent(getActivity(), StaticScannerActivity.class);
                    scannerActivity.putExtra("Type", "Weblink");
                    startActivity(scannerActivity);
                }
            });
            dialog.show();
        }


    }

    private String[] getArray(ArrayList<String> weblinksCleanList) {
        String[] returnArray = new String[weblinksCleanList.size()];
        for (int i = 0; i < weblinksCleanList.size(); i++)
            returnArray[i] = weblinksCleanList.get(i);

        return returnArray;
    }


    public void setView(View view, final String weblink) {
        RelativeLayout weblinkParent = view.findViewById(R.id.weblink_parent);
        TextView weblinkMessage = view.findViewById(R.id.weblink_message);
        final TextView weblinkCountdown = view.findViewById(R.id.weblink_countdown);
        FloatingActionButton retryButton = view.findViewById(R.id.weblink_retry);
        FloatingActionButton shareButton = view.findViewById(R.id.weblink_share);
        weblinkParent.setVisibility(View.VISIBLE);
        weblinkMessage.setText("Opening " + weblink + " in");

        countDownTimer = new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                weblinkCountdown.setText("" + (millisUntilFinished / 1000));
            }

            public void onFinish() {
                getActivity().finish();
                //Save history using helper
                openWeblink(weblink);
            }
        }.start();

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                getActivity().finish();
                Intent scannerActivity = new Intent(getActivity(), StaticScannerActivity.class);
                scannerActivity.putExtra("Type", "Weblink");
                startActivity(scannerActivity);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, weblink + "\n\nScanned with Lens");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });


    }

    private void openWeblink(String weblink) {
        if (!weblink.startsWith("http://") && !weblink.startsWith("https://"))
            weblink = "http://" + weblink;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(weblink));
        startActivity(browserIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (countDownTimer != null)
            countDownTimer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (countDownTimer != null)
            countDownTimer.cancel();
    }
}



