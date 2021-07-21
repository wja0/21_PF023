package com.example.probono;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

public class Notice extends AppCompatActivity {

    NoticeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);

        init();

        getData();
    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.noticeRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new NoticeAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        List<Integer> listResId = Arrays.asList(
                R.drawable.ic_baseline_camera_alt_24,
                R.drawable.ic_baseline_mic_24,
                R.drawable.ic_baseline_warning_24
        );

        List<String> listTitle = Arrays.asList(
                "낙상위험",
                "울음소리 감지",
                "질식사 위험 감지"
        );

        List<String> listContent = Arrays.asList(
                "아이가 침대에서 떨어질 뻔했어요!!",
                "아이가 울고있어요!!",
                "아이가 위험해요!!!"
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

