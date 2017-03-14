package com.timmyho.miniminesweeper;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

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
        myGrid = new MineGrid(10, 10, 10);
        initializeMinesweeperUI();

        // PROTO_ONLY
//        myGrid.ClickMineCell(1,2);
        UpdateMinesweeperGrid();
    }

    private void initializeMinesweeperUI() {
        GridView minesweeperUI = (GridView) findViewById(R.id.minesweeperUI);

        minesweeperUI.setNumColumns(10);
        minesweeperUI.setColumnWidth(minesweeperUI.getWidth()/minesweeperUI.getNumColumns());

        minesweeperUI.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Log.d("clickCheck","I'm clicking at ["+position/10+", "+position % 10+"]");
                myGrid.ClickMineCell(position / 10, position % 10);
                UpdateMinesweeperGrid();
                }
        });
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
            // PRETTIFY cleanup stuff return toast message
        }
    }

    public void resetMinesweeperGameClick(View view) {
        myGrid = new MineGrid(10, 10, 10);
        UpdateMinesweeperGrid();
    }

    private void UpdateMinesweeperGrid() {
        Log.d("MineGridInit", myGrid.GetMineGridToString());

        ArrayAdapter<String> minesweeperUIAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, myGrid.GetMineGridToStringArray());
//
        GridView minesweeperUI = (GridView) findViewById(R.id.minesweeperUI);
        minesweeperUI.setAdapter(minesweeperUIAdapter);

/*        TextView minesweeperGrid = (TextView) findViewById(R.id.minesweeperGridView);
        minesweeperGrid.setText(myGrid.GetMineGridToString());
*/
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
