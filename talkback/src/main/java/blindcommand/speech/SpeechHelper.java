package blindcommand.speech;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;

import blindcommand.SoundPlayer;
import blindcommand.util.FucUtil;
import blindcommand.util.JsonParser;
import blindcommand.util.XmlParser;

public class SpeechHelper{
    private static String TAG = "SpeechHelper";
    // 语音识别对象
    private SpeechRecognizer mAsr;
    private Toast mToast;
    // 缓存
    private SharedPreferences mSharedPreferences;
    // 本地语法文件
    private String mLocalGrammar = null;
    private Context context;
    // 本地语法构建路径
    private String grmPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/msc/test";
    // 返回结果格式，支持：xml,json
    private String mResultType = "json";

    private  final String GRAMMAR_TYPE_BNF = "bnf";

    private SpeechCallback mSpeechCallBack;

    public SpeechHelper(Context context, SpeechCallback speechCallBack, String bnfRule){
        this.context = context;
        mAsr = SpeechRecognizer.createRecognizer(this.context, mInitListener);
//        mToast = Toast.makeText(this.context,"",Toast.LENGTH_SHORT);
        mSpeechCallBack = speechCallBack;
        buildGrammar(bnfRule);
    }

    String mContent;// 语法、词典临时变量
    int ret = 0;// 函数调用返回值

    public void startRecognizing(){
        setParam();
        ret = mAsr.startListening(mRecognizerListener);
        if(ret != ErrorCode.SUCCESS){
            showTip("识别失败，错误码: " + ret);
        }
    }

    public void stopRecognizing(){
        mAsr.stopListening();
        showTip("停止识别");
    }

    public void cancalRecognizing(){
        mAsr.cancel();
        showTip("取消识别");
    }
    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码："+code);
            }
        }
    };

//    /**
//     * 更新词典监听器。
//     */
//    private LexiconListener lexiconListener = new LexiconListener() {
//        @Override
//        public void onLexiconUpdated(String lexiconId, SpeechError error) {
//            if(error == null){
//                showTip("词典更新成功");
//            }else{
//                showTip("词典更新失败,错误码："+error.getErrorCode());
//            }
//        }
//    };

    /**
     * 构建语法监听器。
     */
    private GrammarListener grammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if(error == null){
                SoundPlayer.tts("语法构建成功");
                showTip("语法构建成功：" + grammarId);
            }else{
                showTip("语法构建失败,错误码：" + error.getErrorCode());
            }
        }
    };
//    /**
//     * 获取联系人监听器。
//     */
//    private ContactListener mContactListener = new ContactListener() {
//        @Override
//        public void onContactQueryFinish(String contactInfos, boolean changeFlag) {
//            //获取联系人
//            mLocalLexicon = contactInfos;
//        }
//    };
    /**
     * 识别监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result && !TextUtils.isEmpty(result.getResultString())) {
                Log.d(TAG, "recognizer result：" + result.getResultString());
                String text = "";
//                if (mResultType.equals("json")) {
//                    text = JsonParser.parseGrammarResult(result.getResultString(), SpeechConstant.TYPE_LOCAL);
//                } else if (mResultType.equals("xml")) {
//                    text = XmlParser.parseNluResult(result.getResultString());
//                }else{
//                    text = result.getResultString();
//                }
//                // 显示
//                showTip("result: " + text);
                System.out.println(result.getResultString());
                mSpeechCallBack.onResult(JsonParser.parseGrammarResult(result.getResultString(), SpeechConstant.TYPE_LOCAL));
            } else {
                Log.d(TAG, "recognizer result : null");
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
            mSpeechCallBack.onEndOfSpeech();
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
            mSpeechCallBack.onBeginOfSpeech();
        }

        @Override
        public void onError(SpeechError error) {
            showTip("onError Code："	+ error.getErrorCode() + " " + error.getErrorDescription());
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private void showTip(final String str) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//        mToast.setText(str);
//        mToast.show();
//            }
//        });
    }

    public void setParam(){
        mAsr.setParameter(SpeechConstant.PARAMS, null);
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, mResultType);
        mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, "action");
        // 设置识别的门限值
        mAsr.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
        // 使用8k音频的时候请解开注释
//			mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/asr.wav");
    }
    private void buildGrammar(String bnfRule){
//        mLocalGrammar = FucUtil.readFile(this.context, "call.bnf", "utf-8");
        mLocalGrammar = bnfRule;
//        ((EditText) findViewById(R.id.isr_text)).setText(mLocalGrammar);
        mContent = new String(mLocalGrammar);
        mAsr.setParameter(SpeechConstant.PARAMS, null);
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
        ret = mAsr.buildGrammar(GRAMMAR_TYPE_BNF, mContent, grammarListener);
        if(ret != ErrorCode.SUCCESS) {
            showTip("语法构建错误，错误码: " + ret);
        }
        else{
            showTip("语法构建成功");
        }
    }
//    /**
//     * 参数设置
//     * @return
//     */
//    public boolean setParam(){
//        boolean result = false;
//        // 清空参数
//        mAsr.setParameter(SpeechConstant.PARAMS, null);
//        // 设置识别引擎
//        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
//        if("cloud".equalsIgnoreCase(mEngineType))
//        {
//            String grammarId = mSharedPreferences.getString(KEY_GRAMMAR_ABNF_ID, null);
//            if(TextUtils.isEmpty(grammarId))
//            {
//                result =  false;
//            }else {
//                // 设置返回结果格式
//                mAsr.setParameter(SpeechConstant.RESULT_TYPE, mResultType);
//                // 设置云端识别使用的语法id
//                mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, grammarId);
//                result =  true;
//            }
//        }
//        else
//        {
//            // 设置本地识别资源
//            mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
//            // 设置语法构建路径
//            mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
//            // 设置返回结果格式
//            mAsr.setParameter(SpeechConstant.RESULT_TYPE, mResultType);
//            // 设置本地识别使用语法id
//            mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, "action");
//            // 设置识别的门限值
//            mAsr.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
//            // 使用8k音频的时候请解开注释
////			mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
//            result = true;
//        }
//
//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
//        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/asr.wav");
//        return result;
//    }

    //获取识别资源路径
    private String getResourcePath(){
        StringBuffer tempBuffer = new StringBuffer();
        //识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this.context, RESOURCE_TYPE.assets, "asr/common.jet"));
        return tempBuffer.toString();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if( null != mAsr ){
//            // 退出时释放连接
//            mAsr.cancel();
//            mAsr.destroy();
//        }
//    }

}
