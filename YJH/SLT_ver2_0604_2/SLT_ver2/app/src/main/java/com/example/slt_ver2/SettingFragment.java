package com.example.slt_ver2;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class SettingFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Toast.makeText(this.getContext(), "This is SearchFragment", Toast.LENGTH_SHORT).show();
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        RadioGroup radioGroup = (RadioGroup)v.findViewById(R.id.speedGroup);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.Button_050) { TranslationFragment.ttsSpeed = 0.5; }
                else if(checkedId == R.id.Button_075) {TranslationFragment.ttsSpeed = 0.75;}
                else if(checkedId == R.id.Button_100) {TranslationFragment.ttsSpeed = 1.0;}
                else if(checkedId == R.id.Button_125) {TranslationFragment.ttsSpeed = 1.25;}
                else if(checkedId == R.id.Button_150) {TranslationFragment.ttsSpeed = 1.5;}

            }
        });

        return v;
    }


}
