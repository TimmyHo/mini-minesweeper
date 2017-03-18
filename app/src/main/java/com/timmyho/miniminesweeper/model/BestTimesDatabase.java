package com.timmyho.miniminesweeper.model;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.support.v7.app.AppCompatActivity;

import com.timmyho.miniminesweeper.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by timot on 3/18/2017.
 */

// Maybe there is an easier to figure get the mock data from the resource ID
public class BestTimesDatabase extends AppCompatActivity {
    private static SQLiteDatabase bestTimesDB = initDB();
    private static int numTimeEntries;

    private static List<TimeEntry> mockDataEntries;

    private static SQLiteDatabase initDB() {
        if (bestTimesDB != null) {
            bestTimesDB = SQLiteDatabase.openOrCreateDatabase("BestTimesDB", null);

            bestTimesDB.execSQL("CREATE TABLE IF NOT EXISTS timeList (" +
                    "id     INTEGER    PRIMARY KEY   AUTOINCREMENT, " +
                    "name   VARCHAR(20)              NOT NULL, " +
                    "timeTaken  INT                  NOT NULL);");
            setNumTimeEntries();
        }
        return bestTimesDB;
    }

    public static List<TimeEntry> GetData(int currentOffset, int pageSize) {
        List<TimeEntry> timeEntries = new ArrayList<TimeEntry>();

        Cursor cr = bestTimesDB.rawQuery("SELECT name, timeTaken FROM timeList ORDER BY timeTaken ASC LIMIT "+currentOffset* pageSize +", "+ pageSize, null);
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

    public static void InsertTimeEntry(String name, int timeTaken) {
        bestTimesDB.execSQL(String.format("INSERT INTO timeList (name, timeTaken) VALUES (\"%s\", %d);", name, timeTaken));
        setNumTimeEntries();
    }

    public static void ClearAllTimes() {
        bestTimesDB.execSQL("DELETE FROM timeList");
    }

    public static void readInMockData() {
        Scanner scanner = new Scanner(Resources.getSystem().openRawResource(R.raw.mock_data));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\t");

            if (parts.length == 2) {
                TimeEntry timeEntry = new TimeEntry();
                timeEntry.name = parts[0];
                timeEntry.timeTaken = Integer.parseInt(parts[1]);

                mockDataEntries.add(timeEntry);
            }
        }
    }

    public static void AddMockData() {
        for (int i = 0; i < mockDataEntries.size(); i++) {
            bestTimesDB.execSQL(String.format(
                    "INSERT INTO timeList (name, timeTaken) VALUES (\"%s\", %d)",
                    mockDataEntries.get(i).name,
                    mockDataEntries.get(i).timeTaken));
        }
        setNumTimeEntries();
    }

    private static void setNumTimeEntries() {

        Cursor countCr = bestTimesDB.rawQuery("SELECT COUNT(*) AS timeCount FROM timeList", null);
        countCr.moveToFirst();

        numTimeEntries = countCr.getInt(countCr.getColumnIndex("timeCount"));
    }

    public static int GetNumTimeEntries() {
        return numTimeEntries;
    }
}
