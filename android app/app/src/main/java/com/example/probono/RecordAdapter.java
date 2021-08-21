package com.example.probono;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

// 데이터 가져와서 파베에 뿌리기
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private ArrayList<RecordData> arrayList; // 객체 클래스에 추가
    private Context context; // 어댑터에서 context를 가져올때

    public RecordAdapter(ArrayList<RecordData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, parent, false); // 레이아웃 연결
        RecordViewHolder holder = new RecordViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull RecordAdapter.RecordViewHolder holder, int position) {
        // 각 아이템에 대한 매칭
//        Glide.with(holder.itemView)
//                .load(arrayList.get(position).getProfile())
//                .into(holder.iv_profile);
        // firebase 데이터를 가져오면 user 객체가 있는 arraylist에 담아서 어댑터에 전송
        // 여기서 그걸 받아서 gilde로 load
        // server에서 이미지 가져와서 출력
        holder.nowtime.setText(arrayList.get(position).getNowtime());
        holder.category.setText(arrayList.get(position).getCategory()); // 타입 에러 방지
       // holder.contents.setText(arrayList.get(position).getContents());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
        // (arrayList != null ? arrayList.size() : Log.i("ArrayList", "no"));
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView nowtime;
        TextView category;
        //TextView contents;

        public RecordViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);
            this.nowtime = itemView.findViewById(R.id.nowtime);
            this.category = itemView.findViewById(R.id.category);
         //   this.contents = itemView.findViewById(R.id.contents);
        }
    }
}
