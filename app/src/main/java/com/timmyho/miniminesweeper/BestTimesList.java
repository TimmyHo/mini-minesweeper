package com.timmyho.miniminesweeper;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.timmyho.miniminesweeper.helpers.BestTimesDatabase;
import com.timmyho.miniminesweeper.model.TimeEntry;
import com.timmyho.miniminesweeper.utilities.ClearTimesDialogFragment;
import com.timmyho.miniminesweeper.utilities.TimeEntryAdapter;

import java.util.List;

public class BestTimesList extends AppCompatActivity {
    private BestTimesDatabase bestTimesDB;
    private int totalNumTimeEntries = 0;
    private int currentOffset = 0;
    private int pageSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_times_list);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        long timeTaken = intent.getLongExtra("timeTaken", -1);

        bestTimesDB = BestTimesDatabase.GetInstance(this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // This is pretty ugly because have to check the listview height compared to the item
            // height and know how many items to show
            // It is also repeated for the (in all honesty this should be a fragment).

            View bestTimesLayout = findViewById(R.id.bestTimesLayout);
            bestTimesLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                // Note this is slightly different compared to miniTimesList because it also
                // includes paging (maybe could bake that into a fragment)
                @Override
                public void onLayoutChange(
                    View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

                    view.removeOnLayoutChangeListener(this);

                    ListView bestTimesListView = (ListView) view;

                    int listHeight = bestTimesListView.getHeight();
                    int divHeight = bestTimesListView.getDividerHeight();
                    int itemHeight =
                        (int) (TimeEntryAdapter.TimeEntryItemHeightInDP *
                        getResources().getDisplayMetrics().density);

                    pageSize = (listHeight + divHeight) / (itemHeight + divHeight);

                    setNumTimeEntries();
                    displayNewTimes();
                }
            });
        }

        // Add the hold listener to the "clear times" button to add mock data
        Button clearTimesButton = (Button) findViewById(R.id.clearTimesButton);

        clearTimesButton.setOnLongClickListener(new Button.OnLongClickListener() {
            public boolean onLongClick(View v) {
                bestTimesDB.AddMockData();
                displayNewTimes();
                return true;
            }
        });

        if (name != "" && timeTaken != -1) {
            // TODO_REFACTOR_POSS make the score/time accurate down to ms or just make it an int
            bestTimesDB.InsertTimeEntry(name, (int)timeTaken);
            Toast.makeText(this.getBaseContext(), name+": "+timeTaken+" added to \"Best Times\"", Toast.LENGTH_SHORT).show();
        }

        setNumTimeEntries();
        displayNewTimes();
    }

    private void setNumTimeEntries() {
        this.totalNumTimeEntries = bestTimesDB.GetNumTimeEntries();
    }

    // ARCHITECT Should ClearTimesDialogFragment be a class so this is a private?
    public void ClearTimes() {
        bestTimesDB.ClearAllTimes();

        this.currentOffset = 0;
        this.totalNumTimeEntries = 0;

        displayNewTimes();
    }

    private void displayNewTimes() {
        List<TimeEntry> timeEntries = bestTimesDB.GetData(this.currentOffset, this.pageSize);

        ListView bestTimesList = (ListView) findViewById(R.id.bestTimesListView);

        TimeEntryAdapter timeEntryAdapter = new TimeEntryAdapter(
                this, timeEntries, this.currentOffset, this.pageSize);

        bestTimesList.setAdapter(timeEntryAdapter);

        enableOrDisablePagingButtons();
    }

    private void enableOrDisablePagingButtons() {
        Button prevTimesButton = (Button) findViewById(R.id.prevTimesButton);
        prevTimesButton.setEnabled(this.currentOffset > 0);

        Button nextTimesButton = (Button) findViewById(R.id.nextTimesButton);
        nextTimesButton.setEnabled((this.currentOffset + 1) * this.pageSize <= this.totalNumTimeEntries);

        Log.d("values", String.format("co, ps, tnte => %d, %d, %d",
            this.currentOffset, this.pageSize, this.totalNumTimeEntries));
    }

    public void PrevTimesClick(View view) {
        this.currentOffset--;

        displayNewTimes();
    }

    public void NextTimesClick(View view) {
        this.currentOffset++;

        displayNewTimes();
    }

    public void ClearTimesClick(View view) {
        DialogFragment newFragment = new ClearTimesDialogFragment();

        newFragment.show(getFragmentManager(), "clearTimes");
    }
}
