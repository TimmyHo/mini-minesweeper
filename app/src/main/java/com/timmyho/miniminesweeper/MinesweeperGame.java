package com.timmyho.miniminesweeper;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
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
import com.timmyho.miniminesweeper.utilities.BestTimeEntryDialogFragment;
import com.timmyho.miniminesweeper.utilities.ImageAdapter;

import org.w3c.dom.Text;

import java.util.Random;

public class MinesweeperGame extends AppCompatActivity {

    // CODE_SMELL? Maybe this needs to be put in a model or a singleton instance
    MineGrid myGrid;
    private int numRows = 10;
    private int numCols = 10;
    private int numMines = 10;

    private long maxTime = 999;

    final Handler timerHandler = new Handler();
    Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minesweeper_game);

        myGrid = new MineGrid(numRows, numCols, numMines);
        initializeMinesweeperUI();

        UpdateMinesweeperGrid();

        // This seems a bit weird, because I am keeping two background tasks, one for the game logic
        // (in MineGrid.java) and then another one here (to update the UI). It makes the structure
        // better (since there is no play, pause, stop, etc... here but may not be the best use of
        // resources
        timerRunnable = new Runnable() {

            @Override
            public void run() {
                long timeDisplayed = Math.min(myGrid.GetTimeTaken(), maxTime);

                TextView timerText = (TextView) findViewById(R.id.timerText);
                timerText.setText(String.format("%03d", timeDisplayed));

                timerHandler.postDelayed(this, 1000);
            }

        };
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void initializeMinesweeperUI() {
        GridView minesweeperUI = (GridView) findViewById(R.id.minesweeperUI);

        minesweeperUI.setNumColumns(numCols);
        minesweeperUI.setColumnWidth(minesweeperUI.getWidth()/minesweeperUI.getNumColumns());

        minesweeperUI.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Log.d("clickCheck","I'm clicking at ["+position/numCols+", "+position % numCols+"]");
                myGrid.ClickMineCell(position / numCols, position % numCols);
                UpdateMinesweeperGrid();
                }
        });

        minesweeperUI.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Log.d("clickCheck", "I'm flagging at [" + position / numCols + ", " + position % numCols + "]");
                myGrid.FlagMineCell(position / numCols, position % numCols);
                UpdateMinesweeperGrid();
                return true;
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
        myGrid = new MineGrid(numRows, numCols, numMines);
        UpdateMinesweeperGrid();
    }

    private void UpdateMinesweeperGrid() {
        Log.d("MineGridInit", myGrid.GetMineGridToString());

        ArrayAdapter<String> minesweeperUIAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, myGrid.GetMineGridToStringArray());

        GridView minesweeperUI = (GridView) findViewById(R.id.minesweeperUI);
        minesweeperUI.setAdapter(minesweeperUIAdapter);

        ImageAdapter newAdapter = new ImageAdapter(this, this.myGrid.GetMineGridAsImageIds(), Resources.getSystem().getDisplayMetrics().widthPixels/numCols);
        minesweeperUI.setAdapter(newAdapter);


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

        TextView numMinesText = (TextView) findViewById(R.id.numMinesText);

        numMinesText.setText(String.valueOf(this.numMines-this.myGrid.GetNumFlaggedCells()));
    }

    public void goToScoresClick(View view) {
        Intent intent = new Intent(this, HighScoreList.class);

        // PROTO_ONLY just to make sure the passing of data works
        intent.putExtra("name", "Tim");
        intent.putExtra("score", 420);

        //startActivity(intent);
        DialogFragment newFragment = new BestTimeEntryDialogFragment();
        newFragment.show(getFragmentManager(), "highScore");
    }
}
