package com.timmyho.miniminesweeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BestTimesList extends AppCompatActivity {
    private SQLiteDatabase bestTimesDB;
    int currentOffset = 0;
    int paginateValue = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_times_list);


        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        long timeTaken = intent.getLongExtra("timeTaken", -1);

        Log.d("newIntentInfo", "From previous page: "+name+" has a timeTaken of "+timeTaken);


        this.bestTimesDB = openOrCreateDatabase("BestTimesDB", MODE_PRIVATE, null);

        // PROTO_ONLY this is just so I can fill the table up with random information to
        // ensure it works

        this.bestTimesDB.execSQL("DROP TABLE IF EXISTS timeList");
        this.bestTimesDB.execSQL("CREATE TABLE IF NOT EXISTS timeList (" +
                "id     INTEGER    PRIMARY KEY   AUTOINCREMENT, " +
                "name   VARCHAR(20)              NOT NULL, " +
                "timeTaken  INT                  NOT NULL);");

        for (int i = 0; i < 30; i+=3) {
            this.bestTimesDB.execSQL(
                "INSERT INTO timeList (name, timeTaken) VALUES (\"JOJO\", " + i + ");");
        }

        if (name != "" && timeTaken != -1) {
            this.bestTimesDB.execSQL(String.format("INSERT INTO timeList (name, timeTaken) VALUES (\"%s\", %d);", name, timeTaken));

            Toast.makeText(this.getBaseContext(), "Added "+name+": "+timeTaken, Toast.LENGTH_SHORT).show();
        }
        Cursor cr = this.bestTimesDB.rawQuery("SELECT name, timeTaken FROM timeList ORDER BY timeTaken DESC LIMIT "+paginateValue, null);

        DisplayNewTimes(cr);
    }


    public void prevScoreClick(View view) {
        if (this.currentOffset == 0) {
            return;
        }

        this.currentOffset--;

        Cursor cr = this.bestTimesDB.rawQuery("SELECT name, timeTaken FROM timeList ORDER BY timeTaken DESC LIMIT "+currentOffset*paginateValue+", "+paginateValue, null);
        DisplayNewTimes(cr);
    }

    public void nextScoreClick(View view) {
        this.currentOffset++;

        // PRETTIFY: Figure out how to determine the end of the pagination and what to do with it

        Cursor cr = this.bestTimesDB.rawQuery("SELECT name, timeTaken FROM timeList ORDER BY timeTaken DESC LIMIT "+currentOffset*paginateValue+", "+paginateValue, null);
        DisplayNewTimes(cr);
    }

    private void DisplayNewTimes(Cursor cr) {
        ArrayList<String> times = new ArrayList<String>();
        if (cr.moveToFirst()) {
            do {
                String name = cr.getString(cr.getColumnIndex("name"));
                int score = cr.getInt(cr.getColumnIndex("timeTaken"));

                times.add(name+": "+score);
            } while (cr.moveToNext());
        }

        ListView highScoreList = (ListView) findViewById(R.id.highScoreListView);

        ArrayAdapter<String> highScoreListAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, times);

        highScoreList.setAdapter(highScoreListAdapter);
    }
}
