package com.example.probono;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //모니터링 버튼 클릭시 이동
        Button btn_monitoring = findViewById(R.id.monitoring);

        btn_monitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Monitoring.class);
                startActivity(intent);
            }
        });

        // 기록 버튼 클릭시 이동
        Button btn_record = findViewById(R.id.record);

        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Record.class);
                startActivity(intent);
            }
        });

        // 알림 버튼 클릭시 이동
        Button btn_notice = findViewById(R.id.notice);

        btn_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Notice.class);
                startActivity(intent);
            }
        });
    }
}