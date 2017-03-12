package com.timmyho.miniminesweeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.timmyho.miniminesweeper.model.MineGrid;

import java.util.Random;

public class MinesweeperGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minesweeper_game);

        // TEST_CODE
        MineGrid myGrid = new MineGrid(10, 10, 10);


        Log.d("MineGridInit", myGrid.GetMineGridToString());

        // TEST_CODE, will need to replace this with the ui
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            myGrid.ClickMineCell(rand.nextInt(10), rand.nextInt(10));
        }
        Log.d("MineGridInit", myGrid.GetMineGridToString());

    }
}
