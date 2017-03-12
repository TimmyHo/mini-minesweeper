package com.timmyho.miniminesweeper.model;

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

        // PROTO_ONLY only to ensure the mines work, this needs to be replaced by a way to
        // randomly generate the mine field;
        int numRandomMines = 0;
        Random rand = new Random();
        for (int i = 0; i < this.numRows; i++) {
            ArrayList<MineCell> mineRow = new ArrayList<MineCell>();
            for (int j = 0; j < this.numCols; j++) {
                boolean addMine = rand.nextInt(10) == 0;
                MineCell cell = new MineCell(addMine);
                numRandomMines += addMine ? 1: 0;
                mineRow.add(cell);
            }
            this.mineGrid.add(mineRow);
        }

        // PROTO_ONLY
        this.numMines = numRandomMines;

        this.CalculateSurroundingMines();
        this.gameState = GameState.NEWGAME;
        this.exposedCells = 0;
    }

    public GameState GetGameState() {
        return this.gameState;
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

        // CODESMELL: feel this could be cleaner
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

    // TODO_POSS maybe return a value for what the result of clicking this cell is
    public void ClickMineCell(int i, int j) {
        // Don't want the user to die on the initial click
        if (this.gameState == GameState.NEWGAME) {
            while (this.mineGrid.get(i).get(j).getIsMine()) {
                GenerateMineGrid();
            }

            this.gameState = GameState.STARTED;
        }
        else if (this.gameState == GameState.STARTED) {
            // Now check the cell itself
            MineCell clickedCell = this.mineGrid.get(i).get(j);
            if (clickedCell.getCellState() == MineCell.CellState.UNCLICKED) {
                clickedCell.setCellState(MineCell.CellState.CLICKED);

                if (clickedCell.getIsMine()) {
                    this.gameState = GameState.LOST;
                }
                else {
                    this.exposedCells++;
                    if (this.numRows * this.numCols - this.numMines == this.exposedCells) {
                        this.gameState = GameState.WON;
                    }
                }
            }
        }
    }
}
