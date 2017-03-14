package com.timmyho.miniminesweeper.model;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by timot on 3/10/2017.
 */

public class MineGrid {
    private List<ArrayList<MineCell>> mineGrid;
    private int numRows;
    private int numCols;
    private int numMines;
    private GameState gameState;

    // techinically a misnomer because one click on a 0 will "expand"
    // This is meant to talk about the number of uncovered cells
    // CODE_SMELL? pick a better name
    private int exposedCells;

    public enum GameState { NEWGAME, STARTED, LOST, WON }

    public MineGrid(int rows, int cols, int numMines) {
        this.numRows = rows;
        this.numCols = cols;
        this.numMines = numMines;

        GenerateMineGrid();
    }

    private void GenerateMineGrid() {
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

        this.PlaceMines();

        this.CalculateSurroundingMines();
        this.gameState = GameState.NEWGAME;
        this.exposedCells = 0;
    }

    public GameState GetGameState() {
        return this.gameState;
    }

    private void PlaceMines() {
        Random rand = new Random(0);

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

    private void CalculateSurroundingMines() {
        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numCols; j++) {
                this.mineGrid.get(i).get(j).setNumSurroundingMines(CalculateSurroundingMinesForMineCell(i, j));
            }
        }
    }

    private int CalculateSurroundingMinesForMineCell(int row, int col) {
        int surroundingMines = 0;

        // CODESMELL: feel this could be cleaner Use the dsurrounding thing to create it
        if (row > 0) {
            // TOP LEFT
            if (col > 0) {
                surroundingMines += this.mineGrid.get(row - 1).get(col - 1).getIsMine() ? 1 : 0;
            }
            // TOP RIGHT
            if (col < this.numCols - 1) {
                surroundingMines += this.mineGrid.get(row - 1).get(col + 1).getIsMine() ? 1 : 0;
            }
            // TOP MIDDLE
            surroundingMines += this.mineGrid.get(row - 1).get(col).getIsMine() ? 1 : 0;
        }

        if (row < this.numRows - 1) {
            // BOTTOM LEFT
            if (col > 0) {
                surroundingMines += this.mineGrid.get(row + 1).get(col - 1).getIsMine() ? 1 : 0;
            }
            // BOTTOM RIGHT
            if (col < this.numCols - 1) {
                surroundingMines += this.mineGrid.get(row + 1).get(col + 1).getIsMine() ? 1 : 0;
            }
            // BOTTOM MIDDLE
            surroundingMines += this.mineGrid.get(row + 1).get(col).getIsMine() ? 1 : 0;
        }

        // MIDDLE LEFT
        if (col > 0) {
            surroundingMines += this.mineGrid.get(row).get(col - 1).getIsMine() ? 1 : 0;
        }
        if (col < this.numCols - 1) {
            surroundingMines += this.mineGrid.get(row).get(col + 1).getIsMine() ? 1 : 0;
        }

        return surroundingMines;
    }

    // CODE_SMELL: could possibly override ToString?
    public String GetMineGridToString() {
        String gridAsString = "";
        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numCols; j++) {
                // CODE_SMELL: could be a place for an enum/string literal
                String mineChar = "";
                if (this.mineGrid.get(i).get(j).getCellState() == MineCell.CellState.UNCLICKED) {
                    mineChar = "U";
                }
                else {
                    if (this.mineGrid.get(i).get(j).getIsMine() == true) {
                        mineChar = "M";
                    } else {
                        mineChar = this.mineGrid.get(i).get(j).getNumSurroundingMines().toString();
                    }
                }
                gridAsString += mineChar+" ";
            }
            gridAsString += "\n";
        }
        return gridAsString;
    }

    public List<String> GetMineGridToStringArray() {
        List<String> mineGridAsStringArray = new ArrayList<String>();

        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numCols; j++) {
                // CODE_SMELL: could be a place for an enum/string literal
                String mineChar = "";
                if (this.mineGrid.get(i).get(j).getCellState() == MineCell.CellState.UNCLICKED) {
                    mineChar = "U";
                }
                else {
                    if (this.mineGrid.get(i).get(j).getIsMine() == true) {
                        mineChar = "M";
                    } else {
                        mineChar = this.mineGrid.get(i).get(j).getNumSurroundingMines().toString();
                    }
                }
                mineGridAsStringArray.add(mineChar);
            }
        }

        return mineGridAsStringArray;
    }

    // TODO_POSS maybe return a value for what the result of clicking this cell is
    public void ClickMineCell(int i, int j) {
        // Error Checking to make sure there is a valid item;
        if (i < 0 || i >= this.numRows || j < 0 || j >= this.numCols) {
            return;
        }

        // Don't want the user to die on the initial click
        if (this.gameState == GameState.NEWGAME) {
            while (this.mineGrid.get(i).get(j).getIsMine()) {
                GenerateMineGrid();
            }

            this.gameState = GameState.STARTED;
        }

        if (this.gameState == GameState.STARTED) {
            // Now check the cell itself
            MineCell clickedCell = this.mineGrid.get(i).get(j);
            if (clickedCell.getCellState() == MineCell.CellState.UNCLICKED) {
                if (clickedCell.getIsMine()) {
                    clickedCell.setCellState(MineCell.CellState.CLICKED);
                    this.gameState = GameState.LOST;
                }
                else {
                    if (clickedCell.getNumSurroundingMines() == 0) {
                        // TEMPORARY DISABLE, this is the next step;
                        ExpandExposedCells(i, j);
                    } else {
                        clickedCell.setCellState(MineCell.CellState.CLICKED);
                        this.exposedCells++;
                    }

                    if (this.numRows * this.numCols - this.numMines == this.exposedCells) {
                        this.gameState = GameState.WON;
                    }

                    Log.d("What", this.numRows+", "+this.numCols+": "+this.numCols+ " remaining: "+ this.exposedCells);
                }
            }
        }
    }

    private List<GridIndex> GetSurroundingCells(int row, int col) {
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

    // Add
    private void ExpandExposedCells(int row, int col) {
        List<GridIndex> checkCandidates = new ArrayList<GridIndex>();

        checkCandidates.add(new GridIndex(row, col));

        while (checkCandidates.size() > 0) {
            List<GridIndex> surroundingCells = GetSurroundingCells(checkCandidates.get(0).row, checkCandidates.get(0).col);
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
}
