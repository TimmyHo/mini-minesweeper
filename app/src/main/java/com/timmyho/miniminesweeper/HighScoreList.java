package com.timmyho.miniminesweeper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class HighScoreList extends AppCompatActivity {
    private SQLiteDatabase highScoreDB;
    int currentOffset = 0;
    int paginateValue = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score_list);



        this.highScoreDB = openOrCreateDatabase("HighScoreDB", MODE_PRIVATE, null);

        // PROTO_ONLY this is just so I can fill the table up with random information to
        // ensure it works
        this.highScoreDB.execSQL("drop table if exists scoreList");
        this.highScoreDB.execSQL("create table if not exists scoreList (" +
                "id     INTEGER    PRIMARY KEY   AUTOINCREMENT, " +
                "name   VARCHAR(20)              NOT NULL, " +
                "score  INT                      NOT NULL);");

        for (int i = 0; i < 30; i++) {
            this.highScoreDB.execSQL(
                "INSERT into scoreList (name, score) VALUES (\"Bob\", " + i + ");");
        }
        Cursor cr = this.highScoreDB.rawQuery("SELECT name, score from scoreList ORDER BY score DESC LIMIT "+paginateValue, null);


        DisplayNewScores(cr);
    }


    public void prevScoreClick(View view) {
        if (this.currentOffset == 0) {
            return;
        }

        this.currentOffset--;

        Cursor cr = this.highScoreDB.rawQuery("SELECT name, score from scoreList ORDER BY score DESC LIMIT "+currentOffset*paginateValue+", "+paginateValue, null);
        DisplayNewScores(cr);
    }

    public void nextScoreClick(View view) {
        this.currentOffset++;

        // PRETTIFY: Figure out how to determine the end of the pagination and what to do with it

        Cursor cr = this.highScoreDB.rawQuery("SELECT name, score from scoreList ORDER BY score DESC LIMIT "+currentOffset*paginateValue+", "+paginateValue, null);
        DisplayNewScores(cr);
    }

    private void DisplayNewScores(Cursor cr) {
        ArrayList<String> scores = new ArrayList<String>();
        if (cr.moveToFirst()) {
            do {
                String name = cr.getString(cr.getColumnIndex("name"));
                int score = cr.getInt(cr.getColumnIndex("score"));

                Log.d("WriteDB", name+": "+score);

                scores.add(name+": "+score);
            } while (cr.moveToNext());
        }

        ListView highScoreList = (ListView) findViewById(R.id.highScoreListView);

        ArrayAdapter<String> highScoreListAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, scores);

        highScoreList.setAdapter(highScoreListAdapter);
    }
}
