package com.timmyho.miniminesweeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.timmyho.miniminesweeper.model.MineGrid;

public class MinesweeperGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minesweeper_game);

        // TEST_CODE
        MineGrid myGrid = new MineGrid(10, 10, 10);
        Log.d("MineGridInit", myGrid.GetMineGridToString());
    }
}
