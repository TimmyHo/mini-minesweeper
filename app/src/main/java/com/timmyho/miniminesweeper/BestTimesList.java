package com.timmyho.miniminesweeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.timmyho.miniminesweeper.model.TimeEntry;
import com.timmyho.miniminesweeper.utilities.TimeEntryAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BestTimesList extends AppCompatActivity {
    private SQLiteDatabase bestTimesDB;
    private int totalNumTimeEntries = 0;
    private int currentOffset = 0;
    private int pageSize = 10;

    private List<TimeEntry> mockDataEntries = new ArrayList<TimeEntry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_times_list);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        long timeTaken = intent.getLongExtra("timeTaken", -1);

        this.bestTimesDB = openOrCreateDatabase("BestTimesDB", MODE_PRIVATE, null);

        this.bestTimesDB.execSQL("CREATE TABLE IF NOT EXISTS timeList (" +
                "id     INTEGER    PRIMARY KEY   AUTOINCREMENT, " +
                "name   VARCHAR(20)              NOT NULL, " +
                "timeTaken  INT                  NOT NULL);");

        readInMockData();

        // Add the hold listener to the "clear times" button to add mock data
        Button clearTimesButton = (Button) findViewById(R.id.clearTimesButton);

        clearTimesButton.setOnLongClickListener(new Button.OnLongClickListener() {
            public boolean onLongClick(View v) {
                addMockData();
                return true;
            }
        });

        if (name != "" && timeTaken != -1) {
            this.bestTimesDB.execSQL(String.format("INSERT INTO timeList (name, timeTaken) VALUES (\"%s\", %d);", name, timeTaken));

            Toast.makeText(this.getBaseContext(), name+": "+timeTaken+" added to \"Best Times\"", Toast.LENGTH_SHORT).show();
        }

        setNumTimeEntries();
        displayNewTimes();
    }


    private void setNumTimeEntries() {
        Cursor countCr = this.bestTimesDB.rawQuery("SELECT COUNT(*) AS timeCount FROM timeList", null);
        countCr.moveToFirst();

        this.totalNumTimeEntries = countCr.getInt(countCr.getColumnIndex("timeCount"));
    }

    private void readInMockData() {
        Scanner scanner = new Scanner(getResources().openRawResource(R.raw.mock_data));

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


    private void addMockData() {
        for (int i = 0; i < mockDataEntries.size(); i++) {
            this.bestTimesDB.execSQL(String.format(
                    "INSERT INTO timeList (name, timeTaken) VALUES (\"%s\", %d)",
                    this.mockDataEntries.get(i).name,
                    this.mockDataEntries.get(i).timeTaken));
        }

        setNumTimeEntries();
        displayNewTimes();
    }

    private void displayNewTimes() {
        ArrayList<TimeEntry> timeEntries = new ArrayList<TimeEntry>();

        Cursor cr = this.bestTimesDB.rawQuery("SELECT name, timeTaken FROM timeList ORDER BY timeTaken ASC LIMIT "+currentOffset* pageSize +", "+ pageSize, null);
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
                this, timeEntries, this.currentOffset, this.pageSize);

        bestTimesList.setAdapter(timeEntryAdapter);

        enableOrDisablePagingButtons();
    }

    private void enableOrDisablePagingButtons() {
        Button prevTimesButton = (Button) findViewById(R.id.prevTimesButton);
        prevTimesButton.setEnabled(this.currentOffset > 0);

        Button nextTimesButton = (Button) findViewById(R.id.nextTimesButton);
        nextTimesButton.setEnabled((this.currentOffset + 1) * this.pageSize <= this.totalNumTimeEntries);
    }

    public void PrevTimesClick(View view) {
        this.currentOffset--;

        displayNewTimes();
    }

    public void NextTimesClick(View view) {
        this.currentOffset++;

        displayNewTimes();
    }

    public void ClearTimesClick(View view) {
        this.bestTimesDB.execSQL("DELETE FROM timeList");

        this.currentOffset = 0;
        this.totalNumTimeEntries = 0;

        displayNewTimes();
    }
}
