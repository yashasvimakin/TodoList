package com.yashasvi.cloudmagic;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by makin on 31/10/16.
 */

public class CustomCursorAdapter extends CursorAdapter {

    public CustomCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.single_row_item, parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views
        String s1,s2;
        TextView textViewPersonName = (TextView) view.findViewById(R.id.tv_person_name);
        textViewPersonName.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));

        TextView textViewPersonPIN = (TextView) view.findViewById(R.id.tv_person_pin);
        if(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(4))) == null)
            s1 = "";
        else
            s1 = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(4)));

        if(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5))) == null)
            s2 = "";
        else
            s2 = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5)));

        textViewPersonPIN.setText(s1+" "+s2);
    }
}
