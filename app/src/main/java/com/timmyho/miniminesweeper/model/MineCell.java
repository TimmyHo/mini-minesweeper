package com.timmyho.miniminesweeper.model;

/**
 * Created by timot on 3/10/2017.
 */

public class MineCell {
    boolean isMine;
    int numSurroundingMines;
    CellState cellState;

    enum CellState { UNCLICKED, CLICKED, FLAGGED }

    public MineCell(boolean isMine) {
        this.isMine = isMine;
        this.numSurroundingMines = -1;
        this.cellState = CellState.UNCLICKED;
    }

    public boolean getIsMine() {
        return this.isMine;
    }
}
