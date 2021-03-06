package com.timmyho.miniminesweeper.utilities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.BaseAdapter;

import com.timmyho.miniminesweeper.R;

import java.util.List;

/**
 * Created by timot on 3/14/2017.
 */

public class MineCellAsImageAdapter extends BaseAdapter {
    private Context mContext;
    List<Integer> imageIds;
    private int columnWidth;

    public MineCellAsImageAdapter(Context c, List<Integer> imageIds, int columnWidth) {
        mContext = c;
        this.imageIds = imageIds;
        this.columnWidth = columnWidth;
    }

    public long getItemId(int position) {
        return 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public int getCount() {
        return imageIds.size();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(this.columnWidth, this.columnWidth));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(imageIds.get(position));
        return imageView;
    }
}
