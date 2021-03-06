package com.example.calendar;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is the class which allows the user to add an event
 * to the Calendar App. This includes the functionality of
 * adding functionality for the UI and Database as well.
 */
public class AddEventActivity extends Activity {
    EventDatabase eventdb;

    @Override
    /**
     * Creates the UI and Database information as soon as
     * this program is started
     *
     * @param savedInstanceState This allows the layout to become saved
     *                           and show the user what they need to insert
     *                           for adding an activity
      */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        eventdb = new EventDatabase(this);
        eventdb.getWritableDatabase();
        final EditText etTitle = findViewById(R.id.txtTitle);
        final EditText etTag = findViewById(R.id.txtTag);
        final EditText etDate = findViewById(R.id.txtDate);
        final EditText etTime = findViewById(R.id.txtStart);                    //creates a reference to the View for future use
        final EditText etDetails = findViewById(R.id.txtDetails);
        final EditText etColor = findViewById(R.id.txtColor);


        final Button submitButton = findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Clicked!");
                //Check for each important field
                if (isEmpty(etTitle) || isEmpty(etDate) || isEmpty(etTime)) {
                    System.out.println("Empty textfield");
                }
                else {
                    //Create the correct event date format
                    String date = etDate.getText().toString();
                    String []dateArr = date.split("/");
                    String typedMonth = dateArr[0];
                    String typedDay = dateArr[1];
                    String typedYear = dateArr[2];

                    date = typedYear + "-" + typedMonth + "-" + typedDay;                       //gets the data from the fields and creates an event to be stored in the database
                    String timeEventID = date + "-" + etTime.getText().toString();
                    String title = etTitle.getText().toString();
                    String tag = etTag.getText().toString();
                    String details = etDetails.getText().toString();
                    String color = etColor.getText().toString();
                    String time = etTime.getText().toString();
                    int sort = selectedSort;
                    eventdb.addEvent(title, timeEventID, date, tag, details, color, time, sort);
                    Toast toast = Toast.makeText(getApplicationContext(), "Successfully added event", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                }
            }
        });

        etTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            /**
             * This allows the user to insert the time for the event.
             * This will go into the event database
             *
             * @param v Shows the view for the time
             * @param hasFocus A condition when the time should be chosen
             */
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    final TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                            etTime.setText((hourOfDay % 12 == 0 ? 12 : hourOfDay % 12) + ":" + (minutes < 10 ? "0" + minutes : minutes) + (hourOfDay < 12 ? "AM" : "PM"));

                            selectedSort = (hourOfDay < 10 ? 1 : (hourOfDay <= 13 ? 2 : (hourOfDay < 22 ? 3 : (hourOfDay < 24 ? 4 : 5))));
                        }
                    }, 0, 0, false);
                    timePickerDialog.show();
                }
            }
        });
    }

    @Override
    /**
     * Happens after onCreate where the user starts to go into the add event window
      */
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        selectedDay = intent.getIntExtra("day", 01);
        selectedYear = intent.getIntExtra("year", 2000);
        selectedMonth = intent.getIntExtra("month", 01);
        String autoFillDate = (selectedMonth + "/" + selectedDay + "/" + selectedYear);
        EditText dateText = findViewById(R.id.txtDate);
        dateText.setText(autoFillDate);
    }

    /**
     * This detects if the text fields are empty and also moves it to default
     * if nothing is changed, submitted, or added
     *
     * @param et This will help with editing text to reset to default
     * @return this returns all the values to become empty
     */
    private boolean isEmpty(EditText et) {
        return et.getText().toString().trim().length() == 0;
    }

    private int selectedDay;
    private int selectedYear;
    private int selectedMonth;
    private int selectedSort;
}
