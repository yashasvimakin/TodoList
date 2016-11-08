package com.yashasvi.cloudmagic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by makin on 31/10/16.
 */

public class PersonDatabaseHelper {

    private static final String TAG = PersonDatabaseHelper.class.getSimpleName();

    // database configuration
    // if you want the onUpgrade to run then change the database_version
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mydatabase.db";

    // table configuration
    private static final String TABLE_NAME = "task_table";         // Table name
    private static final String TABLE_IMAGE = "image_table";
    private static final String PERSON_TABLE_COLUMN_ID = "_id";     // a column named "_id" is required for cursor
    private static final String PERSON_TABLE_COLUMN_NAME = "task_name";
    private static final String IMAGE_TABLE_COLUMN_NAME = "task_name";
    private static final String PERSON_TABLE_IMAGE = "task_image";
    private static final String PERSON_TABLE_DESCRIPTION = "task_description";
    private static final String PERSON_TABLE_REMINDER_DATE = "task_reminder_date";
    private static final String PERSON_TABLE_REMINDER_TIME = "task_reminder_time";
    private static final String PERSON_TABLE_COLUMN_PIN = "person_pin";

    private DatabaseOpenHelper openHelper;
    private SQLiteDatabase database;

    // this is a wrapper class. that means, from outside world, anyone will communicate with PersonDatabaseHelper,
    // but under the hood actually DatabaseOpenHelper class will perform database CRUD operations
    public PersonDatabaseHelper(Context aContext) {

        openHelper = new DatabaseOpenHelper(aContext);
        database = openHelper.getWritableDatabase();
    }
    private String getDateTime(String date,String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm", Locale.getDefault());
        Date datetime = new Date( date + " "+time);
        return dateFormat.format(date);
    }
    public void insertData (String aPersonName, String aPersonPin, String date,String time) {

        // we are using ContentValues to avoid sql format errors

        ContentValues contentValues = new ContentValues();
        contentValues.put(PERSON_TABLE_COLUMN_NAME, aPersonName);
        contentValues.put(PERSON_TABLE_DESCRIPTION, aPersonPin);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dates = new Date();
        contentValues.put("date_created", dateFormat.format(dates));
        if(date.length() != 0){
            contentValues.put(PERSON_TABLE_REMINDER_DATE,date);
        }
        if(time.length() != 0){
            contentValues.put(PERSON_TABLE_REMINDER_TIME,time+":00");
        }

        database.insert(TABLE_NAME, null, contentValues);
    }
    public Cursor getData(String row){
        String buildSQL = "SELECT * FROM " + TABLE_NAME + " Where task_name = '"+row+"'";
        Log.d(TAG, "getAllData SQL: " + buildSQL);
        Cursor data = database.rawQuery(buildSQL, null);
        Log.d(TAG, String.valueOf(data.getCount()));
        return data;
    }
    public Cursor getimages(String row){
        String buildSQL = "SELECT * FROM " + TABLE_IMAGE + " Where "+ IMAGE_TABLE_COLUMN_NAME+" ='"+row+"'";
        Log.d(TAG, "getAllData SQL: " + buildSQL);
        Cursor data = database.rawQuery(buildSQL, null);
        Log.d(TAG, String.valueOf(data.getCount()));
        return data;
    }
    public void insertImage (String aPersonName, Bitmap image){
        ContentValues contentimages = new ContentValues();
            contentimages.put(PERSON_TABLE_COLUMN_NAME, aPersonName);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytesArray = stream.toByteArray();

            Bitmap newObject = BitmapFactory.decodeByteArray(bytesArray, 0, bytesArray.length);
            contentimages.put(PERSON_TABLE_IMAGE, bytesArray);
            try {
                database.insertOrThrow(TABLE_IMAGE, null, contentimages);
            } catch (SQLiteException e) {
                Log.e("ymakin", e.toString());
            }
            Cursor cursor = getimages(aPersonName);
            cursor.moveToFirst();
            byte[] retreivedImage = cursor.getBlob(2);
            Bitmap retreivedImageObject = BitmapFactory.decodeByteArray(bytesArray, 0, bytesArray.length);

    }

    public Cursor verifyUniqueTask (String taskname) throws SQLiteException{

        String buildSQL = "SELECT * FROM " + TABLE_NAME + " WHERE " +PERSON_TABLE_COLUMN_NAME+ " = '"+taskname+"'";

        Log.d(TAG, "verifyUniqueTask SQL: " + buildSQL);

        return database.rawQuery(buildSQL, null);
    }

    public Cursor getAllData () {

        String buildSQL = "SELECT * FROM " + TABLE_NAME + " ORDER BY date("+PERSON_TABLE_REMINDER_DATE+"),time("+PERSON_TABLE_REMINDER_TIME+")";

        Log.d(TAG, "getAllData SQL: " + buildSQL);

        return database.rawQuery(buildSQL, null);
    }
    public void updateData(String aPersonName, String aPersonPin, String date,String time) {

        // we are using ContentValues to avoid sql format errors
        String where = PERSON_TABLE_COLUMN_NAME+"=?";
        String[] whereArgs = new String[] {String.valueOf(aPersonName)};
        ContentValues contentValues = new ContentValues();
        contentValues.put(PERSON_TABLE_COLUMN_NAME, aPersonName);
        contentValues.put(PERSON_TABLE_DESCRIPTION, aPersonPin);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dates = new Date();
        contentValues.put("date_created", dateFormat.format(dates));
        if(date.length() != 0){
            contentValues.put(PERSON_TABLE_REMINDER_DATE,date);
        }
        if(time.length() != 0){
            contentValues.put(PERSON_TABLE_REMINDER_TIME,time+":00");
        }

        database.update(TABLE_NAME,contentValues,where,whereArgs);
    }

    // this DatabaseOpenHelper class will actually be used to perform database related operation

    private class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context aContext) {
            super(aContext, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            // Create your tables here

            String buildSQL = "CREATE TABLE " + TABLE_NAME + "( " + PERSON_TABLE_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    PERSON_TABLE_COLUMN_NAME + " TEXT, " + PERSON_TABLE_DESCRIPTION +  " TEXT, " +"date_created date,"+ PERSON_TABLE_REMINDER_DATE+" date,"+PERSON_TABLE_REMINDER_TIME +" time )";

            Log.d(TAG, "onCreate SQL: " + buildSQL);
            String imageSQL = "CREATE TABLE " + TABLE_IMAGE + "( " + PERSON_TABLE_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    IMAGE_TABLE_COLUMN_NAME +  " TEXT, " + PERSON_TABLE_IMAGE +" BLOB);";
            sqLiteDatabase.execSQL(buildSQL);
            sqLiteDatabase.execSQL(imageSQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            // Database schema upgrade code goes here

            String buildSQL = "DROP TABLE IF EXISTS " + TABLE_NAME;

            Log.d(TAG, "onUpgrade SQL: " + buildSQL);

            sqLiteDatabase.execSQL(buildSQL);       // drop previous table

            onCreate(sqLiteDatabase);               // create the table from the beginning
        }
    }
}
