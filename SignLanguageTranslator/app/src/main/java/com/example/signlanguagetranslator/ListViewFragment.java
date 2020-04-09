package com.example.signlanguagetranslator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class ListViewFragment extends Fragment {

private ListView listview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        listview = (ListView)view.findViewById(R.id.listview1);
        List<String> list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, list);

        listview.setAdapter(adapter);

        list.add("ㄱ");
        list.add("ㄴ");
        list.add("ㄷ");
        list.add("ㄹ");
        list.add("ㅁ");
        list.add("ㅂ");
        return view;
    }


}
