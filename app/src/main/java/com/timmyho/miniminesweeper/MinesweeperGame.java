package com.timmyho.miniminesweeper;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.timmyho.miniminesweeper.model.MineGrid;

import java.util.Random;

public class MinesweeperGame extends AppCompatActivity {

    // CODE_SMELL? Maybe this needs to be put in a model or a singleton instance
    MineGrid myGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minesweeper_game);

        // TEST_CODE
        //myGrid = new MineGrid(10, 10, 10);
        myGrid = new MineGrid(5, 5, 5);
        /*
        // TEST_CODE, will need to replace this with the ui
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            myGrid.ClickMineCell(rand.nextInt(10), rand.nextInt(10));
        }
        */
        // PROTO_ONLY
        myGrid.ClickMineCell(1,2);
        UpdateMinesweeperGrid();
    }

    public void cellClick(View view) {
        EditText rowText = (EditText) findViewById(R.id.rowText);
        EditText colText = (EditText) findViewById(R.id.colText);

        try {
            Integer rowIndex = Integer.parseInt(rowText.getText().toString());
            Integer colIndex = Integer.parseInt(colText.getText().toString());
            myGrid.ClickMineCell(rowIndex, colIndex);

            UpdateMinesweeperGrid();
        } catch (NumberFormatException ex) {
            // Do nothing, Invalid number
        }
    }

    public void resetMinesweeperGameClick(View view) {
        myGrid = new MineGrid(10, 10, 10);
        myGrid = new MineGrid(5, 5, 5);
        UpdateMinesweeperGrid();
    }

    private void UpdateMinesweeperGrid() {
        Log.d("MineGridInit", myGrid.GetMineGridToString());

        TextView minesweeperGrid = (TextView) findViewById(R.id.minesweeperGridView);
        minesweeperGrid.setText(myGrid.GetMineGridToString());

        MineGrid.GameState gameState = myGrid.GetGameState();

        TextView gameStateText = (TextView) findViewById(R.id.gameStateText);
        gameStateText.setText(gameState.toString());

        // TODO_CLEANUP: Maybe a class or something (so there's no need for so many if/switch
        // statements
        if (gameState == MineGrid.GameState.NEWGAME) {
            gameStateText.setTextColor(Color.BLACK);
        } else if (gameState == MineGrid.GameState.STARTED){
            gameStateText.setTextColor(Color.DKGRAY);
        } else if (gameState == MineGrid.GameState.WON){
            gameStateText.setTextColor(Color.GREEN);
        } else if (gameState == MineGrid.GameState.LOST){
            gameStateText.setTextColor(Color.RED);
        }
    }
}
