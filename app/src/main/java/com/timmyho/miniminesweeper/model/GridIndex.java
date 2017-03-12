package com.timmyho.miniminesweeper.model;

/**
 * Created by timot on 3/12/2017.
 */


// CLEANLINESS Make this readonly pubic or only allowed to set once?
    // NEED TO override other functions?
public class GridIndex {
    public int row;
    public int col;

    public GridIndex(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
