package com.example.reminderstuff;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.reminderstuff.ReminderStuff.SetReminder;

public class MainActivity extends AppCompatActivity {
    Button btn_reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_reminder = findViewById(R.id.btn_reminder);
        btn_reminder.setOnClickListener(v -> {
            new SetReminder(this,
                    "reminder_title",
                    "reminder_text" + " ",
                    "",
                    "").show();
        });
    }
}