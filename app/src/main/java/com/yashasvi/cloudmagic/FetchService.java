package com.yashasvi.cloudmagic;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;

import java.util.ArrayList;

/**
 * Created by makin on 05/11/16.
 */

public class FetchService extends Service {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private PersonDatabaseHelper databaseHelper;
    public static ArrayList<ListItem> listItemList;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /*
     * Retrieve appwidget id from intent it is needed to update widget later
     * initialize our AQuery class
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        databaseHelper = new PersonDatabaseHelper(getBaseContext());
        fetchData();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * method which fetches data(json) from web aquery takes params
     * remoteJsonUrl = from where data to be fetched String.class = return
     * format of data once fetched i.e. in which format the fetched data be
     * returned AjaxCallback = class to notify with data once it is fetched
     */
    private void fetchData() {
        listItemList = new ArrayList<ListItem>();
        Cursor cursor = databaseHelper.getAllData();
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(1);
                String reminder_time = cursor.getString(5);
                String reminder_date = cursor.getString(4);
                ListItem listItem = new ListItem();
                listItem.heading = name;
                listItem.content = reminder_date+" "+reminder_time;
                listItemList.add(listItem);
                //This I use to create listlayout dynamically and show all the Titles in it
            } while (cursor.moveToNext());
            populateWidget();
        }

    }

    /**
     * Method which sends broadcast to WidgetProvider
     * so that widget is notified to do necessary action
     * and here action == WidgetProvider.DATA_FETCHED
     */
    private void populateWidget() {

        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(WidgetProvider.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        sendBroadcast(widgetUpdateIntent);

        this.stopSelf();
    }
}
