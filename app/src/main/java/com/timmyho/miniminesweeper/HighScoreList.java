package com.timmyho.miniminesweeper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class HighScoreList extends AppCompatActivity {
    private SQLiteDatabase highScoreDB;

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

        for (int i = 0; i < 10; i++) {
            this.highScoreDB.execSQL(
                "INSERT into scoreList (name, score) VALUES (\"Bob\", " + i + ");");
        }
        Cursor cr = this.highScoreDB.rawQuery("SELECT name, score from scoreList", null);

        if (cr.moveToFirst()) {
            do {
                String name = cr.getString(cr.getColumnIndex("name"));
                int score = cr.getInt(cr.getColumnIndex("score"));

                Log.d("WriteDB", name+": "+score);
            } while (cr.moveToNext());
        }
    }
}
