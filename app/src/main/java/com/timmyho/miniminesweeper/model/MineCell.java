package com.timmyho.miniminesweeper.model;

/**
 * Created by timot on 3/10/2017.
 */

public class MineCell {
    Boolean isMine;
    Integer numSurroundingMines;
    CellState cellState;

    enum CellState { UNCLICKED, CLICKED, FLAGGED }

    public MineCell(Boolean isMine) {
        this.isMine = isMine;
        this.numSurroundingMines = -1;
        this.cellState = CellState.UNCLICKED;
    }

    public Boolean getIsMine() {
        return this.isMine;
    }

    public void setNumSurroundingMines(Integer numSurroundingMines) {
        this.numSurroundingMines = numSurroundingMines;
    }

    public Integer getNumSurroundingMines() {
        return this.numSurroundingMines;
    }
}
