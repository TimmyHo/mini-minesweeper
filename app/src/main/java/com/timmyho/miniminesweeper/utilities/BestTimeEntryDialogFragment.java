package com.timmyho.miniminesweeper.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.timmyho.miniminesweeper.BestTimesList;
import com.timmyho.miniminesweeper.R;

/**
 * Created by timot on 3/16/2017.
 */

public class BestTimeEntryDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle data = getArguments();
        final long timeTaken = (data != null) ? data.getLong("timeTaken", 999) : 999;

        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // CLEANUP "create a string class" and just reference it instead
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View v = inflater.inflate(R.layout.best_time_entry, null);
        TextView wonTimeText = (TextView) v.findViewById(R.id.wonTimeText);
        wonTimeText.setText(String.valueOf(timeTaken));

        builder.setTitle("Congratulations! You Won!! ")
                .setView(v)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getActivity(), BestTimesList.class);

                        EditText nameEntryText = (EditText) v.findViewById(R.id.nameEntryText);
                        String nameEntry = nameEntryText.getText().toString();

                        intent.putExtra("name", nameEntry);
                        intent.putExtra("timeTaken", timeTaken);

                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
