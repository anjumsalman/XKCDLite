package com.stellerapps.xkcdlite;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ComicListAdapter extends CursorAdapter{

    private Cursor cursor;

    public ComicListAdapter(Context context, Cursor c) {
        super(context, c);
        cursor = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.menu_list_check, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView titleTV = (TextView) view.findViewById(R.id.listTextTV);
        titleTV.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)))+" : "+
                cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));
    }

    @Override
    public boolean hasStableIds(){
        return true;
    }
}
