package com.example.reminderstuff.ReminderStuff;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SetReminder {
    Context context;
    String reminder_title = "", reminder_text = "";
    String gson;
    String date_time;
    private Dialog dialog;
    private AppDatabase appDatabase;
    String arr;

    public SetReminder(Context context, String reminder_title, String reminder_text,
                       String gson, String arr) {
        this.context = context;
        this.reminder_title = reminder_title;
        this.reminder_text = reminder_text;
        this.gson = gson;
        this.arr = arr;
        appDatabase = AppDatabase.geAppdatabase(context);
    }

    public void show() {
        try {
            final Calendar newCalender = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                    final Calendar newDate = Calendar.getInstance();
                    Calendar newTime = Calendar.getInstance();
                    TimePickerDialog time = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            newDate.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                            Calendar tem = Calendar.getInstance();
                            Log.w("TIME", System.currentTimeMillis() + "");
                            if (newDate.getTimeInMillis() - tem.getTimeInMillis() > 0) {
                                date_time = newDate.getTime().toString();
                                fun_set();
                            } else {
                                Toast.makeText(context, "Invalid time", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, newTime.get(Calendar.HOUR_OF_DAY), newTime.get(Calendar.MINUTE), true);
                    time.show();

                }
            }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(System.currentTimeMillis());
            dialog.show();
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void fun_set() {
        try {
            RoomDAO roomDAO = appDatabase.getRoomDAO();
            Reminders reminders = new Reminders();
            reminders.setMessage(reminder_text);
            Date remind = new Date(date_time);
            reminders.setRemindDate(remind);
            roomDAO.Insert(reminders);
            List<Reminders> l = roomDAO.getAll();
            reminders = l.get(l.size() - 1);


            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar.setTime(remind);
            calendar.set(Calendar.SECOND, 0);
            Intent intent = new Intent(context, NotifierAlarm.class);
            intent.putExtra("Message", reminders.getMessage());
            intent.putExtra("RemindDate", reminders.getRemindDate().toString());
            intent.putExtra("Title", reminder_title);
            intent.putExtra("id", reminders.getId());
            intent.putExtra("gson", gson);
            intent.putExtra("arr", arr);

            @SuppressLint("UnspecifiedImmutableFlag")
            PendingIntent intent1 = PendingIntent.getBroadcast(context, reminders.getId(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intent1);

            AppDatabase.destroyInstance();
            Toast.makeText(context, "Inserted Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
