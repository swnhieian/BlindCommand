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

import java.util.HashMap;
import java.util.List;

public class InstructionSet {
    private static final String TAG = "InstructionSet.";
    private Context service;
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

    private Executor executor;
    public InstructionSet(List<Instruction> allInstructions) {
        //this.service = service;
        //this.executor = new Executor(Utility.service);
        instructions = new HashMap<>();
        //List<Instruction> allInstructions = executor.getInstructions();
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
//    public void InstructionSet_ori(Context service) {
//        this.service = service;
//        instructions = new HashMap<>();
//        String [][] iter = ins;
//        if (Utility.getLanguage().equals("CN")) {
//            iter = ins;
//        } else if (Utility.getLanguage().equals("US")) {
//            iter = ins_en;
//        }
//        for (String[] i:iter) {
//            String cmd = i[1].toLowerCase();
//            instructions.put(cmd, new Instruction(cmd, i[0]));
//            cmd = i[1].replaceAll("[a-z]+", "").toLowerCase();
//            if (cmd.length() >=2) {
//                instructions.put(cmd, new Instruction(cmd, i[0]));
//            }
//        }
//        //Set<String> keys = instructions.keySet();
//        this.dict = instructions.keySet().toArray(new String[] {});
////        List<PackageInfo> packList = service.getPackageManager().getInstalledPackages(0);
////        System.out.println("=========");
////        PackageManager packageManager = null;
////
////
////            packageManager = service.getPackageManager();
////
////        for (PackageInfo pkgInfo: packList) {
////            System.out.println(pkgInfo.packageName);
////            String applicationName =
////                    (String) packageManager.getApplicationLabel(pkgInfo.applicationInfo);
////            System.out.println(applicationName);
////            System.out.println("----------");
////
////        }
////        System.out.println("=========");
//        this.executor = new Executor(Utility.service);
//    }
    public void execute(Instruction ins) {
        final String SUBTAG = "execute";
        executor.execute(ins);
        /*
        switch (commandName) {
            case "Open Wechat":
            case "打开微信":
                Intent intent = new Intent();
                ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                this.service.startActivity(intent);
                break;
            case "Open Alipay":
            case "打开支付宝":
                intent = new Intent();
                cmp = new ComponentName("com.eg.android.AlipayGphone", "com.eg.android.AlipayGphone.AlipayLogin");
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                this.service.startActivity(intent);
                break;
            case "Phone":
            case "打电话":
                intent =  new Intent(Intent.ACTION_CALL_BUTTON);//跳转到拨号界面
                this.service.startActivity(intent);
                break;
            case "Open Camera":
            case "打开相机":
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                this.service.startActivity(intent);
                break;
            case "Flashlight":
            case "手电筒":
                try {
                    CameraManager manager = (CameraManager) this.service.getSystemService(Context.CAMERA_SERVICE);
                    this.lightStatus = !this.lightStatus;
                    manager.setTorchMode("0", this.lightStatus);
                    SoundPlayer.tts("手电筒已"+ (this.lightStatus?"打开":"关闭"));
                } catch (Exception e) {
                    this.lightStatus = false;
                }
                break;
            case "截屏":
                break;
            case "Home":
            case "返回桌面":
                intent =  new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.service.startActivity(intent);
                break;
            case "Open Settings":
            case "打开设置":
                intent =  new Intent(Settings.ACTION_SETTINGS);
                this.service.startActivity(intent);
                break;
            case "Open TaoBao":
            case "打开淘宝":
                String pkgName = "com.taobao.taobao";
                launchApp(pkgName);
                break;
            case "打开微博":
            case "Open Weibo":
                launchApp("com.sina.weibo");
                break;
            default:
                Toast.makeText(service, commandName, Toast.LENGTH_SHORT).show();
                break;
        }
        */
    }
    public void launchApp(String pkgName) {
        PackageManager packageManager = service.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo != null) {
            Intent intent = packageManager.getLaunchIntentForPackage(pkgName);
            if (intent != null) {
                service.startActivity(intent);
            }
        }
    }

}