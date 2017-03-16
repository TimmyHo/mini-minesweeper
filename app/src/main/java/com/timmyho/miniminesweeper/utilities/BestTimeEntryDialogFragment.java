package com.timmyho.miniminesweeper.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.timmyho.miniminesweeper.HighScoreList;
import com.timmyho.miniminesweeper.R;

/**
 * Created by timot on 3/16/2017.
 */

public class BestTimeEntryDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // CLEANUP "create a string class" and just reference it instead
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Congratulations! You Won!!")
                .setView(inflater.inflate(R.layout.best_time_entry, null))
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getActivity(), HighScoreList.class);

                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
