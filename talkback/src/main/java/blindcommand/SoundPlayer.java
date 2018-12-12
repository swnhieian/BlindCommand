package blindcommand;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.google.android.accessibility.talkback.R;

import java.util.Locale;

public class SoundPlayer {
    private static SoundPool soundPool;
    private static Context context;
    private static TextToSpeech tts;
    public static void setContext(Context context) {
        SoundPlayer.context = context;
        soundPool = new SoundPool.Builder().build();
        //soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundPool.load(context, R.raw.click, 1);
        soundPool.load(context, R.raw.delete, 2);
        soundPool.load(context, R.raw.ding, 1);

        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int result;
                    if (Utility.getLanguage().equals("CN")) {
                        result = tts.setLanguage(Locale.CHINA);
                    } else {
                        result = tts.setLanguage(Locale.US);
                    }
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                        && result != TextToSpeech.LANG_AVAILABLE) {
                        Toast.makeText(SoundPlayer.context, "TTS暂不支持", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        tts.setPitch(0.5f);
        tts.setSpeechRate(1.5f);


    }
    public static void click() {
        soundPool.play(1,1,1,0,0,1.5f);
    }
    public static void delete() {
        soundPool.play(2,1,1,1,0,1.5f);
    }
    public static void ding() {
        soundPool.play(3,1,1,1,0,1.5f);
    }
    public static void tts(String text) {
        System.out.println("tts: " + text +"   " + System.currentTimeMillis());
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
//    public static void tts(Operations oper) {
//
//        switch (oper) {
//            case CLEAER_ALL:
//
//        }
//    }
//    enum Operations {
//        CLEAER_ALL
//    }
//    public static
}
