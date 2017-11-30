package com.eightbitpanda.lens.helper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "lensdb";
    private static final String TABLE_HISTORY = "history";

    private static final String HISTORY_ITEMID = "id";
    private static final String HISTORY_TYPE = "type";
    private static final String HISTORY_TEXT = "text";
    private static final String HISTORY_TIME = "time";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createHistoryTable = "CREATE TABLE " + TABLE_HISTORY + "(" + HISTORY_ITEMID + " INTEGER PRIMARY KEY, "
                + HISTORY_TYPE + " TEXT, " + HISTORY_TEXT + " TEXT, " + HISTORY_TIME + " TEXT" + ")";
        sqLiteDatabase.execSQL(createHistoryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(sqLiteDatabase);
    }

    public void addHistoryItem(HistoryItem historyItem) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(HISTORY_TYPE, historyItem.getType());
        contentValues.put(HISTORY_TEXT, historyItem.getText());
        contentValues.put(HISTORY_TIME, historyItem.getTime());

        db.insert(TABLE_HISTORY, null, contentValues);
        db.close();
    }

    public HistoryItem getHistoryItem(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HISTORY, new String[]{HISTORY_ITEMID,
                        HISTORY_TYPE, HISTORY_TEXT, HISTORY_TIME}, HISTORY_ITEMID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        HistoryItem historyItem = new HistoryItem(cursor.getString(0),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));

        cursor.close();
        return historyItem;
    }

    public List<HistoryItem> getHistory() {
        List<HistoryItem> historyItemsList = new ArrayList<HistoryItem>();

        String selectQuery = "SELECT  * FROM " + TABLE_HISTORY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HistoryItem historyItem = new HistoryItem();
                historyItem.setId(cursor.getString(0));
                historyItem.setType(cursor.getString(1));
                historyItem.setText(cursor.getString(2));
                historyItem.setTime(cursor.getString(3));
                historyItemsList.add(historyItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return historyItemsList;
    }

    public void deleteHistoryItem(HistoryItem historyItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, HISTORY_ITEMID + " = ?",
                new String[]{String.valueOf(historyItem.getId())});
        db.close();
    }
}
