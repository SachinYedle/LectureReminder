package com.example.sachin.lecturereminder;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sachin.lecturereminder.dbModel.classData;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class AddClassActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText nameEditText,topicEditText,professorEditText,locationEditText;
    private static TextView dateTextView,timeTextView;
    private DatabaseAccess databaseAccess;
    private Button save;
    int year,month,day,hour,minute;
    private String operation;
    private static final int PLACE_PICKER_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        /** get database access class Object*/
        databaseAccess = DatabaseAccess.getDatabaseAccess(AddClassActivity.this);

        /** initialize views*/
        save = (Button)findViewById(R.id.save);
        save.setOnClickListener(this);
        locationEditText = (EditText) findViewById(R.id.location);
        locationEditText.setFocusable(false);
        locationEditText.setOnClickListener(this);
        nameEditText = (EditText)findViewById(R.id.name);
        topicEditText = (EditText)findViewById(R.id.topic);
        professorEditText = (EditText)findViewById(R.id.professor);
        dateTextView = (TextView)findViewById(R.id.date);
        dateTextView.setOnClickListener(this);
        timeTextView = (TextView)findViewById(R.id.time);
        timeTextView.setOnClickListener(this);
        operation = getIntent().getStringExtra("operation");

        /** initialize and set - up toolbar*/
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /** checking activities purpose used for adding class and for updating*/
        if(operation.equals("add")) {
            getSupportActionBar().setTitle("Add Class");
        }else if(operation.equals("edit")){
            getSupportActionBar().setTitle("Modify Class");
            addDataToFields();
        }
    }

    /** if activity used of updating class fill the fields*/
    public void addDataToFields(){
        String name = getIntent().getStringExtra("name");
        String topic = getIntent().getStringExtra("topic");
        String professor = getIntent().getStringExtra("professor");
        String location = getIntent().getStringExtra("location");
        String date = "Date: "+getIntent().getStringExtra("date");
        String time = "Time: "+getIntent().getStringExtra("time");
        nameEditText.setText(name);
        topicEditText.setText(topic);
        dateTextView.setText(date);
        timeTextView.setText(time);
        professorEditText.setText(professor);
        locationEditText.setText(location);
        save.setText("Update");
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.date:
                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.show(getFragmentManager(), "datePicker");
                break;
            case R.id.location:
                takeLoaction();
                break;
            case R.id.time:
                DialogFragment timeFragment = new TimePickerFragment();
                timeFragment.show(getFragmentManager(), "timePicker");
                break;
            case R.id.save:
                String name = nameEditText.getText().toString();
                String topic = topicEditText.getText().toString();
                String professor = professorEditText.getText().toString();
                String timeWithString = timeTextView.getText().toString();
                String time = timeWithString.substring(5,timeWithString.length());
                Log.i("time",time);
                String dateWithString = dateTextView.getText().toString();
                String date = dateWithString.substring(6,dateWithString.length());
                Log.i("date",date);
                String location = locationEditText.getText().toString();
                boolean isValid = validateData(name,topic,professor,time,date,location);
                if(isValid){
                    setAlarm();
                    if(operation.equals("add")) {
                        callToInsertClass(name, topic, professor, date, time, location);
                    }else if(operation.equals("edit")){
                        callToUpdateClass(name, topic, professor, date, time, location);
                    }
                }
        }
    }

    public void takeLoaction() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("place picker activity", e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("Place picker activity", e.getMessage());
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("%s", place.getName());
                Double latitude = place.getLatLng().latitude;
                Double longitude = place.getLatLng().longitude;
                //Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                locationEditText.setText(toastMsg);
            }
        }
    }
    /** insert class data to database*/
    public void callToInsertClass(String name, String topic,String professor,String date,String time,String location){
        databaseAccess.insertClass(name, topic, professor, date, time, location);
        Intent addDataIntent = new Intent(AddClassActivity.this, MainActivity.class);
        startActivity(addDataIntent);
    }

    /**update class data to database*/
    public void callToUpdateClass(String name, String topic,String professor,String date,String time,String location){
        String id = getIntent().getStringExtra("id");
        databaseAccess.updateClass(id, name, topic, professor, date, time, location);
        Intent editDatantent = new Intent(AddClassActivity.this, MainActivity.class);
        startActivity(editDatantent);
    }

    /** class data validation*/
    public boolean validateData(String name,String topic,String professor,String time,String date, String location){
        boolean isValid = true;
        if(name.isEmpty() || name.length() < 3){
            nameEditText.setError("at least 3 characters");
            isValid = false;
        }else if (topic.isEmpty()){
            topicEditText.setError("Should Not Null");
            isValid = false;
        }else if(professor.isEmpty() || professor.length() < 3){
            professorEditText.setError("at least 3 characters");
            isValid = false;
        }else if (time.length() > 12){
            timeTextView.setError("please pick time");
            Toast.makeText(AddClassActivity.this,"Please Select Time",Toast.LENGTH_SHORT).show();
            isValid = false;
        }else if (date.length() > 16){
            dateTextView.setError("please pick date");
            Toast.makeText(AddClassActivity.this,"Please Select Date",Toast.LENGTH_SHORT).show();
            isValid = false;
        }else if(location.isEmpty()){
            locationEditText.setError("Please select Location");
            isValid = false;
        }
        return isValid;
    }

    /**time picker inner class to pick time*/
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            AddClassActivity activity = (AddClassActivity) getActivity();
            activity.setHour(hourOfDay);
            activity.setMinute(minute);
            String time = "Time: " + hourOfDay + ":" + minute;
            timeTextView.setText(time);
        }
    }


    /**Date picker inner class to pick date*/
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            AddClassActivity activity = (AddClassActivity) getActivity();
            activity.setYear(year);
            activity.setMonth(month);
            activity.setDay(day);
            month = month+1;
            String date = "Date: " + year + "-" + month + "-" + day;
            dateTextView.setText(date);
        }
    }

    /**set up date for alarm*/
    public void setAlarm(){
        Calendar current = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(getYear(),getMonth(),getDay(),getHour(),getMinute(),00);
        Log.i("Year",""+getMonth()+getYear()+getHour()+getMinute());
        if(calendar.compareTo(current) <= 0){
            //Toast.makeText(AddClassActivity.this, "Past Date/Time", Toast.LENGTH_LONG).show();
        }else{
            setAlarmWithDate(calendar);
        }
    }
    /**set data with given date*/
    private void setAlarmWithDate(Calendar targetCal){
        String data = "Alarm is set@ " + targetCal.getTime();
        Log.i("Alarm Set @",data);
        Random r = new Random();
        int alarmRequestCode = r.nextInt(100 - 1) + 1;
        Log.i("requestCode",""+alarmRequestCode);
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("name",nameEditText.getText().toString());
        intent.putExtra("topic",topicEditText.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmRequestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        intentArray.add(pendingIntent);

    }

    /**Setting private fields*/
    public void setHour(int hour) {
        this.hour = hour;
        Log.i("year hour","HH"+hour);
    }

    public void setMinute(int minute) {
        this.minute = minute;
        Log.i("year ","MM"+minute);
    }

    public void setDay(int day) {
        this.day = day;
        Log.i("year hour","day"+day);
    }

    public void setMonth(int month) {
        Log.i("year hour","month"+month);
        this.month = month;
    }

    public void setYear(int year) {
        Log.i("year hour","year"+year);
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }
}
