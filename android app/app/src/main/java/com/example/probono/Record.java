package com.example.probono;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Record extends AppCompatActivity {

    RecordAdapter adapter;
    final int DIALOG_TIME = 2;
    int a;
    int num = 1;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

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

        Button b1 = (Button)findViewById(R.id.recordButton1);
        Button b2 = (Button)findViewById(R.id.recordButton2);
        Button b3 = (Button)findViewById(R.id.recordButton3);
        Button b4 = (Button)findViewById(R.id.recordButton4);
        Button b5 = (Button)findViewById(R.id.recordButton5);


        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                a = 1;
                showDialog(DIALOG_TIME); // 날짜 설정 다이얼로그 띄우기
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                a = 2;
                showDialog(DIALOG_TIME);

            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                a = 3;
                showDialog(DIALOG_TIME);
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                a = 4;
                showDialog(DIALOG_TIME);
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                a = 5;
                showDialog(DIALOG_TIME);
            }
        });

    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch (id) {

            case DIALOG_TIME:
                TimePickerDialog tpd =
                        new TimePickerDialog(Record.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view,
                                                          int hourOfDay, int minute) {
//                                        Toast.makeText(getApplicationContext(),
//                                                hourOfDay + "시 " + minute + "분 을 선택했습니다",
//                                                Toast.LENGTH_SHORT).show();

                                        //파이어베이스 저장
                                        String nowtime = hourOfDay + "시 " + minute + "분 ";

                                        SimpleDateFormat format1 = new SimpleDateFormat( "yyyy-MM-dd");
                                        Date time = new Date();
                                        String time1 = format1.format(time);

                                        if (a == 1){
                                            Map<String, Object> taskMap = new HashMap<String, Object>();
                                            taskMap.put("nowtime", nowtime);
                                            taskMap.put("category", "모유");
                                            databaseReference.child(time1).child(String.valueOf(num)).updateChildren(taskMap);
                                            num = num + 1;

                                        }

                                        if (a == 2){
                                            Map<String, Object> taskMap = new HashMap<String, Object>();
                                            taskMap.put("nowtime", nowtime);
                                            taskMap.put("category", "분유");
                                            databaseReference.child(time1).child(String.valueOf(num)).updateChildren(taskMap);
                                            num = num + 1;
                                        }

                                        if (a == 3){
                                            Map<String, Object> taskMap = new HashMap<String, Object>();
                                            taskMap.put("nowtime", nowtime);
                                            taskMap.put("category", "소변");
                                            databaseReference.child(time1).child(String.valueOf(num)).updateChildren(taskMap);
                                            num = num + 1;
                                        }

                                        if (a == 4){
                                            Map<String, Object> taskMap = new HashMap<String, Object>();
                                            taskMap.put("nowtime", nowtime);
                                            taskMap.put("category", "대변");
                                            databaseReference.child(time1).child(String.valueOf(num)).updateChildren(taskMap);
                                            num = num + 1;
                                        }

                                        if (a == 5){
                                            Map<String, Object> taskMap = new HashMap<String, Object>();
                                            taskMap.put("nowtime", nowtime);
                                            taskMap.put("category", "수면");
                                            databaseReference.child(time1).child(String.valueOf(num)).updateChildren(taskMap);
                                            num = num + 1;
                                        }

                                    }
                                }, // 값설정시 호출될 리스너 등록
                                4, 19, false); // 기본값 시분 등록
                // true : 24 시간(0~23) 표시
                // false : 오전/오후 항목이 생김
                return tpd;
        }


        return super.onCreateDialog(id);

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

