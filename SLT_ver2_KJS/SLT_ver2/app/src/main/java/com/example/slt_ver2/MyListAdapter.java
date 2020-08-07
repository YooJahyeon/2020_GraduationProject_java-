package com.example.slt_ver2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

// 보충 필요
public class MyListAdapter extends BaseAdapter{
    Context context;
    ArrayList<list_item> list_itemArrayList;

    public MyListAdapter(Context context, ArrayList<list_item> list_itemArrayList) {
        this.context = context;
        this.list_itemArrayList = list_itemArrayList;
    }

   @Override
    public int getCount() {
        if (this.list_itemArrayList.size()<=0)
        {
            return 5;
        }
        else
            return this.list_itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        position %= 5;
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 나중에
        return null;
    }

}
