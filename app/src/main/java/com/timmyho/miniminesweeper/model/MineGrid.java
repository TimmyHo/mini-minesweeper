package com.timmyho.miniminesweeper.model;

import com.timmyho.miniminesweeper.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by timot on 3/10/2017.
 */

public class MineGrid {
    private List<ArrayList<MineCell>> mineGrid;
    private int numRows;
    private int numCols;
    private int numMines;

    private int randomSeed;

    private int exposedCells;
    private int flaggedCells;

    private long timeTaken;
    private Timer timer;
    private TimerTask timerTask;

    public enum GameState {NEW_GAME, STARTED, PAUSED, LOST, WON }
    private GameState gameState;

    private static List<Integer> imageIds = Arrays.asList(
            R.drawable.cell0,
            R.drawable.cell1,
            R.drawable.cell2,
            R.drawable.cell3,
            R.drawable.cell4,
            R.drawable.cell5,
            R.drawable.cell6,
            R.drawable.cell7,
            R.drawable.cell8);

    public MineGrid(int rows, int cols, int numMines) {
        this.numRows = rows;
        this.numCols = cols;
        this.numMines = numMines;
        generateMineGrid();

        this.timer = new Timer();
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                if (gameState == GameState.STARTED) {
                    timeTaken++;
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private void generateMineGrid() {
        this.mineGrid = new ArrayList<ArrayList<MineCell>>();

        int numRandomMines = 0;
        for (int i = 0; i < this.numRows; i++) {
            ArrayList<MineCell> mineRow = new ArrayList<MineCell>();
            for (int j = 0; j < this.numCols; j++) {
                MineCell cell = new MineCell(false);
                mineRow.add(cell);
            }
            this.mineGrid.add(mineRow);
        }

        Random rand = new Random();
        int seed = rand.nextInt();

        this.placeMines(seed);

        this.calculateSurroundingMines();
        this.exposedCells = 0;
        this.flaggedCells = 0;
        this.timeTaken = 0;

        this.gameState = GameState.NEW_GAME;
    }

    public void RestoreMineGrid(int seed, long timeTaken, GameState gameState) {

        // PROTO_ONLY
        this.mineGrid = new ArrayList<ArrayList<MineCell>>();

        int numRandomMines = 0;
        for (int i = 0; i < this.numRows; i++) {
            ArrayList<MineCell> mineRow = new ArrayList<MineCell>();
            for (int j = 0; j < this.numCols; j++) {
                MineCell cell = new MineCell(false);
                mineRow.add(cell);
            }
            this.mineGrid.add(mineRow);
        }

        this.placeMines(seed);

        this.calculateSurroundingMines();
        this.exposedCells = 0;
        this.flaggedCells = 0;

        // END OF PROTO_ONLY
        this.timeTaken = timeTaken;

        this.gameState = gameState;
    }

    public int GetRandomSeed() { return this.randomSeed; }

    public int GetNumFlaggedCells() { return this.flaggedCells; }

    public long GetTimeTaken() { return this.timeTaken; }

    public GameState GetGameState() {
        return this.gameState;
    }


    private void placeMines(int seed) {
        Random rand = new Random(seed);
        this.randomSeed = seed;

        // OPT? The other option (to have it = numMines) is to generate an array of all possible
        // values and then take a random number, add a mine at that index and remove it from the
        // list though this adds more space requirements
        int placedMines = 0;
        while (placedMines < this.numMines) {
            int row = rand.nextInt(this.numRows);
            int col = rand.nextInt(this.numCols);
            MineCell cell = mineGrid.get(row).get(col);

            if (!cell.getIsMine()) {
                mineGrid.get(row).set(col, new MineCell(true));
                placedMines++;
            }
        }
    }

    private List<GridIndex> getSurroundingCells(int row, int col) {
        List<GridIndex> surroundingCellIndexList = new ArrayList<GridIndex>();

        // CODESMELL: feel this could be cleaner, not sre if I really need a GridIndex
        if (row > 0) {
            // TOP LEFT
            if (col > 0) {
                surroundingCellIndexList.add(new GridIndex(row - 1, col - 1));
            }
            // TOP RIGHT
            if (col < this.numCols - 1) {
                surroundingCellIndexList.add(new GridIndex(row - 1, col + 1));
            }
            // TOP MIDDLE
            surroundingCellIndexList.add(new GridIndex(row - 1, col));
        }
        if (row < this.numRows - 1) {
            // BOTTOM LEFT
            if (col > 0) {
                surroundingCellIndexList.add(new GridIndex(row + 1, col - 1));
            }
            // BOTTOM RIGHT
            if (col < this.numCols - 1) {
                surroundingCellIndexList.add(new GridIndex(row + 1, col + 1));
            }
            // BOTTOM MIDDLE
            surroundingCellIndexList.add(new GridIndex(row + 1, col));
        }

        // MIDDLE LEFT
        if (col > 0) {
            surroundingCellIndexList.add(new GridIndex(row, col - 1));
        }
        // MIDDLE RIGHT
        if (col < this.numCols - 1) {
            surroundingCellIndexList.add(new GridIndex(row, col + 1));
        }

        return surroundingCellIndexList;
    }

    private void calculateSurroundingMines() {
        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numCols; j++) {
                this.mineGrid.get(i).get(j).setNumSurroundingMines(calculateSurroundingMinesForMineCell(i, j));
            }
        }
    }

