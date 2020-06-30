package com.example.slt_ver2;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Toast;
import static com.example.slt_ver2.TranslationFragment.speed;

import java.util.ArrayList;
import java.util.List;


public class SettingFragment extends Fragment implements View.OnClickListener {

    RadioButton button_050;
    RadioButton button_075;
    RadioButton button_100;
    RadioButton button_125;
    RadioButton button_150;

    SeekBar seekbar_volume;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AudioManager audioManager = (AudioManager)this.getActivity().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int nCurrentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        seekbar_volume.setMax(nMax);
        seekbar_volume.setProgress(nCurrentVolume);

        seekbar_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Toast.makeText(this.getContext(), "This is SearchFragment", Toast.LENGTH_SHORT).show();
        View v = inflater.inflate(R.layout.fragment_setting, container, false);

        button_050 = (RadioButton)v.findViewById(R.id.Button_050);
        button_075 = (RadioButton)v.findViewById(R.id.Button_075);
        button_100 = (RadioButton)v.findViewById(R.id.Button_100);
        button_125 = (RadioButton)v.findViewById(R.id.Button_125);
        button_150 = (RadioButton)v.findViewById(R.id.Button_150);

        seekbar_volume = (SeekBar)v.findViewById(R.id.seekbar_volume);

        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.Button_050) {
            speed = (float) 0.5;
        } else if (v.getId() == R.id.Button_075) {
            speed = (float) 0.75;
        } else if (v.getId() == R.id.Button_100) {
            speed = (float) 1.00;
        } else if (v.getId() == R.id.Button_125) {
            speed = (float) 1.25;
        } else if (v.getId() == R.id.Button_150) {
            speed = (float) 1.50;
        }
    }
}
