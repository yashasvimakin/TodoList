package com.yashasvi.cloudmagic;



import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.util.ArrayList;

/**
 * Created by makin on 02/11/16.
 */

public class ListProvider implements RemoteViewsFactory {
    private ArrayList<ListItem> listItemList = new ArrayList<ListItem>();
    private Context context = null;
    private int appWidgetId;
    private PersonDatabaseHelper databaseHelper;
    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        populateListItem();
    }

    private void populateListItem() {
        databaseHelper = new PersonDatabaseHelper(context);
        Cursor cursor = databaseHelper.getAllData();
        int count=0;
        if (cursor.moveToFirst()) {
            do {
                count++;
                String name = cursor.getString(1);
                String reminder_time = cursor.getString(5);
                String reminder_date = cursor.getString(4);
                if(reminder_date == null){
                    reminder_date ="";
                }
                if(reminder_time == null){
                    reminder_time ="";
                }
                ListItem listItem = new ListItem();
                listItem.heading = name;
                listItem.content = reminder_date+" "+reminder_time;
                listItemList.add(listItem);
                //This I use to create listlayout dynamically and show all the Titles in it
            } while (cursor.moveToNext() && count <5);
            //populateWidget();
        }
//        if(FetchService.listItemList !=null )
//            listItemList = (ArrayList<ListItem>) FetchService.listItemList
//                    .clone();
//        else
//            listItemList = new ArrayList<ListItem>();
    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     *Similar to getView of Adapter where instead of View
     *we return RemoteViews
     *
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.list_row);
        ListItem listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.heading, listItem.heading);
        remoteView.setTextViewText(R.id.content, listItem.content);

        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }

}
