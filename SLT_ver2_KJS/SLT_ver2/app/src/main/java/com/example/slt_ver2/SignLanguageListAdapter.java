package com.example.slt_ver2;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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


        //Dialog ini
        vHolder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"Click Delete Button", Toast.LENGTH_SHORT).show();
            }
        });
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.signLanguageName.setText(signLanguageList.get(position).getSignLanguageName());
        holder.iconImage.setImageResource(signLanguageList.get(position).getImageUrl());

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
