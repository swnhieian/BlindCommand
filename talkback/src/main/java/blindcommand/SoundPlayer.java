package blindcommand;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.widget.Toast;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;
import com.google.android.accessibility.talkback.R;
import com.google.android.accessibility.talkback.TalkBackService;

import java.util.HashMap;
import java.util.Locale;

public class SoundPlayer {
    private static SoundPool soundPool;
    private static Context context;
//    private static TextToSpeech tts;
    static HashMap<String, int[]> voice = new HashMap<>();
    public static void setContext(Context context) {
        SoundPlayer.context = context;
        soundPool = new SoundPool.Builder().build();
        //soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundPool.load(context, R.raw.click, 1);
        soundPool.load(context, R.raw.delete, 2);
        soundPool.load(context, R.raw.ding, 1);
        soundPool.load(context, R.raw.start, 1);
        soundPool.load(context, R.raw.end, 1);
        soundPool.load(context, R.raw.success, 1);
        initTTS();

//        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int i) {
//                if (i == TextToSpeech.SUCCESS) {
//                    int result;
//                    if (Utility.getLanguage().equals("CN")) {
//                        result = tts.setLanguage(Locale.CHINA);
//                    } else {
//                        result = tts.setLanguage(Locale.US);
//                    }
//                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
//                            && result != TextToSpeech.LANG_AVAILABLE) {
//                        Toast.makeText(SoundPlayer.context, "TTS暂不支持", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
//        tts.setPitch(0.5f);
//        tts.setSpeechRate(2.0f);
        initVoice();
    }
    public static void interrupt() {
        if (TalkBackService.isServiceActive()) {
            // Stop the TTS engine when any key (except for volume up/down key) is pressed on physical
            // keyboard.

            ((TalkBackService)context).interruptAllFeedback(false /* stopTtsSpeechCompletely */);

        }
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
    public static void start() { soundPool.play(4,1,1,1,0,1.5f); }
    public static void end() {soundPool.play(5,1,1,1,0,1.5f); }
    public static void success() {soundPool.play(6,1,1,10,0,1.5f); }

    public static void tts(String text) {
//        interrupt();
//        System.out.println("tts: " + text +"   " + System.currentTimeMillis());
//        String[] splitSpeech = text.split("\\.");
//        for (int i = 0; i < splitSpeech.length; i++) {
//            if (i == 0) { // Use for the first splited text to flush on audio stream
//                tts.speak(splitSpeech[i].toString().trim(),TextToSpeech.QUEUE_FLUSH, null);
//            } else { // add the new test on previous then play the TTS
//                tts.speak(splitSpeech[i].toString().trim(), TextToSpeech.QUEUE_ADD, null);
//            }
//            tts.playSilence(250, TextToSpeech.QUEUE_ADD, null);
//        }
        xftts(text);
    }
    public static void readKey(int speed, char key) {
//        interrupt();
//        MediaPlayer current = MediaPlayer.create(context, voice.get("ios11_"+speed)[Character.toLowerCase(key)- 'a']);
//        current.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                mediaPlayer.reset();
//                mediaPlayer.release();
//            }
//        });
//        current.start();
        xftts(String.valueOf(key));
    }

    public static void initVoice() {
        voice.put("ios11_50", new int[26]);
        voice.put("ios11_60", new int[26]);
        voice.put("ios11_70", new int[26]);
        voice.put("ios11_80", new int[26]);
        voice.put("ios11_90", new int[26]);
        voice.put("ios11_100", new int[26]);
        voice.put("ios11da", new int[1]);
        voice.put("delete", new int[1]);
        voice.put("blank", new int[1]);
        voice.put("delete_word", new int[1]);
        voice.put("fuzzyInput", new int[3]);
        voice.put("google", new int[26]);
        voice.get("ios11_50")[0] = R.raw.ios11_50_a;
        voice.get("ios11_50")[1] = R.raw.ios11_50_b;
        voice.get("ios11_50")[2] = R.raw.ios11_50_c;
        voice.get("ios11_50")[3] = R.raw.ios11_50_d;
        voice.get("ios11_50")[4] = R.raw.ios11_50_e;
        voice.get("ios11_50")[5] = R.raw.ios11_50_f;
        voice.get("ios11_50")[6] = R.raw.ios11_50_g;
        voice.get("ios11_50")[7] = R.raw.ios11_50_h;
        voice.get("ios11_50")[8] = R.raw.ios11_50_i;
        voice.get("ios11_50")[9] = R.raw.ios11_50_j;
        voice.get("ios11_50")[10] = R.raw.ios11_50_k;
        voice.get("ios11_50")[11] = R.raw.ios11_50_l;
        voice.get("ios11_50")[12] = R.raw.ios11_50_m;
        voice.get("ios11_50")[13] = R.raw.ios11_50_n;
        voice.get("ios11_50")[14] = R.raw.ios11_50_o;
        voice.get("ios11_50")[15] = R.raw.ios11_50_p;
        voice.get("ios11_50")[16] = R.raw.ios11_50_q;
        voice.get("ios11_50")[17] = R.raw.ios11_50_r;
        voice.get("ios11_50")[18] = R.raw.ios11_50_s;
        voice.get("ios11_50")[19] = R.raw.ios11_50_t;
        voice.get("ios11_50")[20] = R.raw.ios11_50_u;
        voice.get("ios11_50")[21] = R.raw.ios11_50_v;
        voice.get("ios11_50")[22] = R.raw.ios11_50_w;
        voice.get("ios11_50")[23] = R.raw.ios11_50_x;
        voice.get("ios11_50")[24] = R.raw.ios11_50_y;
        voice.get("ios11_50")[25] = R.raw.ios11_50_z;

        voice.get("ios11_60")[0] = R.raw.ios11_60_a;
        voice.get("ios11_60")[1] = R.raw.ios11_60_b;
        voice.get("ios11_60")[2] = R.raw.ios11_60_c;
        voice.get("ios11_60")[3] = R.raw.ios11_60_d;
        voice.get("ios11_60")[4] = R.raw.ios11_60_e;
        voice.get("ios11_60")[5] = R.raw.ios11_60_f;
        voice.get("ios11_60")[6] = R.raw.ios11_60_g;
        voice.get("ios11_60")[7] = R.raw.ios11_60_h;
        voice.get("ios11_60")[8] = R.raw.ios11_60_i;
        voice.get("ios11_60")[9] = R.raw.ios11_60_j;
        voice.get("ios11_60")[10] = R.raw.ios11_60_k;
        voice.get("ios11_60")[11] = R.raw.ios11_60_l;
        voice.get("ios11_60")[12] = R.raw.ios11_60_m;
        voice.get("ios11_60")[13] = R.raw.ios11_60_n;
        voice.get("ios11_60")[14] = R.raw.ios11_60_o;
        voice.get("ios11_60")[15] = R.raw.ios11_60_p;
        voice.get("ios11_60")[16] = R.raw.ios11_60_q;
        voice.get("ios11_60")[17] = R.raw.ios11_60_r;
        voice.get("ios11_60")[18] = R.raw.ios11_60_s;
        voice.get("ios11_60")[19] = R.raw.ios11_60_t;
        voice.get("ios11_60")[20] = R.raw.ios11_60_u;
        voice.get("ios11_60")[21] = R.raw.ios11_60_v;
        voice.get("ios11_60")[22] = R.raw.ios11_60_w;
        voice.get("ios11_60")[23] = R.raw.ios11_60_x;
        voice.get("ios11_60")[24] = R.raw.ios11_60_y;
        voice.get("ios11_60")[25] = R.raw.ios11_60_z;

        voice.get("ios11_70")[0] = R.raw.ios11_70_a;
        voice.get("ios11_70")[1] = R.raw.ios11_70_b;
        voice.get("ios11_70")[2] = R.raw.ios11_70_c;
        voice.get("ios11_70")[3] = R.raw.ios11_70_d;
        voice.get("ios11_70")[4] = R.raw.ios11_70_e;
        voice.get("ios11_70")[5] = R.raw.ios11_70_f;
        voice.get("ios11_70")[6] = R.raw.ios11_70_g;
        voice.get("ios11_70")[7] = R.raw.ios11_70_h;
        voice.get("ios11_70")[8] = R.raw.ios11_70_i;
        voice.get("ios11_70")[9] = R.raw.ios11_70_j;
        voice.get("ios11_70")[10] = R.raw.ios11_70_k;
        voice.get("ios11_70")[11] = R.raw.ios11_70_l;
        voice.get("ios11_70")[12] = R.raw.ios11_70_m;
        voice.get("ios11_70")[13] = R.raw.ios11_70_n;
        voice.get("ios11_70")[14] = R.raw.ios11_70_o;
        voice.get("ios11_70")[15] = R.raw.ios11_70_p;
        voice.get("ios11_70")[16] = R.raw.ios11_70_q;
        voice.get("ios11_70")[17] = R.raw.ios11_70_r;
        voice.get("ios11_70")[18] = R.raw.ios11_70_s;
        voice.get("ios11_70")[19] = R.raw.ios11_70_t;
        voice.get("ios11_70")[20] = R.raw.ios11_70_u;
        voice.get("ios11_70")[21] = R.raw.ios11_70_v;
        voice.get("ios11_70")[22] = R.raw.ios11_70_w;
        voice.get("ios11_70")[23] = R.raw.ios11_70_x;
        voice.get("ios11_70")[24] = R.raw.ios11_70_y;
        voice.get("ios11_70")[25] = R.raw.ios11_70_z;

        voice.get("ios11_80")[0] = R.raw.ios11_80_a;
        voice.get("ios11_80")[1] = R.raw.ios11_80_b;
        voice.get("ios11_80")[2] = R.raw.ios11_80_c;
        voice.get("ios11_80")[3] = R.raw.ios11_80_d;
        voice.get("ios11_80")[4] = R.raw.ios11_80_e;
        voice.get("ios11_80")[5] = R.raw.ios11_80_f;
        voice.get("ios11_80")[6] = R.raw.ios11_80_g;
        voice.get("ios11_80")[7] = R.raw.ios11_80_h;
        voice.get("ios11_80")[8] = R.raw.ios11_80_i;
        voice.get("ios11_80")[9] = R.raw.ios11_80_j;
        voice.get("ios11_80")[10] = R.raw.ios11_80_k;
        voice.get("ios11_80")[11] = R.raw.ios11_80_l;
        voice.get("ios11_80")[12] = R.raw.ios11_80_m;
        voice.get("ios11_80")[13] = R.raw.ios11_80_n;
        voice.get("ios11_80")[14] = R.raw.ios11_80_o;
        voice.get("ios11_80")[15] = R.raw.ios11_80_p;
        voice.get("ios11_80")[16] = R.raw.ios11_80_q;
        voice.get("ios11_80")[17] = R.raw.ios11_80_r;
        voice.get("ios11_80")[18] = R.raw.ios11_80_s;
        voice.get("ios11_80")[19] = R.raw.ios11_80_t;
        voice.get("ios11_80")[20] = R.raw.ios11_80_u;
        voice.get("ios11_80")[21] = R.raw.ios11_80_v;
        voice.get("ios11_80")[22] = R.raw.ios11_80_w;
        voice.get("ios11_80")[23] = R.raw.ios11_80_x;
        voice.get("ios11_80")[24] = R.raw.ios11_80_y;
        voice.get("ios11_80")[25] = R.raw.ios11_80_z;

        voice.get("ios11_90")[0] = R.raw.ios11_90_a;
        voice.get("ios11_90")[1] = R.raw.ios11_90_b;
        voice.get("ios11_90")[2] = R.raw.ios11_90_c;
        voice.get("ios11_90")[3] = R.raw.ios11_90_d;
        voice.get("ios11_90")[4] = R.raw.ios11_90_e;
        voice.get("ios11_90")[5] = R.raw.ios11_90_f;
        voice.get("ios11_90")[6] = R.raw.ios11_90_g;
        voice.get("ios11_90")[7] = R.raw.ios11_90_h;
        voice.get("ios11_90")[8] = R.raw.ios11_90_i;
        voice.get("ios11_90")[9] = R.raw.ios11_90_j;
        voice.get("ios11_90")[10] = R.raw.ios11_90_k;
        voice.get("ios11_90")[11] = R.raw.ios11_90_l;
        voice.get("ios11_90")[12] = R.raw.ios11_90_m;
        voice.get("ios11_90")[13] = R.raw.ios11_90_n;
        voice.get("ios11_90")[14] = R.raw.ios11_90_o;
        voice.get("ios11_90")[15] = R.raw.ios11_90_p;
        voice.get("ios11_90")[16] = R.raw.ios11_90_q;
        voice.get("ios11_90")[17] = R.raw.ios11_90_r;
        voice.get("ios11_90")[18] = R.raw.ios11_90_s;
        voice.get("ios11_90")[19] = R.raw.ios11_90_t;
        voice.get("ios11_90")[20] = R.raw.ios11_90_u;
        voice.get("ios11_90")[21] = R.raw.ios11_90_v;
        voice.get("ios11_90")[22] = R.raw.ios11_90_w;
        voice.get("ios11_90")[23] = R.raw.ios11_90_x;
        voice.get("ios11_90")[24] = R.raw.ios11_90_y;
        voice.get("ios11_90")[25] = R.raw.ios11_90_z;

        voice.get("ios11_100")[0] = R.raw.ios11_100_a;
        voice.get("ios11_100")[1] = R.raw.ios11_100_b;
        voice.get("ios11_100")[2] = R.raw.ios11_100_c;
        voice.get("ios11_100")[3] = R.raw.ios11_100_d;
        voice.get("ios11_100")[4] = R.raw.ios11_100_e;
        voice.get("ios11_100")[5] = R.raw.ios11_100_f;
        voice.get("ios11_100")[6] = R.raw.ios11_100_g;
        voice.get("ios11_100")[7] = R.raw.ios11_100_h;
        voice.get("ios11_100")[8] = R.raw.ios11_100_i;
        voice.get("ios11_100")[9] = R.raw.ios11_100_j;
        voice.get("ios11_100")[10] = R.raw.ios11_100_k;
        voice.get("ios11_100")[11] = R.raw.ios11_100_l;
        voice.get("ios11_100")[12] = R.raw.ios11_100_m;
        voice.get("ios11_100")[13] = R.raw.ios11_100_n;
        voice.get("ios11_100")[14] = R.raw.ios11_100_o;
        voice.get("ios11_100")[15] = R.raw.ios11_100_p;
        voice.get("ios11_100")[16] = R.raw.ios11_100_q;
        voice.get("ios11_100")[17] = R.raw.ios11_100_r;
        voice.get("ios11_100")[18] = R.raw.ios11_100_s;
        voice.get("ios11_100")[19] = R.raw.ios11_100_t;
        voice.get("ios11_100")[20] = R.raw.ios11_100_u;
        voice.get("ios11_100")[21] = R.raw.ios11_100_v;
        voice.get("ios11_100")[22] = R.raw.ios11_100_w;
        voice.get("ios11_100")[23] = R.raw.ios11_100_x;
        voice.get("ios11_100")[24] = R.raw.ios11_100_y;
        voice.get("ios11_100")[25] = R.raw.ios11_100_z;
    }
    private static String TAG = "SoundPlayer";
    // 默认云端发音人
    public static String voicerCloud="xiaoyan";
    // 默认本地发音人
    public static String voicerLocal="xiaoyan";

    // 云端发音人列表
    private static String[] cloudVoicersEntries;
    private static String[] cloudVoicersValue ;

    // 本地发音人列表
    private static String[] localVoicersEntries = {"小燕","小峰"};
    private static String[] localVoicersValue = {"xiaoyan","xiaofeng"};

    // 引擎类型
    private static String mEngineType = SpeechConstant.TYPE_LOCAL;


    private static InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                System.out.println("初始化失败，错误码：" + code);
            }
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
        }
    };

    /**
     * 合成回调监听。
     */
    private static SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
        }

        @Override
        public void onCompleted(SpeechError error) {
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    // 语音合成对象
    private static SpeechSynthesizer mTts;

    public static void initTTS(){
        System.out.println("context: " + context);
        System.out.println("listener: " + mTtsInitListener);
        mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
        setParam();
    }
    public static void xftts(String text){
        mTts.stopSpeaking();
        mTts.startSpeaking(text, mTtsListener);
    }

    public static void setParam(){
        mTts.setParameter(SpeechConstant.PARAMS, null);
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
        mTts.setParameter(SpeechConstant.VOICE_NAME,voicerLocal);

        //合成语速
        mTts.setParameter(SpeechConstant.SPEED, Utility.speechSpeed);
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, Utility.speechPitch);
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, Utility.speechVolume);
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
   }

    private static String getResourcePath(){
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(context, RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(context, RESOURCE_TYPE.assets, "tts/"+ voicerLocal + ".jet"));
        return tempBuffer.toString();
    }

}