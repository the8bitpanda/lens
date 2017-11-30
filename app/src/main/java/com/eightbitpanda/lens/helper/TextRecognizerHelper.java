package com.eightbitpanda.lens.helper;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TextRecognizerHelper {

    public static Bitmap getBitmapToScan(Context This) {
        Bitmap bitmap = null;
        File file = new File(This.getCacheDir(), "lensCache");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static SparseArray<TextBlock> getTextBlocks(Context This, Bitmap bitmap) {

        TextRecognizer textRecognizer = new TextRecognizer.Builder(This).build();
        if (!textRecognizer.isOperational()) {
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = This.registerReceiver(null, lowStorageFilter) != null;
            if (hasLowStorage) {
                Toast.makeText(This, "Low Storage", Toast.LENGTH_SHORT).show();
            }
        }

        Frame imageFrame = new Frame.Builder().setBitmap(bitmap).build();
        return textRecognizer.detect(imageFrame);
    }

    public static void clearCache(Context This) {
        File file = new File(This.getCacheDir(), "lensCache");
        file.delete();
    }

    public static void saveHistory(Context This, HistoryItem historyItem) {
        DatabaseHandler db = new DatabaseHandler(This);
        db.addHistoryItem(historyItem);
    }
}
