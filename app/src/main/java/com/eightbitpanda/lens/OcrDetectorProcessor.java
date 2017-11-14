package com.eightbitpanda.lens;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.util.SparseArray;

import com.eightbitpanda.lens.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 * TODO: Make this implement Detector.Processor<TextBlock> and add text to the GraphicOverlay
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private String type;

    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay, String type) {
        mGraphicOverlay = ocrGraphicOverlay;
        this.type = type;
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null && validItemType(item.getValue())) {
                Log.d("Processor", "Text detected! " + item.getValue());
                success(type, item.getValue());
                break;
                //OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                //mGraphicOverlay.add(graphic);
            }
        }
    }

    private void success(String type, String itemValue) {
        switch (type) {
            case "Weblink":
                if (!itemValue.startsWith("http://") && !itemValue.startsWith("https://"))
                    itemValue = "http://" + itemValue;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemValue));
                mGraphicOverlay.getContext().startActivity(browserIntent);
                break;
            case "Call":
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + itemValue));
                mGraphicOverlay.getContext().startActivity(intent);

                break;
            case "Bussiness Card":
                break;
            case "Translate":
                break;
            case "Search":
                break;
        }
    }

    private boolean validItemType(String itemValue) {
        switch (type) {
            case "Weblink":
                String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
                Pattern p = Pattern.compile(URL_REGEX);
                Matcher m = p.matcher(itemValue);
                return m.find();
            case "Call":
                return !TextUtils.isEmpty(itemValue) && Patterns.PHONE.matcher(itemValue).matches();
            case "Bussiness Card":
                return true;
            case "Translate":
                return true;
            case "Search":
                return true;
        }
        return false;
    }

    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}
