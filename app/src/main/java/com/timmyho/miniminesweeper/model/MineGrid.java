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

    public MineGrid(int rows, int cols, int numMines) {
        this.numRows = rows;
        this.numCols = cols;
        this.mineGrid = new ArrayList<ArrayList<MineCell>>();

        // PROTO_ONLY only to ensure the mines work, this needs to be replaced by a way to
        // randomly generate the mine field;
        Random rand = new Random();
        for (int i = 0; i < this.numRows; i++) {
            ArrayList<MineCell> mineRow = new ArrayList<MineCell>();
            for (int j = 0; j < this.numCols; j++) {
                MineCell cell = new MineCell(rand.nextInt(10) == 0);
                mineRow.add(cell);
            }
            this.mineGrid.add(mineRow);
        }

        this.CalculateSurroundingMines();
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

    // TODO_POSS mahbe return a value for what the result of clicking this cell is
    public void ClickMineCell(int i, int j) {
        // TODO: This should check if it is flagged or clicked already and not do anything if it is
        this.mineGrid.get(i).get(j).setCellState(MineCell.CellState.CLICKED);
    }
}
