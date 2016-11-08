package com.yashasvi.cloudmagic;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by makin on 31/10/16.
 */

public class EnterDataActivity extends AppCompatActivity implements
        View.OnClickListener{
    // Image loading result to pass to startActivityForResult method.
    private static int LOAD_IMAGE_RESULTS = 1;
    LinearLayout linearLayout1;
    private CustomCursorAdapter customAdapter;
    private PersonDatabaseHelper databaseHelper;
    // GUI components
    // This is a handle so that we can call methods on our service
    private ScheduleClient scheduleClient;

    private Button button;	// The button
    private ImageView image;// ImageView
    EditText editTextPersonName;
    EditText editTextPersionDescription;
    Button btnDatePicker, btnTimePicker;
    TextView txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    ArrayList<Bitmap> bitmapArray;
    String ids,name,description="",reminder_date,reminder_time;
    Bundle b;
    int check = 0;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        bitmapArray = new ArrayList<Bitmap>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createtask);
        linearLayout1 = (LinearLayout) findViewById(R.id.imageLayout);
        // Find references to the GUI objects
        button = (Button)findViewById(R.id.button);
        image = (ImageView)findViewById(R.id.image);
        databaseHelper = new PersonDatabaseHelper(this);
        editTextPersonName = (EditText) findViewById(R.id.et_person_name);
        editTextPersionDescription = (EditText) findViewById(R.id.et_person_pin);
        btnDatePicker=(Button)findViewById(R.id.btn_date);
        btnTimePicker=(Button)findViewById(R.id.btn_time);
        txtDate=(TextView)findViewById(R.id.in_date);
        txtTime=(TextView)findViewById(R.id.in_time);
        button.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        scheduleClient = new ScheduleClient(this);
        scheduleClient.doBindService();
        Intent iin= getIntent();
        b = iin.getExtras();

        if(b!=null) {
           ids =(String) b.get("id");
            name =(String) b.get("name");
            description =(String)b.get("description");
            reminder_date=(String)b.get("reminder_date");
            reminder_time = (String)b.get("reminder_time");
            //bitmapArray = b.getParcelableArrayList("images");
            Cursor cimage = databaseHelper.getimages(name);
            Bitmap retreivedImageObject;
            //cimage.moveToFirst();
            if (cimage .moveToFirst()) {
                do {
                    byte[] image = cimage.getBlob(2);
                    retreivedImageObject = BitmapFactory.decodeByteArray(image, 0, image.length);
                    bitmapArray.add(retreivedImageObject);
                    //This I use to create listlayout dynamically and show all the Titles in it
                } while (cimage.moveToNext());
            }
            editTextPersonName.setText(name);
            editTextPersionDescription.setText(description);
            txtDate.setText(reminder_date);
            txtTime.setText(reminder_time);
            check = 1;
            editTextPersonName.setFocusable(false);
            if(bitmapArray !=null) {
                for (Bitmap image : bitmapArray) {
                    Bitmap converetdImage = getResizedBitmap(image, 300);
                    // Log.d(TAG, String.valueOf(bitmap));
                    ImageView imageview = new ImageView(EnterDataActivity.this);
                    imageview.setImageBitmap(converetdImage);
                    linearLayout1.addView(imageview);
                }
            }
        }
    }

    public void onClickAdd (View btnAdd) {

        String personName = editTextPersonName.getText().toString();
        String personDescription = editTextPersionDescription.getText().toString();
        String personDate = txtDate.getText().toString();
        String personTime = txtTime.getText().toString();

        if ( personName.length() != 0 && personDescription.length() != 0 ) {
                Cursor cursor = databaseHelper.verifyUniqueTask(personName);
                if((cursor.getCount() != 0 && check != 1 )|| (description.compareTo(personDescription) == 0 && (reminder_time == null && personDate.isEmpty() && reminder_date ==null && personTime.isEmpty()))){
                    Toast.makeText(EnterDataActivity.this, "This Task Name Already Present Change Description/Reminder to Save!",
                            Toast.LENGTH_LONG).show();
                }else{
                Intent newIntent = getIntent();
                newIntent.putExtra("tag_person_name", personName);
                newIntent.putExtra("tag_person_desciption", personDescription);
                newIntent.putExtra("tag_person_date", personDate);
                newIntent.putExtra("tag_person_time", personTime);
                //newIntent.putParcelableArrayListExtra("images",bitmapArray);
                newIntent.putExtra("check", check);
                this.setResult(RESULT_OK, newIntent);
                Calendar calendar = Calendar.getInstance();
                calendar.set(mYear, mMonth, mDay, mHour, mMinute);
                if(personDate.length() != 0  || personTime.length() != 0){
                    scheduleClient.setAlarmForNotification(calendar);
                    Toast.makeText(this, "Notification set for: "+ mDay +"/"+ mMonth +"/"+ mYear+","+ mHour +"/"+ mMinute, Toast.LENGTH_SHORT).show();
                }

                //createNotification();
                finish();
            }
//            if(cursor.getCount() == 0){
//
//            }else {
//
//            }
        }
    }
    private static final int NOTIFY_ME_ID=1337;
    public void createNotification() {
        // Prepare intent which is triggered if the
        // notification is selected
        String personName = editTextPersonName.getText().toString();
        String personDescription = editTextPersionDescription.getText().toString();
        String personDate = txtDate.getText().toString();
        String personTime = txtTime.getText().toString();
        Intent intent = new Intent(this, EnterDataActivity.class);
        intent.putExtra("name", personName);
        intent.putExtra("description", personDescription);
        intent.putExtra("reminder_date", personDate);
        intent.putExtra("reminder_time", personTime);
        intent.putExtra("check",check);
        //intent.putParcelableArrayListExtra("images",bitmapArray);
        Context context = getBaseContext();
        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, mDay, mHour, mMinute);
        //scheduleClient.setAlarmForNotification(calendar);
        //Intent notificationIntent = new Intent(context, <the-activity-you-need-to-call>.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.todo)
                .setContentTitle(personName)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[] { 1000, 1000})
                .setWhen(calendar.getTimeInMillis())
                .setContentText(description)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NOTIFY_ME_ID, mBuilder.build());
        }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                databaseHelper = new PersonDatabaseHelper(this);
                Bitmap converetdImage = getResizedBitmap(bitmap, 300);

                // Log.d(TAG, String.valueOf(bitmap));
                //bitmapArray.add(converetdImage);
                databaseHelper.insertImage(editTextPersonName.getText().toString(),Bitmap.createScaledBitmap(bitmap, 300, 300, true));
                ImageView image = new ImageView(EnterDataActivity.this);
                image.setImageBitmap(converetdImage);
                Toast.makeText(EnterDataActivity.this, "Image Save To your Task!",
                        Toast.LENGTH_LONG).show();
                editTextPersonName.setFocusable(false);
                linearLayout1.addView(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    Calendar c;
    @Override
    public void onClick(View v) {
        if(v == button){
            String personName = editTextPersonName.getText().toString();
            if(personName.length() == 0){
                Toast.makeText(this, "Please Enter the Task Name First ", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
                startActivityForResult(i, LOAD_IMAGE_RESULTS);
            }
        }
        if (v == btnDatePicker) {

            // Get Current Date
            c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            mYear = year;
                            mMonth=monthOfYear;
                            mDay=dayOfMonth;
                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            mHour=hourOfDay;
                            mMinute = minute;
                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
    @Override
    protected void onStop() {
        // When our activity is stopped ensure we also stop the connection to the service
        // this stops us leaking our activity into the system *bad*
        if(scheduleClient != null)
            scheduleClient.doUnbindService();
        super.onStop();
    }
}