    private int calculateSurroundingMinesForMineCell(int row, int col) {
        int surroundingMines = 0;

        List<GridIndex> surroundingCells = getSurroundingCells(row, col);

        for (int i = 0; i < surroundingCells.size(); i++) {
            MineCell checkCell = this.mineGrid.get(surroundingCells.get(i).row).get(surroundingCells.get(i).col);

            surroundingMines += checkCell.getIsMine() ? 1 : 0;
        }

        return surroundingMines;
    }

    // TODO_POSS maybe return a value for what the result of clicking this cell is
    public void ClickMineCell(int i, int j) {
        // Error Checking to make sure there is a valid item;
        if (i < 0 || i >= this.numRows || j < 0 || j >= this.numCols) {
            return;
        }

        // Don't want the user to die on the initial click
        if (this.gameState == GameState.NEW_GAME) {
            while (this.mineGrid.get(i).get(j).getIsMine()) {
                generateMineGrid();
            }

            this.gameState = GameState.STARTED;
        }

        if (this.gameState == GameState.STARTED) {
            // Now check the cell itself
            MineCell clickedCell = this.mineGrid.get(i).get(j);
            if (clickedCell.getCellState() == MineCell.CellState.UNCLICKED) {
                if (clickedCell.getIsMine()) {
                    clickedCell.setCellState(MineCell.CellState.CLICKED_LOST);

                    this.gameState = GameState.LOST;
                    showMinesAfterGameOver();
                }
                else {

                    clickedCell.setCellState(MineCell.CellState.CLICKED);
                    this.exposedCells++;
                    if (clickedCell.getNumSurroundingMines() == 0) {
                        expandExposedCells(i, j);
                    }

                    if (this.numRows * this.numCols - this.numMines == this.exposedCells) {
                        this.gameState = GameState.WON;
                        showMinesAfterGameOver();
                    }
                }
            }
        }
    }

    private void expandExposedCells(int row, int col) {
        List<GridIndex> checkCandidates = new ArrayList<GridIndex>();

        checkCandidates.add(new GridIndex(row, col));

        while (checkCandidates.size() > 0) {
            List<GridIndex> surroundingCells = getSurroundingCells(checkCandidates.get(0).row, checkCandidates.get(0).col);
            for (int i = 0; i < surroundingCells.size(); i++) {
                MineCell checkCell = this.mineGrid.get(surroundingCells.get(i).row).get(surroundingCells.get(i).col);

                if (checkCell.getCellState() == MineCell.CellState.UNCLICKED) {
                    checkCell.setCellState(MineCell.CellState.CLICKED);
                    this.exposedCells++;

                    if (checkCell.getNumSurroundingMines() == 0) {
                        checkCandidates.add(new GridIndex(surroundingCells.get(i).row, surroundingCells.get(i).col));
                    }
                }
            }

            MineCell candidateCell = this.mineGrid.get(checkCandidates.get(0).row).get(checkCandidates.get(0).col);
            // A 0th one in the second loop (already added
            checkCandidates.remove(0);
        }
    }

