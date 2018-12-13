package blindcommand;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.accessibility.talkback.TalkBackService;

import java.util.HashMap;

public class InstructionSet {
    public static String[] set;
    //public static HashMap<String, String> instructions;
    public static TalkBackService service;
    public static boolean lightStatus = false;
    public static HashMap<String, Instruction> instructions;
    public static String[][] ins = {
            {"截屏", "JiePing"},
            {"手电筒", "ShouDianTong"},
            {"打电话", "DaDianHua"},
            {"打电话", "DianHua"},
            {"微信红包", "HongBao"},
            {"朋友圈", "PengYouQuan"},
            {"打开微信", "WeiXin"},
            {"打开支付宝", "ZhiFuBao"},
            {"打开录音机", "LuYinJi"},
            {"微信语音", "YuYin"},
            {"微信语音", "WeiXinYuYin"},
            {"扫码付款", "FuKuan"},
            {"微信二维码", "ErWeiMa"},
            {"返回桌面", "ZhuoMian"},
            {"返回桌面", "FanHuiZhuoMian"},  //15
            {"打开相机", "XiangJi"},
            {"打开淘宝", "TaoBao"},
            {"打开邮箱", "YouXiang"},
            {"打开闹钟", "NaoZhong"},
            {"搜索", "SouSuo"},    //20
            {"打开设置", "SheZhi"},
            {"蓝牙", "LanYa"},
            {"无线网络", "WuXianWang"},
            {"撤销", "CheXiao"}    //24
    };
    public static String[][] ins_en = {
        {"Screenshot", "Screenshot"},
        {"Flashlight", "Flashlight"},
        {"Phone", "phone"},
        {"Red Packet", "RedPacket"},
        {"Moments", "Moments"},
        {"Open Wechat", "Wechat"},
        {"Open Alipay", "Alipay"},
        {"Open Recorder", "Recorder"},
        {"Send Voice Message", "VoiceMessage"},
        {"Scan QR Code", "ScanQRCode"},
        {"My QR Code", "QRCode"},
        {"Home", "Home"},
        {"Open Camera", "Camera"},
        {"Open TaoBao", "TaoBao"},
        {"Open Email", "Email"},
        {"Open Alarm Clock", "AlarmClock"},
        {"Search", "Search"},
        {"Open Settings", "Settings"},
        {"Open Bluetooth", "BlueTooth"},
        {"Open Wifi", "Wifi"},  //20
        {"Undo", "Undo"}
    };
    public static void init(TalkBackService service) {
        InstructionSet.service = service;
        instructions = new HashMap<>();
        String [][] iter = ins;
        if (Utility.getLanguage().equals("CN")) {
            iter = ins;
        } else if (Utility.getLanguage().equals("US")) {
            iter = ins_en;
        }
        for (String[] i:iter) {
            String cmd = i[1].toLowerCase();
            instructions.put(cmd, new Instruction(cmd, i[0]));
            cmd = i[1].replaceAll("[a-z]+", "").toLowerCase();
            if (cmd.length() >=2) {
                instructions.put(cmd, new Instruction(cmd, i[0]));
            }
        }
        //Set<String> keys = instructions.keySet();
        InstructionSet.set = instructions.keySet().toArray(new String[] {});
    }
    public static void execute(String command) {
        switch (command) {
            case "Open Wechat":
            case "打开微信":
                Intent intent = new Intent();
                ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                InstructionSet.service.startActivity(intent);
                break;
            case "Open Alipay":
            case "打开支付宝":
                intent = new Intent();
                cmp = new ComponentName("com.eg.android.AlipayGphone", "com.eg.android.AlipayGphone.AlipayLogin");
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                InstructionSet.service.startActivity(intent);
                break;
            case "Phone":
            case "打电话":
                intent =  new Intent(Intent.ACTION_CALL_BUTTON);//跳转到拨号界面
                InstructionSet.service.startActivity(intent);
                break;
            case "Open Camera":
            case "打开相机":
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                InstructionSet.service.startActivity(intent);
                break;
            case "Flashlight":
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
            case "Home":
            case "返回桌面":
                intent =  new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                InstructionSet.service.startActivity(intent);
                break;
            case "Open Settings":
            case "打开设置":
                intent =  new Intent(Settings.ACTION_SETTINGS);
                InstructionSet.service.startActivity(intent);
                break;
            default:
                Toast.makeText(service, command, Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
