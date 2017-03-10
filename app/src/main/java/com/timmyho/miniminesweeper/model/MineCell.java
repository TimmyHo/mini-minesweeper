package com.timmyho.miniminesweeper.model;

/**
 * Created by timot on 3/10/2017.
 */

public class MineCell {
    boolean isMine;
    int numSurroundingMines;
    CellState cellState;

    enum CellState { CLICKED, UNCLICKED, FLAGGED }
}
