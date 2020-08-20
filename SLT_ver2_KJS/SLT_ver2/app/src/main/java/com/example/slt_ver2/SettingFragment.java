package com.example.slt_ver2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class SettingFragment extends Fragment implements View.OnClickListener {

    RadioButton button_010;
    RadioButton button_050;
    RadioButton button_100;
    RadioButton button_150;
    RadioButton button_200;

    ImageButton add_button;
    ImageButton sub_button;

    TextView text_font_size;
    static int font_size = 15;
    static float speed = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println(text_font_size);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Toast.makeText(this.getContext(), "This is SearchFragment", Toast.LENGTH_SHORT).show();
        View v = inflater.inflate(R.layout.fragment_setting, container, false);

        button_010 = (RadioButton)v.findViewById(R.id.Button_010);
        button_050 = (RadioButton)v.findViewById(R.id.Button_050);
        button_100 = (RadioButton)v.findViewById(R.id.Button_100);
        button_150 = (RadioButton)v.findViewById(R.id.Button_150);
        button_200 = (RadioButton)v.findViewById(R.id.Button_200);

        button_010.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed = (float) 0.1;
                System.out.println(speed);
            }
        });
        button_050.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed = (float) 0.5;
                System.out.println(speed);
            }
        });
        button_100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed = (float) 1;
                System.out.println(speed);
            }
        });
        button_150.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed = (float) 1.5;
                System.out.println(speed);
            }
        });
        button_200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed = (float) 2;
                System.out.println(speed);
            }
        });

        text_font_size = (TextView)v.findViewById(R.id.text_font_size);
        text_font_size.setText("15");

        System.out.println(String.valueOf(text_font_size));
        add_button = (ImageButton)v.findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                font_size = font_size + 1;
                text_font_size.setText(String.valueOf(font_size));
                System.out.println(font_size);
                TranslationFragment.adapter.notifyDataSetChanged();
            }
        });
        sub_button = (ImageButton)v.findViewById(R.id.sub_button);
        sub_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                font_size = font_size - 1;
                text_font_size.setText(String.valueOf(font_size));
                System.out.println(font_size);
                TranslationFragment.adapter.notifyDataSetChanged();
            }
        });

        return v;
    }

    @Override
    public void onClick(View v) {

    }
}