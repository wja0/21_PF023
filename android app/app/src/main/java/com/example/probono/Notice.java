package com.example.probono;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Notice extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<NoticeData> arrayList; // 중간 통신 매개체
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);

        recyclerView = findViewById(R.id.noticeRecyclerView); // item 여러개 출력하는 layout
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // user 객체를 담을 어레이 리스트 (어댑터 쪽으로 전송)

        database = FirebaseDatabase.getInstance(); // 파이어베이스 DB 연동

        databaseReference = database.getReference("alarm"); // DB 테이블 연동 맨 위 값
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                // 파이어베이스 DB의 데이터를 받아오는 곳
                arrayList.clear(); // 기존 배열리스트가 존재하지 않게 초기화 add 전에
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터 List를 추출해냄
                    NoticeData Notice = snapshot.getValue(NoticeData.class); //만들어뒀던 user 객체에 데이터를 담는다.
                    arrayList.add(Notice); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                    Log.d("파베 데이터 값", String.valueOf(Notice));
                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                // DB를 가져오던 중 에러 발생 시
                Log.e("Notice", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        adapter = new NoticeAdapter(arrayList, this); // NoticeAdapter와 연동
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

    }
}