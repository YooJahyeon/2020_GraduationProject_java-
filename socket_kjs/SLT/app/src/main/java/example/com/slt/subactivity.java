package example.com.slt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class subactivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private static TextToSpeech tts;
    private static String recvMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subactivity);

        tts = new TextToSpeech(this, this); //첫번째는 Context 두번째는 리스너
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int language = tts.setLanguage(Locale.KOREAN);

            if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "지원하지 않는 언어입니다.", Toast.LENGTH_SHORT).show();
            } else {
                speakOutNow();
            }
        } else {
            Toast.makeText(this, "TTS 실패!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    static void speakOutNow() {
        String text = (String)recvMessage;
        //tts.setPitch((float) 0.1); //음량
        //tts.setSpeechRate((float) 0.7); //재생속도
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}
