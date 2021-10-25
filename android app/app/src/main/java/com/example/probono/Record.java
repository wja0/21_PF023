package com.example.probono;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Record extends AppCompatActivity {

    private TextView whatday_log;
    boolean sett;
    String temp;
    String amt, bmt;

    RecyclerView rv;
    RecordAdapter adapter;
    ItemTouchHelper helper;

    //아래 세줄 time1에 오늘 날짜 넣기
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    Date time = new Date();
    String time1 = format1.format(time);

//    public String getContents() {
//        return contents;
//    }
//
//    public void setContents(String contents) {
//        this.contents = contents;
//    }

    // fire base
    final int DIALOG_TIME = 2;
    int a;
    int num = 1;
    boolean arr = true;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    // 가져오기
    private RecyclerView recyclerView;
//    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<RecordData> arrayList; // 중간 통신 매개체
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 가져오기
        super.onCreate(savedInstanceState);
        androidx.appcompat.app.ActionBar chg = getSupportActionBar();
        chg.setTitle("기록");
        setContentView(R.layout.record);


//        //RecyclerView의 레이아웃 방식을 지정
//        LinearLayoutManager manager = new LinearLayoutManager(this);
//        manager.setOrientation(LinearLayoutManager.VERTICAL);
//        rv.setLayoutManager(manager);

//        //RecyclerView의 Adapter 세팅
//        adapter = new RecordAdapter();
//        rv.setAdapter(adapter);



        // 파이어베이스에서 값 받아올때 필요한 변수여서 위로 올림
        Intent receive_intent = getIntent();
        temp = receive_intent.getStringExtra("cal");


        recyclerView = findViewById(R.id.recordRecyclerView); // item 여러개 출력하는 layout


        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // user 객체를 담을 어레이 리스트 (어댑터 쪽으로 전송)

        database = FirebaseDatabase.getInstance(); // 파이어베이스 DB 연동


        //ItemTouchHelper 생성
        helper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
        //RecyclerView에 ItemTouchHelper 붙이기
        rv = findViewById(R.id.recordRecyclerView);
        helper.attachToRecyclerView(rv);

// DATE 가져오기
        if (temp == null) {
            databaseReference = database.getReference("record").child(time1);
            // DB 테이블 연동 맨 위 값
        } else {
            databaseReference = database.getReference("record").child(String.valueOf(temp));
            time1 = temp;
        }
        Log.d("날짜", String.valueOf(temp));

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                // 파이어베이스 DB의 데이터를 받아오는 곳
                arrayList.clear(); // 기존 배열리스트가 존재하지 않게 초기화 add 전에
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터 List를 추출해냄
                    RecordData record = snapshot.getValue(RecordData.class); //만들어뒀던 user 객체에 데이터를 담는다.
                    arrayList.add(record); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                    Log.d("파베 데이터 값", String.valueOf(arrayList));
                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                // DB를 가져오던 중 에러 발생 시
                Log.e("Firebase", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        adapter = new RecordAdapter(arrayList, this); // customadapter와 연동
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

        // 보내기

//        init();
//        getData();

        // 지난 달력 확인
        ImageView imageCalendar = (ImageView) findViewById(R.id.imageCalendar);

        imageCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoryCalendar.class);
                startActivity(intent);
                finish();
            }
        });

        Button b1 = (Button) findViewById(R.id.recordButton1);
        Button b2 = (Button) findViewById(R.id.recordButton2);
        Button b3 = (Button) findViewById(R.id.recordButton3);
        Button b4 = (Button) findViewById(R.id.recordButton4);
        Button b5 = (Button) findViewById(R.id.recordButton5);


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

        whatday_log = (TextView) findViewById(R.id.textCalendar);
        //아래 , historycalendar에서 클릭한 날짜 값 가져오고 표시하기
        if (temp != null) {
            //temp = temp.substring(12, 21);
            whatday_log.setText(temp);
            getIntent().removeExtra("cal");

        }

        //아래 두 줄 달력 옆 날짜 오늘으로 설정
        if (temp == null) {
            whatday_log.setText(time1);
        }


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
                                        String nowtime = "";
                                        // 포맷 추가
                                        if (hourOfDay / 10 == 0) {
                                            nowtime += "0" + hourOfDay + "시 ";
                                        } else {
                                            nowtime += hourOfDay + "시 ";
                                        }
                                        if (minute / 10 == 0) {
                                            nowtime += "0" + minute + "분 ";
                                        } else {
                                            nowtime += minute + "분 ";
                                        }


                                        mAuth = FirebaseAuth.getInstance(); // 유저 계정 정보 가져오기
                                        mDatabase = FirebaseDatabase.getInstance().getReference(); // 파이어베이스 realtime database 에서 정보 가져오기

                                        databaseReference = database.getReference(time1);
                                        String finalNowtime = nowtime;
                                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                                for (int i = 0; i < 1000; i++) {
                                                    if (!snapshot.child("record").child(time1).child(String.valueOf(i)).exists() && a == 1) { //오늘날짜 생기고부터

                                                        Map<String, Object> taskMap = new HashMap<String, Object>();
                                                        taskMap.put("nowtime", finalNowtime);
                                                        taskMap.put("category", "모유");
                                                        database.getReference("record").child(time1).child(String.valueOf(i)).updateChildren(taskMap);
                                                        break;
                                                    }

                                                    if (!snapshot.child("record").child(time1).child(String.valueOf(i)).exists() && a == 2) { //오늘날짜 생기고부터

                                                        Map<String, Object> taskMap = new HashMap<String, Object>();
                                                        taskMap.put("nowtime", finalNowtime);
                                                        taskMap.put("category", "분유");
                                                        database.getReference("record").child(time1).child(String.valueOf(i)).updateChildren(taskMap);
                                                        break;
                                                    }

                                                    if (!snapshot.child("record").child(time1).child(String.valueOf(i)).exists() && a == 3) { //오늘날짜 생기고부터

                                                        Map<String, Object> taskMap = new HashMap<String, Object>();
                                                        taskMap.put("nowtime", finalNowtime);
                                                        taskMap.put("category", "소변");
                                                        database.getReference("record").child(time1).child(String.valueOf(i)).updateChildren(taskMap);
                                                        break;
                                                    }

                                                    if (!snapshot.child("record").child(time1).child(String.valueOf(i)).exists() && a == 4) { //오늘날짜 생기고부터

                                                        Map<String, Object> taskMap = new HashMap<String, Object>();
                                                        taskMap.put("nowtime", finalNowtime);
                                                        taskMap.put("category", "대변");
                                                        database.getReference("record").child(time1).child(String.valueOf(i)).updateChildren(taskMap);
                                                        break;
                                                    }

                                                    if (!snapshot.child("record").child(time1).child(String.valueOf(i)).exists() && a == 5) { //오늘날짜 생기고부터

                                                        Map<String, Object> taskMap = new HashMap<String, Object>();
                                                        taskMap.put("nowtime", finalNowtime);
                                                        taskMap.put("category", "수면");
                                                        database.getReference("record").child(time1).child(String.valueOf(i)).updateChildren(taskMap);
                                                        break;
                                                    }


                                                }
                                            }


                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }

                                        });


                                    }
                                }, // 값설정시 호출될 리스너 등록
                                12, 00, false); // 기본값 시분 등록
                // true : 24 시간(0~23) 표시
                // false : 오전/오후 항목이 생김
                return tpd;
        }


        return super.onCreateDialog(id);

    }

