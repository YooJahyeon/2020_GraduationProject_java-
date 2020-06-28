package com.example.slt_ver3_multiple;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


public class ListFragment extends Fragment  {
    ListView listView;
    ArrayList<list_item> arrayList;
    MyListAdapter adapter;
    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        listView = (ListView)view.findViewById(R.id.listview);

        mContext = getContext();

        arrayList = new ArrayList<>();

        adapter = new MyListAdapter(mContext, arrayList);

        listView.setAdapter(adapter);




        return view;

    }

}
