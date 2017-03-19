package com.timmyho.miniminesweeper.utilities;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.timmyho.miniminesweeper.R;
import com.timmyho.miniminesweeper.model.TimeEntry;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Created by timot on 3/17/2017.
 */

public class TimeEntryAdapter extends BaseAdapter {
    private Context mContext;
    private List<TimeEntry> entries;
    private int currentOffset;
    private int pageSize;


    private List<Integer> medalImages = Arrays.asList(R.drawable.medal_1st, R.drawable.medal_2nd, R.drawable.medal_3rd);


    public TimeEntryAdapter(Context c, List<TimeEntry> entries,  int currentOffset, int pageSize) {
        mContext = c;
        this.entries = entries;
        this.currentOffset = currentOffset;
        this.pageSize = pageSize;
    }

    public long getItemId(int position) {
        return 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public int getCount() {
        // In the case that there are 0 entries, still want to show a similar UI
        if (this.entries.size() == 0) {
            return this.pageSize;
        }

        return (int) (Math.ceil(this.entries.size() / (double)pageSize)*pageSize);

    }
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.time_entry_item, null);

        if (position < this.entries.size()) {
            // Add a medal for the top 3 times
            if (this.currentOffset*this.pageSize + position < medalImages.size()) {
                ImageView medalPlaceImage = (ImageView) v.findViewById(R.id.medalPlaceImage);

                medalPlaceImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                medalPlaceImage.setImageResource(medalImages.get(position));
            }

            TextView nameForTimeEntry = (TextView) v.findViewById(R.id.nameForTimeEntry);
            nameForTimeEntry.setText(this.entries.get(position).name);

            TextView timeForTimeEntry = (TextView) v.findViewById(R.id.timeForTimeEntry);
            timeForTimeEntry.setText(this.entries.get(position).timeTaken.toString());
        }

        return v;
    }
}
