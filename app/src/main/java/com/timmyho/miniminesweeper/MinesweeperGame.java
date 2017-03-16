package com.timmyho.miniminesweeper;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.timmyho.miniminesweeper.model.MineGrid;
import com.timmyho.miniminesweeper.utilities.BestTimeEntryDialogFragment;
import com.timmyho.miniminesweeper.utilities.ImageAdapter;

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

        updateMinesweeperGrid();

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

        minesweeperUI.setNumColumns(this.numCols);
        minesweeperUI.setColumnWidth(minesweeperUI.getWidth()/minesweeperUI.getNumColumns());

        minesweeperUI.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                myGrid.ClickMineCell(position / numCols, position % numCols);
                updateMinesweeperGrid();
                }
        });

        minesweeperUI.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                myGrid.FlagMineCell(position / numCols, position % numCols);
                updateMinesweeperGrid();
                return true;
            }
        });
    }


    public void ResetMinesweeperGameClick(View view) {
        myGrid = new MineGrid(numRows, numCols, numMines);
        updateMinesweeperGrid();
    }

    private void updateMinesweeperGrid() {
        Log.d("MineGridInit", myGrid.GetMineGridToString());

        GridView minesweeperUI = (GridView) findViewById(R.id.minesweeperUI);

        int colWidth = Resources.getSystem().getDisplayMetrics().widthPixels/numCols;
        ImageAdapter minesweeperUiAdapter = new ImageAdapter(this, myGrid.GetMineGridAsImageIds(), colWidth);
        minesweeperUI.setAdapter(minesweeperUiAdapter);

        TextView numMinesText = (TextView) findViewById(R.id.numMinesText);
        numMinesText.setText(String.valueOf(this.numMines - myGrid.GetNumFlaggedCells()));

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

            showTimeEntryDialog();
        } else if (gameState == MineGrid.GameState.LOST){
            gameStateText.setTextColor(Color.RED);
        }
    }

    private void showTimeEntryDialog() {
        DialogFragment newFragment = new BestTimeEntryDialogFragment();
        Bundle data = new Bundle();
        data.putLong("timeTaken", this.myGrid.GetTimeTaken());
        newFragment.setArguments(data);

        newFragment.show(getFragmentManager(), "highScore");
    }

    public void GoToBestTimesClick(View view) {
        Intent intent = new Intent(this, HighScoreList.class);

        startActivity(intent);
    }
}
