package com.timmyho.miniminesweeper.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by timot on 3/10/2017.
 */

public class MineCell implements Parcelable {
    private boolean isMine;
    private int numSurroundingMines;
    private CellState cellState;

    enum CellState { UNCLICKED, CLICKED, CLICKED_LOST, FLAGGED, FLAGGED_WRONG }

    public MineCell(boolean isMine) {
        this.isMine = isMine;
        this.numSurroundingMines = -1;
        this.cellState = CellState.UNCLICKED;
    }

    // Parcelable implementation so it can be successfully saved and restored on orientation change
    // Used in MinesweeperGame.OnSaveInstanceState and OnRestoreInstanceState

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.isMine ? 1 : 0);
        parcel.writeInt(this.numSurroundingMines);
        parcel.writeSerializable(this.cellState);
    }

    public static final Parcelable.Creator<MineCell> CREATOR
            = new Parcelable.Creator<MineCell>() {
                public MineCell createFromParcel(Parcel in) {
                    return new MineCell(in);
                }

                public MineCell[] newArray(int size) {
                    return new MineCell[size];
                }
            };

    public MineCell(Parcel in) {
        this.isMine = (in.readInt() == 1) ? true : false;
        this.numSurroundingMines = in.readInt();
        this.cellState = (CellState) in.readSerializable();
    }

    // End of Parcelable implementation

    public Boolean getIsMine() {
        return this.isMine;
    }

    public Integer getNumSurroundingMines() {
        return this.numSurroundingMines;
    }

    public void setNumSurroundingMines(Integer numSurroundingMines) {
        this.numSurroundingMines = numSurroundingMines;
    }

    public CellState getCellState()
    {
        return this.cellState;
    }

    public void setCellState(CellState cellState)
    {
        this.cellState = cellState;
    }
}
