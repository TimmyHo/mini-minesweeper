package com.timmyho.miniminesweeper.utilities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.BaseAdapter;

import com.timmyho.miniminesweeper.R;

/**
 * Created by timot on 3/14/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private int columnWidth;

    public ImageAdapter(Context c, int columnWidth) {
        mContext = c;
        this.columnWidth = columnWidth;
    }

    public long getItemId(int position) {
        return 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public int getCount() {
        //PROTO_ONLY
        return 100;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            Log.d("colWidth", "column width = "+this.columnWidth);

            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(this.columnWidth, this.columnWidth));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(R.drawable.mine);
        return imageView;
    }
}