    private void showMinesAfterGameOver() {
        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numCols; j++) {
                MineCell cell = this.mineGrid.get(i).get(j);

                if (cell.getIsMine() && cell.getCellState() == MineCell.CellState.UNCLICKED) {
                    if (gameState == GameState.LOST) {
                        cell.setCellState(MineCell.CellState.CLICKED);
                    } else {
                        cell.setCellState(MineCell.CellState.FLAGGED);
                    }
                }
                if (cell.getCellState() == MineCell.CellState.FLAGGED && !cell.getIsMine()) {
                    cell.setCellState(MineCell.CellState.FLAGGED_WRONG);
                }
            }
        }
    }

    public void FlagMineCell(int i, int j) {
        // Error Checking to make sure there is a valid item;
        if (i < 0 || i >= this.numRows || j < 0 || j >= this.numCols) {
            return;
        }

        MineCell selectedCell = this.mineGrid.get(i).get(j);

        if (selectedCell.getCellState() == MineCell.CellState.FLAGGED) {
            selectedCell.setCellState(MineCell.CellState.UNCLICKED);
            this.flaggedCells--;
        } else if (selectedCell.getCellState() == MineCell.CellState.UNCLICKED) {
            selectedCell.setCellState(MineCell.CellState.FLAGGED);
            this.flaggedCells++;
        }
    }

    // CODE_SMELL: could possibly override ToString?
    // CLEANUP Decide if we need these, if we don't then delete, else clean them up
    // Missing flag and clicked on mine
    public String GetMineGridToString() {
        String gridAsString = "";

        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numCols; j++) {
                MineCell currentCell = this.mineGrid.get(i).get(j);
                // CODE_SMELL: could be a place for an enum/string literal or
                // a mapping between string rep and image_id rep.
                // However this is for mostly diagnostics purposes so I've chosen to get to this
                // later (if warrented)
                String cellChar = "U";
                if (currentCell.getCellState() == MineCell.CellState.UNCLICKED) {
                    cellChar = "U";
                } else if (currentCell.getCellState() == MineCell.CellState.FLAGGED) {
                    cellChar = "F";
                } else if (currentCell.getCellState() == MineCell.CellState.FLAGGED_WRONG) {
                    cellChar = "N";
                } else {
                    if (currentCell.getIsMine() == true) {
                        if (currentCell.getCellState() == MineCell.CellState.CLICKED_LOST) {
                            cellChar = "X";
                        } else {
                            cellChar = "M";
                        }
                    } else {
                        cellChar = currentCell.getNumSurroundingMines().toString();
                    }
                }
                gridAsString += cellChar + " ";
            }
            gridAsString += "\n";
        }

        return gridAsString;
    }

    public List<Integer> GetMineGridAsImageIds() {
        List<Integer> mineGridAsImageIdList = new ArrayList<Integer>();

        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numCols; j++) {
                MineCell currentCell = this.mineGrid.get(i).get(j);
                // CODE_SMELL: could be a place for an enum/string literal
                Integer cellId = R.drawable.unclicked;
                if (currentCell.getCellState() == MineCell.CellState.UNCLICKED) {
                    cellId = R.drawable.unclicked;
                } else if (currentCell.getCellState() == MineCell.CellState.FLAGGED) {
                    cellId = R.drawable.flag;
                } else if (currentCell.getCellState() == MineCell.CellState.FLAGGED_WRONG) {
                    cellId = R.drawable.no_mine;
                } else {
                    if (currentCell.getIsMine() == true) {
                        if (currentCell.getCellState() == MineCell.CellState.CLICKED_LOST) {
                            cellId = R.drawable.mine_clicked;
                        } else {
                            cellId = R.drawable.mine;
                        }
                    } else {
                        cellId = MineGrid.imageIds.get(currentCell.getNumSurroundingMines());
                    }
                }
                mineGridAsImageIdList.add(cellId);
            }
        }

        return mineGridAsImageIdList;
    }

    public void PauseGame() {
        if (this.gameState == GameState.STARTED) {
            this.gameState = GameState.PAUSED;
        }
    }

    public void ResumeGame() {
        if (this.gameState == GameState.PAUSED) {
            this.gameState = GameState.STARTED;
        }
    }
}
