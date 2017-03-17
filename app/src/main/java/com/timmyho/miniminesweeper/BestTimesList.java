package com.timmyho.miniminesweeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.timmyho.miniminesweeper.model.TimeEntry;
import com.timmyho.miniminesweeper.utilities.TimeEntryAdapter;

import java.util.ArrayList;

public class BestTimesList extends AppCompatActivity {
    private SQLiteDatabase bestTimesDB;
    int totalNumTimes = 0;
    int currentOffset = 0;
    int paginateValue = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_times_list);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        long timeTaken = intent.getLongExtra("timeTaken", -1);

        this.bestTimesDB = openOrCreateDatabase("BestTimesDB", MODE_PRIVATE, null);

        // PROTO_ONLY this is just so I can fill the table up with random information to
        // ensure it works

        this.bestTimesDB.execSQL("DROP TABLE IF EXISTS timeList");
        this.bestTimesDB.execSQL("CREATE TABLE IF NOT EXISTS timeList (" +
                "id     INTEGER    PRIMARY KEY   AUTOINCREMENT, " +
                "name   VARCHAR(20)              NOT NULL, " +
                "timeTaken  INT                  NOT NULL);");

        for (int i = 0; i < 50; i+=3) {
            this.bestTimesDB.execSQL(
                "INSERT INTO timeList (name, timeTaken) VALUES (\"JOJO\", " + i + ");");
        }

        Log.d("LOL", "[BEFORE] This is how many things are in TimeList: "+totalNumTimes);

        Cursor countCr = this.bestTimesDB.rawQuery("SELECT COUNT(*) AS timeCount FROM timeList", null);
        countCr.moveToFirst();

        this.totalNumTimes = countCr.getInt(countCr.getColumnIndex("timeCount"));
        Log.d("LOL", "This is how many things are in TimeList: "+totalNumTimes);

        if (name != "" && timeTaken != -1) {
            this.bestTimesDB.execSQL(String.format("INSERT INTO timeList (name, timeTaken) VALUES (\"%s\", %d);", name, timeTaken));

            Toast.makeText(this.getBaseContext(), "Added "+name+": "+timeTaken+ " to Best Times!", Toast.LENGTH_SHORT).show();
        }
        Cursor cr = this.bestTimesDB.rawQuery("SELECT name, timeTaken FROM timeList ORDER BY timeTaken ASC LIMIT "+paginateValue, null);

        DisplayNewTimes(cr);
    }


    public void prevTimesClick(View view) {
        this.currentOffset--;

        Cursor cr = this.bestTimesDB.rawQuery("SELECT name, timeTaken FROM timeList ORDER BY timeTaken ASC LIMIT "+currentOffset*paginateValue+", "+paginateValue, null);
        DisplayNewTimes(cr);
    }

    public void nextTimesClick(View view) {
        this.currentOffset++;

        // PRETTIFY: Figure out how to determine the end of the pagination and what to do with it

        Cursor cr = this.bestTimesDB.rawQuery("SELECT name, timeTaken FROM timeList ORDER BY timeTaken ASC LIMIT "+currentOffset*paginateValue+", "+paginateValue, null);
        DisplayNewTimes(cr);
    }

    public void clearTimesClick(View view) {
        this.bestTimesDB.execSQL("DELETE FROM timeList");

        this.currentOffset = 0;
        this.totalNumTimes = 0;
        Cursor cr = this.bestTimesDB.rawQuery("SELECT name, timeTaken FROM timeList ORDER BY timeTaken ASC LIMIT "+paginateValue, null);

        DisplayNewTimes(cr);
    }


    private void DisplayNewTimes(Cursor cr) {
        ArrayList<TimeEntry> timeEntries = new ArrayList<TimeEntry>();
        if (cr.moveToFirst()) {
            do {
                TimeEntry entry = new TimeEntry();
                entry.name = cr.getString(cr.getColumnIndex("name"));
                entry.timeTaken = cr.getInt(cr.getColumnIndex("timeTaken"));

                timeEntries.add(entry);
            } while (cr.moveToNext());
        }

        ListView bestTimesList = (ListView) findViewById(R.id.bestTimesListView);

        TimeEntryAdapter timeEntryAdapter = new TimeEntryAdapter(
                this, timeEntries, this.currentOffset, this.paginateValue);

        bestTimesList.setAdapter(timeEntryAdapter);

        EnableOrDisablePagingButtons();
    }

    private void EnableOrDisablePagingButtons() {
        Button prevTimesButton = (Button) findViewById(R.id.prevTimesButton);
        prevTimesButton.setEnabled(this.currentOffset > 0);

        Button nextTimesButton = (Button) findViewById(R.id.nextTimesButton);
        nextTimesButton.setEnabled((this.currentOffset + 1) * this.paginateValue <= this.totalNumTimes);
    }
}
