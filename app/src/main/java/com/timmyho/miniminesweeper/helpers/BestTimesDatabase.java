package com.timmyho.miniminesweeper.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.timmyho.miniminesweeper.R;
import com.timmyho.miniminesweeper.model.TimeEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by timot on 3/18/2017.
 */

public class BestTimesDatabase extends SQLiteOpenHelper {
    // This is def not the right way to do this
    private static BestTimesDatabase instance;

    private static String DATABASE_NAME = "BestTimesDB";
    private static int DATABASE_VERSION = 1;
    private static String TABLE_NAME = "timeList";

    private int numTimeEntries = -1;
    private List<TimeEntry> mockDataEntries;

    public static BestTimesDatabase GetInstance(Context c) {
        if (instance == null) {
            instance = new BestTimesDatabase(c.getApplicationContext());
        }
        return instance;
    }

    BestTimesDatabase(Context appContext) {
        super(appContext, DATABASE_NAME, null, DATABASE_VERSION);

        readInMockData(appContext);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (" +
                "id     INTEGER    PRIMARY KEY   AUTOINCREMENT, " +
                "name   VARCHAR(20)              NOT NULL, " +
                "timeTaken  INT                  NOT NULL);",
                TABLE_NAME));


        Log.d("huh", "I should be called, no?");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.d("early", "THIS SHOULD BE CALLED SUPER EARLY");
        setNumTimeEntries(db);
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Do nothing, don't anticipate revving the version and would probably just delete data of
        // the older version
    }

    private void readInMockData(Context applicationContext) {
        Scanner scanner = new Scanner(applicationContext.getResources().openRawResource(R.raw.mock_data));

        this.mockDataEntries = new ArrayList<TimeEntry>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\t");

            if (parts.length == 2) {
                TimeEntry timeEntry = new TimeEntry();
                timeEntry.name = parts[0];
                timeEntry.timeTaken = Integer.parseInt(parts[1]);

                this.mockDataEntries.add(timeEntry);
            }
        }
    }

    public void AddMockData() {
        SQLiteDatabase db = getWritableDatabase();

        if (this.mockDataEntries != null) {
            for (int i = 0; i < this.mockDataEntries.size(); i++) {
                db.execSQL(String.format(
                        "INSERT INTO %s (name, timeTaken) VALUES (\"%s\", %d)",
                        TABLE_NAME,
                        this.mockDataEntries.get(i).name,
                        this.mockDataEntries.get(i).timeTaken));
            }
        }

        setNumTimeEntries(db);
    }

    private void setNumTimeEntries(SQLiteDatabase db) {
        Cursor countCr = db.rawQuery(String.format("SELECT COUNT(*) AS timeCount FROM %s", TABLE_NAME), null);

        if (countCr.moveToFirst()) {
            this.numTimeEntries = countCr.getInt(countCr.getColumnIndex("timeCount"));
        } else {
            this.numTimeEntries = 0;
        }
    }

    public int GetNumTimeEntries() {
        if (this.numTimeEntries == -1) {
            SQLiteDatabase db = getReadableDatabase();
            setNumTimeEntries(db);
        }
        return this.numTimeEntries;
    }

    public List<TimeEntry> GetData(int currentOffset, int pageSize) {
        List<TimeEntry> timeEntries = new ArrayList<TimeEntry>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = db.rawQuery(String.format(
            "SELECT name, timeTaken FROM %s ORDER BY timeTaken ASC LIMIT %d, %d",
            TABLE_NAME,
            currentOffset*pageSize,
            pageSize), null);

        if (cr.moveToFirst()) {
            do {
                TimeEntry entry = new TimeEntry();
                entry.name = cr.getString(cr.getColumnIndex("name"));
                entry.timeTaken = cr.getInt(cr.getColumnIndex("timeTaken"));

                timeEntries.add(entry);
            } while (cr.moveToNext());
        }
        return timeEntries;
    }

    public void InsertTimeEntry(String name, int timeTaken) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(String.format(
                "INSERT INTO %S (name, timeTaken) VALUES (\"%s\", %d);",
                TABLE_NAME,
                name,
                timeTaken));

        setNumTimeEntries(db);
    }

    public void ClearAllTimes() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(String.format("DELETE FROM %s", TABLE_NAME));

        setNumTimeEntries(db);
    }
}
