package com.example.bluetoothconnect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    RadioButton button_050;
    RadioButton button_075;
    RadioButton button_100;
    RadioButton button_125;
    RadioButton button_150;

    RadioButton total;
    RadioButton individual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        button_050 = (RadioButton)findViewById(R.id.button_050);
        button_075 = (RadioButton)findViewById(R.id.button_075);
        button_100 = (RadioButton)findViewById(R.id.button_100);
        button_125 = (RadioButton)findViewById(R.id.button_125);
        button_150 = (RadioButton)findViewById(R.id.button_150);

        total = (RadioButton)findViewById(R.id.total);
        individual = (RadioButton)findViewById(R.id.individual);
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button_050)
        {
            TranslateActivity.speed = (float) 0.5;
        }
        else if(v.getId()==R.id.button_075)
        {
            TranslateActivity.speed = (float)0.75;
        }
        else if(v.getId()==R.id.button_100)
        {
            TranslateActivity.speed = (float)1.00;
        }
        else if(v.getId()==R.id.button_125)
        {
            TranslateActivity.speed = (float)1.25;
        }
        else if(v.getId()==R.id.button_150)
        {
            TranslateActivity.speed = (float)1.50;
        }
    }
}
