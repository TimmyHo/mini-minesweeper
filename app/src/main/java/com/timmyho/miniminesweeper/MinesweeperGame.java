package com.timmyho.miniminesweeper;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.timmyho.miniminesweeper.helpers.BestTimesDatabase;
import com.timmyho.miniminesweeper.model.MineGrid;
import com.timmyho.miniminesweeper.model.TimeEntry;
import com.timmyho.miniminesweeper.utilities.BestTimeEntryDialogFragment;
import com.timmyho.miniminesweeper.utilities.MineCellAsImageAdapter;
import com.timmyho.miniminesweeper.utilities.TimeEntryAdapter;

import java.util.List;

public class MinesweeperGame extends AppCompatActivity {
    private MineGrid myGrid;
    private int numRows = 10;
    private int numCols = 10;
    private int numMines = 10;
    private int colWidth = 0;

    private long maxTime = 999;

    final private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minesweeper_game);

        myGrid = new MineGrid(numRows, numCols, numMines);

        initializeMinesweeperUI();
        updateMinesweeperGrid();

        updateTimeList();

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong("timeTaken", myGrid.GetTimeTaken());
        outState.putSerializable("gameState", myGrid.GetGameState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        long timeTaken = savedInstanceState.getLong("timeTaken");
        MineGrid.GameState gameState = (MineGrid.GameState) savedInstanceState.getSerializable("gameState");

        myGrid.RestoreMineGrid(timeTaken, gameState);
        updateMinesweeperGrid();
    }

    @Override
    protected void onPause() {
        super.onPause();

        myGrid.PauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateTimeList();
        myGrid.ResumeGame();
    }

    private int shortestDim() {
        int statusBarHeight = 0;
        int titleBarHeight = 0;

        int statusBarResourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (statusBarResourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(statusBarResourceId);
        }

        int titleBarResourceId = getResources().getIdentifier("action_bar_default_height", "dimen", "android");
        if (titleBarResourceId > 0) {
            titleBarHeight = getResources().getDimensionPixelSize(titleBarResourceId);
        }

        // PRETTIFY for some reason the height is off by a few pixels.
        // Investigate at a later time
        int height = Resources.getSystem().getDisplayMetrics().heightPixels - statusBarHeight - titleBarHeight;
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;

        Log.d("heights", "w, h => "+width+", "+height);
        int shortestDim = Math.min(height, width);

        return shortestDim;
    }

    private void initializeMinesweeperUI() {
        int shortestLen = shortestDim();
        this.colWidth = shortestLen/this.numCols;

        GridView minesweeperUI = (GridView) findViewById(R.id.minesweeperUI);

        minesweeperUI.setNumColumns(this.numCols);
        minesweeperUI.setColumnWidth(this.colWidth);

        // In landscape the width takes all the screen width and pushes the other content off-screen
        // So need to manually set the width
        ViewGroup.LayoutParams params = minesweeperUI.getLayoutParams();

        params.width = shortestLen;
        minesweeperUI.setLayoutParams(params);

        minesweeperUI.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (myGrid.GetGameState() == MineGrid.GameState.NEW_GAME ||
                    myGrid.GetGameState() == MineGrid.GameState.STARTED) {

                    myGrid.ClickMineCell(position / numCols, position % numCols);
                    updateMinesweeperGrid();
                }
                }
        });

        minesweeperUI.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (myGrid.GetGameState() == MineGrid.GameState.NEW_GAME ||
                    myGrid.GetGameState() == MineGrid.GameState.STARTED) {

                    myGrid.FlagMineCell(position / numCols, position % numCols);
                    updateMinesweeperGrid();
                }
                return true;
            }
        });
    }

    // REFACTOR Maybe put this in a fragment?
    private void updateTimeList() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ListView miniBestTimesList = (ListView) findViewById(R.id.miniBestTimesList);

            int currentOffset = 0;
            int pageSize = 5;

            List<TimeEntry> timeEntries = BestTimesDatabase.GetInstance(this).GetData(currentOffset, pageSize);

            TimeEntryAdapter timeEntryAdapter = new TimeEntryAdapter(
                    this, timeEntries, currentOffset, pageSize);

            miniBestTimesList.setAdapter(timeEntryAdapter);
        }
    }

    private void updateMinesweeperGrid() {
        Log.d("MineGridInit", myGrid.GetMineGridToString());

        GridView minesweeperUI = (GridView) findViewById(R.id.minesweeperUI);

        MineCellAsImageAdapter minesweeperUiAdapter = new MineCellAsImageAdapter(this, myGrid.GetMineGridAsImageIds(), this.colWidth);
        minesweeperUI.setAdapter(minesweeperUiAdapter);

        // Update ancillary (ie: non-game) elements
        TextView numMinesText = (TextView) findViewById(R.id.numMinesText);
        numMinesText.setText(String.valueOf(this.numMines - myGrid.GetNumFlaggedCells()));

        MineGrid.GameState gameState = myGrid.GetGameState();

        TextView gameStateText = (TextView) findViewById(R.id.gameStateText);
        gameStateText.setText(gameState.toString());

        // TODO_CLEANUP: Maybe a class or something (so there's no need for so many if/switch
        // statements
        if (gameState == MineGrid.GameState.NEW_GAME) {
            gameStateText.setTextColor(Color.LTGRAY);
        } else if (gameState == MineGrid.GameState.STARTED){
            gameStateText.setTextColor(Color.BLACK);
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
        data.putLong("timeTaken", myGrid.GetTimeTaken());
        newFragment.setArguments(data);

        newFragment.show(getFragmentManager(), "bestTimes");
    }

    public void ResetMinesweeperGameClick(View view) {
        myGrid = new MineGrid(numRows, numCols, numMines);
        updateMinesweeperGrid();
    }

    public void GoToBestTimesClick(View view) {
        Intent intent = new Intent(this, BestTimesList.class);
        startActivity(intent);
    }
}
