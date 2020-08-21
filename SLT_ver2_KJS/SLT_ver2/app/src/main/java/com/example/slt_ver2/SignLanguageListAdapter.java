package com.example.slt_ver2;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SignLanguageListAdapter extends RecyclerView.Adapter<SignLanguageListAdapter.MyViewHolder> {

    Context mContext;
    List<SignLanguageList> signLanguageList;

    public SignLanguageListAdapter(Context mContext, List<SignLanguageList> signLanguageList) {
        this.mContext = mContext;
        this.signLanguageList = signLanguageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false);

        final MyViewHolder vHolder = new MyViewHolder(view);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.signLanguageName.setText(signLanguageList.get(position).getSignLanguageName());
        holder.iconImage.setImageResource(signLanguageList.get(position).getImageUrl());

        //Dialog ini
        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"Click Delete Button" + position, Toast.LENGTH_SHORT).show();
                int pos = position;
                if(pos != RecyclerView.NO_POSITION) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("삭제 확인");
                    builder.setMessage("삭제하시겠습니까?");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //예 눌렀을때의 이벤트 처리
                            new Thread() {
                                public void run() {
                                    TranslationFragment.out.println("delete " + signLanguageList.get(position).getSignLanguageName());
                                    Log.d("====del ", signLanguageList.get(position).getSignLanguageName());
                                    signLanguageList.remove(position);
                                    TranslationFragment.check_finished = false;
                                }
                            }.start();
                            notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //아니오 눌렀을때의 이벤트 처리
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() { return signLanguageList.size(); }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageButton delete_btn;
        ImageView iconImage;
        TextView signLanguageName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            delete_btn = itemView.findViewById(R.id.delete_button);
            iconImage = itemView.findViewById(R.id.listitem_image);
            signLanguageName = itemView.findViewById(R.id.listitem_text);
        }
    }

 }
