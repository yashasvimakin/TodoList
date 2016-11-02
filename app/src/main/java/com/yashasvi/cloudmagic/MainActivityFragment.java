package com.yashasvi.cloudmagic;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private CustomCursorAdapter customAdapter;
    private PersonDatabaseHelper databaseHelper;
    private static final int ENTER_DATA_REQUEST_CODE = 1;
    private ListView listView;
    private Context thiscontext;
    private static final String TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        thiscontext = getActivity();
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        //super.onActivityCreated(savedInstanceState);
        databaseHelper = new PersonDatabaseHelper(thiscontext);

        listView = (ListView) getView().findViewById(R.id.list_data);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "clicked on item: " + position);
                String selected = ((TextView) view.findViewById(R.id.tv_person_name)).getText().toString();
                Cursor c = databaseHelper.getData(selected);
                Intent intent = new Intent(thiscontext, EnterDataActivity.class);
                c.moveToFirst();
                String ids = c.getString(0);
                String name = c.getString(1);
                String decription = c.getString(2);
                String reminder_time = c.getString(5);
                String reminder_date = c.getString(4);
                Log.d(TAG,"Details :" +ids+" "+name+" "+decription+" "+reminder_date+" "+reminder_time);
                intent.putExtra("id",ids);
                intent.putExtra("name",name);
                intent.putExtra("description",decription);
                intent.putExtra("reminder_time",reminder_time);
                intent.putExtra("reminder_date",reminder_date);
                Cursor cimage = databaseHelper.getimages(name);
                Bitmap retreivedImageObject;
                //cimage.moveToFirst();
                ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
                if (cimage .moveToFirst()) {
                    do {
                        byte[] image = cimage.getBlob(2);
                        retreivedImageObject = BitmapFactory.decodeByteArray(image, 0, image.length);
                        bitmapArray.add(retreivedImageObject);
                        //This I use to create listlayout dynamically and show all the Titles in it
                    } while (cimage.moveToNext());
                }
                intent.putParcelableArrayListExtra("images",bitmapArray);
                startActivityForResult(intent, ENTER_DATA_REQUEST_CODE);
            }
        });

        // Database query can be a time consuming task ..
        // so its safe to call database query in another thread
        // Handler, will handle this stuff for you <img src="http://s0.wp.com/wp-includes/images/smilies/icon_smile.gif?m=1129645325g" alt=":)" class="wp-smiley">

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                customAdapter = new CustomCursorAdapter(thiscontext, databaseHelper.getAllData());
                listView.setAdapter(customAdapter);
            }
        });
    }

    public void onClickEnterData(View btnAdd) {

        startActivityForResult(new Intent(thiscontext, EnterDataActivity.class), ENTER_DATA_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        databaseHelper = new PersonDatabaseHelper(thiscontext);
        if (requestCode == ENTER_DATA_REQUEST_CODE && resultCode == RESULT_OK ) {

            if(data.getExtras().getInt("check") == 1){

                databaseHelper.updateData(data.getExtras().getString("tag_person_name"), data.getExtras().getString("tag_person_desciption"), data.getExtras().getString("tag_person_date"), data.getExtras().getString("tag_person_time"));
            }
            else {
                databaseHelper.insertData(data.getExtras().getString("tag_person_name"), data.getExtras().getString("tag_person_desciption"), data.getExtras().getString("tag_person_date"), data.getExtras().getString("tag_person_time"));
//                ArrayList<Bitmap> images = data.getExtras().getParcelableArrayList("images");
//                databaseHelper.insertImage(data.getExtras().getString("tag_person_name"), images);

            }
            //customAdapter.changeCursor(databaseHelper.getAllData());
        }
    }
}
