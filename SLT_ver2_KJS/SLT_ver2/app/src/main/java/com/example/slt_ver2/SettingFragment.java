package com.example.slt_ver2;

import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import static com.example.slt_ver2.MainActivity.speed;
import static com.example.slt_ver2.utils.Constants.LEFT;
import static com.example.slt_ver2.utils.Constants.RIGHT;
import static com.example.slt_ver2.utils.Constants.STATE_CONNECTING;
import static com.example.slt_ver2.utils.Constants.STATE_DISCONNECTED;


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

        Button button_right;
        Button button_left;

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

            button_right = (Button)v.findViewById(R.id.RightConnectButton);
            button_left = (Button)v.findViewById(R.id.LeftConnectButton);

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

            button_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MainActivity.bs.isConnected_right()) {
                        MainActivity.bs.disconnect(RIGHT);

                        Message m = new Message();
                        m.what = RIGHT;
                        m.arg1 = STATE_DISCONNECTED;
                        BluetoothDialog.handler.sendMessage(m);
                    } else {
                        //블루투스 끊어진 상태
                        Message m = new Message();
                        m.what = RIGHT;
                        m.arg1 = STATE_CONNECTING;
                        BluetoothDialog.handler.sendMessage(m);
                        BluetoothDialog.startConnect_right();
                    }

                }
            });

            button_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( MainActivity.bs.isConnected_left()) {
                        MainActivity.bs.disconnect(LEFT);

                        Message m = new Message();
                        m.what = LEFT;
                        m.arg1 = STATE_DISCONNECTED;
                        BluetoothDialog.handler.sendMessage(m);
                    } else {
                        //블루투스 끊어진 상태
                        Message m = new Message();
                        m.what = LEFT;
                        m.arg1 = STATE_CONNECTING;
                        BluetoothDialog.handler.sendMessage(m);
                        BluetoothDialog.startConnect_left();
                    }
                }
            });

            return v;
        }

        @Override
        public void onClick(View v) {

        }

}
