package com.example.probono;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.List;

public class Record extends AppCompatActivity {

    RecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);

        init();

        getData();

        // 지난 달력 확인
        ImageView imageCalendar = (ImageView) findViewById(R.id.imageCalendar);

        imageCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),HistoryCalendar.class);
                startActivity(intent);
            }
        });

    }


    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recordRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecordAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        List<Integer> listResId = Arrays.asList(
                R.drawable.ic_baseline_camera_alt_24,
                R.drawable.ic_baseline_mic_24,
                R.drawable.ic_baseline_warning_24
        );

        List<String> listTitle = Arrays.asList(
                "모유",
                "분유",
                "소변"
        );

        List<String> listContent = Arrays.asList(
                "1",
                "2",
                "3"
        );

        // *** 이 부분은 if문으로 조건 맞춰서 수정 필요 ***
        for (int i = 0; i < listTitle.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            Data data = new Data();
            data.setTitle(listTitle.get(i));
            data.setContent(listContent.get(i));
            data.setResId(listResId.get(i));

            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }
}

