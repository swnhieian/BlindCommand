package blindcommand;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;

import com.google.android.accessibility.talkback.TalkBackService;

import java.util.HashMap;

public class InstructionSet {
    public static String[] set = {
        "jieping", //  截屏
        "shoudian",//  手电筒
        "dianhua",// 打电话
        "shangwang",// 网络
        "yinliangda",// 音量调节
        "yinliangxiao",
        "weixinhb",// 微信红包
        "weixinzf",// 微信支付
        "weixinyy"// 微信语音
    };
    public static HashMap<String, String> instructions;
    public static TalkBackService service;
    public static boolean lightStatus = false;
    public static void init(TalkBackService service) {
        InstructionSet.service = service;
        instructions = new HashMap<>();
        instructions.put("jieping", "截屏");
        instructions.put("jp", "截屏");
        instructions.put("shoudian", "手电筒");
        instructions.put("shd", "手电筒");
        instructions.put("sd", "手电筒");
        instructions.put("shoudiantong", "手电筒");
        instructions.put("shdt", "手电筒");
        instructions.put("sdt", "手电筒");
        instructions.put("dianhua", "打电话");
        instructions.put("dh", "打电话");
        instructions.put("dadianhua", "打电话");
        instructions.put("ddh", "打电话");
        instructions.put("hongbao", "微信红包");
        instructions.put("hb", "微信红包");
        instructions.put("pengyouquan", "朋友圈");
        instructions.put("pyq", "朋友圈");
        instructions.put("weixin", "打开微信");
        instructions.put("wx", "打开微信");
        instructions.put("zhifubao", "打开支付宝");
        instructions.put("zfb", "打开支付宝");
        instructions.put("zhfb", "打开支付宝");
        instructions.put("yuyin", "微信语音");
        instructions.put("yy", "微信语音");
        instructions.put("fukuan", "扫码付款");
        instructions.put("saomafukuan", "扫码付款");
        instructions.put("smfk", "扫码付款");
        instructions.put("fk", "扫码付款");
        instructions.put("erweima", "微信二维码");
        instructions.put("ewm", "微信二维码");
        instructions.put("wxewm", "微信二维码");
        instructions.put("zhuomian", "返回桌面");
        instructions.put("fanhuizhuomian", "返回桌面");
        instructions.put("fhzm", "返回桌面");
        instructions.put("zhm", "返回桌面");
        instructions.put("zm", "返回桌面");
        //Set<String> keys = instructions.keySet();
        InstructionSet.set = instructions.keySet().toArray(new String[] {});
    }
    public static void execute(String command) {
        switch (command) {
            case "打开微信":
                Intent intent = new Intent();
                ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                InstructionSet.service.startActivity(intent);
                break;
            case "打开支付宝":
                intent = new Intent();
                cmp = new ComponentName("com.eg.android.AlipayGphone", "com.eg.android.AlipayGphone.AlipayLogin");
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                InstructionSet.service.startActivity(intent);
                break;
            case "打电话":
                intent =  new Intent(Intent.ACTION_CALL_BUTTON);//跳转到拨号界面
                InstructionSet.service.startActivity(intent);
                break;
            case "手电筒":
                try {
                    CameraManager manager = (CameraManager) InstructionSet.service.getSystemService(Context.CAMERA_SERVICE);
                    InstructionSet.lightStatus = !InstructionSet.lightStatus;
                    manager.setTorchMode("0", InstructionSet.lightStatus);
                    SoundPlayer.tts("手电筒已"+ (InstructionSet.lightStatus?"打开":"关闭"));
                } catch (Exception e) {
                    InstructionSet.lightStatus = false;
                }
                break;
            case "截屏":
                break;
            case "返回桌面":
                intent =  new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                InstructionSet.service.startActivity(intent);
                break;
            default:
                break;
        }
    }

}
