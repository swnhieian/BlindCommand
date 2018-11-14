package com.shiweinan.BlindCommand.util;

import java.util.HashMap;
import java.util.Set;

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
    public static void init() {

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
        instructions.put("zhifb", "打开支付宝");
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
        //Set<String> keys = instructions.keySet();
        InstructionSet.set = instructions.keySet().toArray(new String[] {});
    }
}