//    private void init() {
//        RecyclerView recyclerView = findViewById(R.id.recordRecyclerView);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//        adapter = new RecordAdapter();
//        recyclerView.setAdapter(adapter);
//    }

//    private void getData() {
//        List<Integer> listResId = Arrays.asList(
//                R.drawable.ic_baseline_camera_alt_24,
//                R.drawable.ic_baseline_mic_24,
//                R.drawable.ic_baseline_warning_24
//        );
//
//        List<String> listTitle = Arrays.asList(
//                "모유",
//                "분유",
//                "소변"
//        );
//
//        List<String> listContent = Arrays.asList(
//                "1",
//                "2",
//                "3"
//        );
//
//        // *** 이 부분은 if문으로 조건 맞춰서 수정 필요 ***
//        for (int i = 0; i < listTitle.size(); i++) {
//            // 각 List의 값들을 data 객체에 set 해줍니다.
//            Data data = new Data();
//            data.setTitle(listTitle.get(i));
//            data.setContent(listContent.get(i));
//            data.setResId(listResId.get(i));
//
//            // 각 값이 들어간 data를 adapter에 추가합니다.
//            adapter.addItem(data);
//        }
//
//        // adapter의 값이 변경되었다는 것을 알려줍니다.
//        adapter.notifyDataSetChanged();
//    }

}

