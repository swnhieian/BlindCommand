package blindcommand;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InstructionSet {
    private static final String TAG = "InstructionSet.";
    public String[] dict;
    //public static HashMap<String, String> instructions;
    public HashMap<String, Instruction> instructions;
    public static String[][] ins = {
            {"截屏", "JiePing"},
            {"手电筒", "ShouDianTong"},
            {"打电话", "DaDianHua"},
            {"打电话", "DianHua"},
            {"微信红包", "HongBao"},
            {"朋友圈", "PengYouQuan"},
            {"打开微信", "WeiXin"},
            {"打开微信", "w"},
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
            {"撤销", "CheXiao"},   //24
            {"打开微博", "WeiBo"}
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
            {"Undo", "Undo"},
            {"Open Weibo", "Weibo"}
    };
    public InstructionSet(List<Instruction> allInstructions) {
        instructions = new HashMap<>();
        for (Instruction instruction:allInstructions) {
            // 区分app名和全简拼
            instruction.pinyin = instruction.pinyin.replaceAll("[^a-zA-Z]", "");
            instructions.put(instruction.pinyin.toLowerCase() + "|" + instruction.meta.appName + "|0", instruction);
            String cmd = instruction.pinyin.replaceAll("[a-z]+", "").toLowerCase();
            if(cmd.length() >= 2) {
                instructions.put(cmd + "|" + instruction.meta.appName + "|1", instruction);
            }
        }
        // instructions.put("wo|新浪微博|0", new Instruction("我","我","Wo",new JsonAppInfo("com.sina.weibo","新浪微博","XinLangWeiBo")));
        this.dict = instructions.keySet().toArray(new String[]{});
    }
    public InstructionSet(Parameter[] names) {
        List<Instruction> allInstructions = new ArrayList<>();
        for (int i=0; i<names.length; i++) {
            allInstructions.add(new Instruction(names[i].id, names[i].name, names[i].id, new JsonAppInfo()));
        }
        instructions = new HashMap<>();
        for (Instruction instruction:allInstructions) {
            // 区分app名和全简拼
            instructions.put(instruction.pinyin.toLowerCase() + "|" + instruction.meta.appName + "|0", instruction);
            String cmd = instruction.pinyin.replaceAll("[a-z]+", "").toLowerCase();
            if(cmd.length() >= 2) {
                instructions.put(cmd + "|" + instruction.meta.appName + "|1", instruction);
            }
        }
        // instructions.put("wo|新浪微博|0", new Instruction("我","我","Wo",new JsonAppInfo("com.sina.weibo","新浪微博","XinLangWeiBo")));
        this.dict = instructions.keySet().toArray(new String[]{});
    }
}