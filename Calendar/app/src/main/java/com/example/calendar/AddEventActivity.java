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

public class AddEventActivity extends Activity {
    EventDatabase eventdb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        eventdb = MainActivity.eventdb;
        final EditText etTitle = findViewById(R.id.txtTitle);
        final EditText etTag = findViewById(R.id.txtTag);
        final EditText etDate = findViewById(R.id.txtDate);
        final EditText etTime = findViewById(R.id.txtStart);
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
                    String date = etDate.getText().toString().replace("/", "-");
                    String timeEventID = date + "-" + etTime.getText().toString();
                    String title = etTitle.getText().toString();
                    String tag= etTag.getText().toString();
                    String details = etDetails.getText().toString();
                    String color = etColor.getText().toString();

                    long l = eventdb.addEvent(title, timeEventID, date, tag, details, color);
                    System.out.println("ROWS INSERTED: " + l);
                    Cursor c = eventdb.getEventForDate(date);
                    c.moveToFirst();
                    for (int i = 0; i < c.getColumnCount() - 1; i++) {
                        System.out.println("COL NAME: " + c.getColumnName(i));
                        System.out.println("COL VAL: " + c.getString(i));
                    }
                }

            }
        });

        etTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    final TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                            etTime.setText((hourOfDay % 12 == 0 ? 12 : hourOfDay % 12) + ":" + (minutes < 10 ? "0" + minutes : minutes) + (hourOfDay < 12 ? "AM" : "PM"));
                        }
                    }, 0, 0, false);
                    timePickerDialog.show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        int selectedDay = intent.getIntExtra("day", 01);
        int selectedYear = intent.getIntExtra("year", 2000);
        int selectedMonth = intent.getIntExtra("month", 01);
        String autoFillDate = (selectedMonth + "/" + selectedDay + "/" + selectedYear);
        EditText dateText = findViewById(R.id.txtDate);
        dateText.setText(autoFillDate);
    }

    private boolean isEmpty(EditText et) {
        return et.getText().toString().trim().length() == 0;
    }

}
