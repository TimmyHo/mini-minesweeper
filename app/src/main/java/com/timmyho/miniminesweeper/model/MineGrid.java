package com.timmyho.miniminesweeper.model;

import java.util.ArrayList;
import java.util.List;

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

        for (int i = 0; i < this.numRows; i++) {
            ArrayList<MineCell> mineRow = new ArrayList<MineCell>();
            for (int j = 0; j < this.numCols; j++) {
                // TEMP_TEST only to ensure the mines work, this needs to be replaced by a way to
                // randomly generate the mine field;
                boolean isMine = false;
                if ((i+j) % 4 == 0) {
                    isMine = true;
                }
                MineCell cell = new MineCell(isMine);

                mineRow.add(cell);
            }
            this.mineGrid.add(mineRow);
        }
    }

    // CODE_SMELL: could possibly override ToString?
    public String GetMineGridToString() {
        String gridAsString = "";
        for (int i = 0; i < this.numRows; i++) {
            for (int j = 0; j < this.numCols; j++) {
                // CODE_SMELL: could be a place for an enum/string literal
                String mineChar = "";
                if (this.mineGrid.get(i).get(j).getIsMine() == true)
                {
                    mineChar = "M";
                }
                else
                {
                    mineChar = "O";
                }
                gridAsString += mineChar+" ";
            }
            gridAsString += "\n";
        }
        return gridAsString;
    }
}
