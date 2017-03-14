package com.timmyho.miniminesweeper;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HighScoreList extends AppCompatActivity {
    private SQLiteDatabase highScoreDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score_list);

        this.highScoreDB = openOrCreateDatabase("HighScoreDB", MODE_PRIVATE, null);

        this.highScoreDB.execSQL(
            "create table if not exists scores (" +
                "id     INT        PRIMARY KEY   NOT NULL, " +
                "name   VARCHAR(20)              NOT NULL, " +
                "score  INT                      NOT NULL);");
    }
}
