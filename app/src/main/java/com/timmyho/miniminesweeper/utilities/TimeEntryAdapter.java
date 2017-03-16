package com.timmyho.miniminesweeper.utilities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.timmyho.miniminesweeper.R;
import com.timmyho.miniminesweeper.model.TimeEntry;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by timot on 3/17/2017.
 */

public class TimeEntryAdapter extends BaseAdapter {
    private Context mContext;
    List<TimeEntry> entries;
    int pageSize;

    public TimeEntryAdapter(Context c, List<TimeEntry> entries, int pageSize) {
        mContext = c;
        this.entries = entries;
        this.pageSize = pageSize;
    }

    public long getItemId(int position) {
        return 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public int getCount() {
        return (int) (Math.ceil(this.entries.size() / (double)pageSize)*pageSize);

    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.time_entry_item, null);

        if (position < this.entries.size()) {
            Log.d("position", "position is: "+position+ ": "+this.entries.get(position).name+", "+this.entries.get(position).timeTaken.toString());

            TextView nameForTimeEntry = (TextView) v.findViewById(R.id.nameForTimeEntry);
            nameForTimeEntry.setText(this.entries.get(position).name);

            TextView timeForTimeEntry = (TextView) v.findViewById(R.id.timeForTimeEntry);
            timeForTimeEntry.setText(this.entries.get(position).timeTaken.toString());
        }

        return v;
    }
}
