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

//import com.bumptech.glide.Glide;

import java.util.ArrayList;

// 데이터 가져와서 파베에 뿌리기
public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {

    private ArrayList<NoticeData> arrayList; // 객체 클래스에 추가
    private Context context; // 어댑터에서 context를 가져올때

    public NoticeAdapter(ArrayList<NoticeData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item, parent, false); // 레이아웃 연결
        NoticeViewHolder holder = new NoticeViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull NoticeAdapter.NoticeViewHolder holder, int position) {
        // 각 아이템에 대한 매칭
        // firebase 데이터를 가져오면 user 객체가 있는 arraylist에 담아서 어댑터에 전송
//        Glide.with(holder.itemView)
//                .load(arrayList.get(position).getProfile())
//                .into(holder.iv_profile);
        //holder.tv_pw.setText(String.valueOf(arrayList.get(position).getPw())); // 타입 에러 방지
        holder.title.setText(arrayList.get(position).getTitle());
        holder.comment.setText(arrayList.get(position).getComment());
        holder.time.setText(arrayList.get(position).getTime());

        // 질식사 위험 감지 : R.drawable.ic_baseline_camera_alt_24
        // "아기가 잘 자고 있나요?" "아기가 위험해요!!
        // 낙상 위험 감지 : R.drawable.ic_baseline_warning_24
        // "아기가 잘 누워 있나요?" "아기가 침대에서 떨어질 것 같아요!!!"
        // 울음소리 감지 : R.drawable.ic_baseline_mic_24
        // "아기가 배고파해요!!" "아이가 졸려요!!"

        if (arrayList.get(position).getTitle().equals("질식사 위험 감지")) {
            Log.d("파베 데이터 확인", arrayList.get(position).getTitle());
            holder.image.setImageResource(R.drawable.ic_baseline_camera_alt_24);
        }
        else if (arrayList.get(position).getTitle().equals("낙상 위험 감지")) {
            Log.d("파베 데이터 확인", arrayList.get(position).getTitle());
            holder.image.setImageResource(R.drawable.ic_baseline_warning_24);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
        // (arrayList != null ? arrayList.size() : Log.i("ArrayList", "no"));
    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView comment;
        TextView time;

        public NoticeViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.image);
            this.title = itemView.findViewById(R.id.title);
            this.comment = itemView.findViewById(R.id.comment);
            this.time = itemView.findViewById(R.id.time);
        }
    }
}
