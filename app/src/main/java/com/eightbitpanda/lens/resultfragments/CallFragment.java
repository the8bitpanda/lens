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
import android.util.Patterns;
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


public class CallFragment extends Fragment {


    ProgressDialog progressDialog;
    private CountDownTimer countDownTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Looking for Phone Numbers");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Bitmap bitmapToScan = TextRecognizerHelper.getBitmapToScan(getActivity());
        if (bitmapToScan != null) {
            SparseArray<TextBlock> textBlocks = TextRecognizerHelper.getTextBlocks(getActivity(), bitmapToScan);
            HashSet<String> numbersRaw = extractNumbersRaw(textBlocks);
            showResult(view, numbersRaw);
        } else {
            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        return view;
    }

    private HashSet<String> extractNumbersRaw(SparseArray<TextBlock> textBlocks) {
        HashSet<String> numbersRaw = new HashSet<>();
        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock block = textBlocks.valueAt(i);
            if (isUrl(block.getValue())) {
                numbersRaw.add(block.getValue());
            }
            for (Text line : block.getComponents()) {
                if (isUrl(line.getValue())) {
                    numbersRaw.add(line.getValue());
                }
                for (Text word : line.getComponents()) {
                    if (isUrl(word.getValue())) {
                        numbersRaw.add(word.getValue());
                    }
                }
            }
        }

        return numbersRaw;
    }

    private boolean isUrl(String value) {
        return Patterns.PHONE.matcher(value).matches();
    }

    private void showResult(final View view, HashSet<String> numbersClean) {
        ArrayList<String> numbersCleanList = new ArrayList<>();
        numbersCleanList.addAll(numbersClean);
        final String[] number = new String[1];

        if (numbersCleanList.size() == 0) {
            number[0] = null;
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "No Phone Numbers found", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            Intent scannerActivity = new Intent(getActivity(), StaticScannerActivity.class);
            scannerActivity.putExtra("Type", "Call");
            startActivity(scannerActivity);
        } else if (numbersCleanList.size() == 1) {
            number[0] = numbersCleanList.get(0);
            progressDialog.dismiss();
            setView(view, number[0]);
        } else if (numbersCleanList.size() > 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Multiple links found");
            final String[] links = getArray(numbersCleanList);
            builder.setItems(links, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    number[0] = links[which];
                    progressDialog.dismiss();
                    setView(view, number[0]);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }


    }

    private String[] getArray(ArrayList<String> numbersCleanList) {
        String[] returnArray = new String[numbersCleanList.size()];
        for (int i = 0; i < numbersCleanList.size(); i++)
            returnArray[i] = numbersCleanList.get(i);

        return returnArray;
    }

    private void callNumber(String number) {

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    public void setView(View view, final String number) {
        RelativeLayout callParent = view.findViewById(R.id.call_parent);
        TextView callMessage = view.findViewById(R.id.call_message);
        final TextView callCountdown = view.findViewById(R.id.call_countdown);
        FloatingActionButton retryButton = view.findViewById(R.id.call_retry);
        FloatingActionButton shareButton = view.findViewById(R.id.call_share);
        callParent.setVisibility(View.VISIBLE);
        callMessage.setText("Calling " + number + " in");

        countDownTimer = new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                callCountdown.setText("" + (millisUntilFinished / 1000));
            }

            public void onFinish() {
                getActivity().finish();
                callNumber(number);
            }
        }.start();

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                getActivity().finish();
                Intent scannerActivity = new Intent(getActivity(), StaticScannerActivity.class);
                scannerActivity.putExtra("Type", "Call");
                startActivity(scannerActivity);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, number + "\n\nScanned with Lens");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });


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



