package com.example.probono;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

public class HistoryCalendar extends AppCompatActivity {

    private TextView whatday_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_calendar);


        MaterialCalendarView materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        materialCalendarView.setSelectedDate(CalendarDay.today()); // 오늘 날짜로 선택되어 시작

        // 날짜 선택 시 화면 전환
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull @org.jetbrains.annotations.NotNull MaterialCalendarView widget, @NonNull @org.jetbrains.annotations.NotNull CalendarDay date, boolean selected) { //클릭이벤트


                Intent intent01 = new Intent(getApplicationContext(), Record.class);
                intent01.putExtra("cal", "" + date);
                startActivity(intent01);

//                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View view = inflater.inflate(R.layout.record, null);



//                //activity_sub.xml layout에 존재하는 Button, TextView 객체 생성(얻기)
//                whatday_log = (TextView)view.findViewById(R.id.textCalendar);
//                whatday_log.setText("" + date);
//
//                Intent intent = new Intent(getApplicationContext(),Record.class);
//                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "" + date, Toast.LENGTH_SHORT).show();
//                Toast.makeText(Record(), "" + date, Toast.LENGTH_SHORT).show();

            }
        });


    }
}