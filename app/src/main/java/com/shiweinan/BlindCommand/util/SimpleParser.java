package com.shiweinan.BlindCommand.util;


import android.media.SoundPool;
import android.util.Log;

import com.shiweinan.BlindCommand.keyboard.MyKey;
import com.shiweinan.BlindCommand.touch.TouchPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;

public class SimpleParser {
    static SimpleParser instance;
    private List<TouchPoint> touchPoints;

    private List<MyKey> keys;
    private Map<Character, MyKey> map;
    private static char[][] keyValue =
            {{'q','w','e','r','t','y','u','i','o','p'},
               {'a','s','d','f','g','h','j','k','l'},
                   {'z','x','c','v','b','n','m'}};


    private double width;
    private double height;


    private SimpleParser(){
        touchPoints = new ArrayList<>();
    }
    private void initKeys(){
       keys = new ArrayList<>();
       map = new HashMap<>();

       double keyWidth = width / 10;
       double keyHeight = height / 3;
       Log.i("Keysize", "initKeys: " + keyWidth + " " + keyHeight);
       double x_offset = 0;
       for(int i = 0; i < keyValue.length; i ++) {
           switch(i){
               case 0: x_offset = 0f; break;
               case 1: x_offset = keyWidth * 0.5f; break;
               case 2: x_offset = keyWidth * 1.5f; break;
           }
           for (int j = 0; j < keyValue[i].length; j++){
               MyKey t = new MyKey(keyValue[i][j], x_offset + keyWidth *j, keyHeight * i, keyWidth, keyHeight);
               keys.add(t);
               map.put(keyValue[i][j], t);
           }
       }
       keys.add(new MyKey('-', 0, keyHeight * 2, keyWidth * 1.5, keyHeight));
        keys.add(new MyKey('+', keyWidth * 8.5, keyHeight * 2, keyWidth * 1.5, keyHeight));
       for(MyKey key: keys){
           Log.i("insert key", "initKeys: " + key.info());
       }
    }

    private static double dis(TouchPoint tp, MyKey k){
        double dx = tp.getX() - k.getCx();
        double dy = tp.getY() - k.getCy();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static double sqrdis(TouchPoint tp, MyKey k){
        double dx = tp.getX() - k.getCx();
        double dy = tp.getY() - k.getCy();
        return dx * dx + dy * dy;
    }

    private static double Gauss2(double sigma, double sd){
        sd *= 0.0001; // rescale range to almost 1
        return Math.exp(-0.5 * sd / sigma / sigma);
    }

    private MyKey keyOfValue(char c){
        return map.get(c);
    }


    @AllArgsConstructor
    class Entry {
        public String instruction;
        public int curInputCnt;
        public double poss;

        public String info(){
            return String.format("(%s, %d, %f)", instruction, curInputCnt, poss);
        }

    }

    public String parse(){
        List<Entry> set = new ArrayList<>();
        // first filter
        for(String ist: InstructionSet.set){
            if(ist.length() >= touchPoints.size()){
                set.add(new Entry(ist, 0, 100.0));
            }
        }

        for(int i = 0; i < touchPoints.size(); i ++){
            double maxPoss = 0;
            for(Entry e: set){
                char c = e.instruction.charAt(e.curInputCnt);
                e.poss *= Gauss2(1.0, sqrdis(touchPoints.get(i), keyOfValue(c)));
                maxPoss = Math.max(e.poss, maxPoss);
                e.curInputCnt ++;
            }
            final double m = maxPoss;
            Iterator<Entry> it = set.iterator();
            while(it.hasNext()){
                Entry e = it.next();
                if(e.poss < maxPoss * 0.1){
                    it.remove();
                }
            }
            /*
            for(Entry e: set){
                if(e.poss < (maxPoss * 0.1)){ // configurable
                    set.remove(e);
                }
            }
            */
        }
        Log.i("Entry set size", "parse: " + set.size());

        for(Entry e: set){
            Log.i("Entry Info", "parse: " + e.info());
        }
        touchPoints.clear();
        if(set.size() == 0){
            return "";
        }
        else{
            return Collections.max(set, new Comparator<Entry>(){
                @Override
                public int compare(Entry e1, Entry e2){
                    if(e1.poss > e2.poss){
                        return 1;
                    }
                    if(e1.poss < e2.poss){
                        return -1;
                    }
                    return 0;
                }
            }).instruction;
        }
    }

    public void setKeyboardInfo(int width, int height){
        this.width = (double)width;
        this.height = (double)height;
        Log.i("Set Keyboard Size",width + " " + height);
        initKeys();
    }

    public static SimpleParser getInstance(){
        if(instance == null)
            instance = new SimpleParser();
        return instance;
    }

    public String press(TouchPoint touchPoint){
        char c = '?';
        for(MyKey key: keys){
            if(key.contains(touchPoint)){
                c = key.getC();
                Log.i("Distance", "dis: " + dis(touchPoint, key) + " Gauss: " + Gauss2(1.0, sqrdis(touchPoint, key)));
                Log.i("Info","TouchPoint " + touchPoint.info());
                Log.i("Info","Key Info " + key.allInfo());
                break;
            }
        }
        Log.i("Key Pressed", "press: " + c);
        if(c >= 'a' && c <= 'z'){
            SoundPlayer.click();
            touchPoints.add(touchPoint);
            Log.i("Add Touch Point", "press: " + c);
            return "input " + c;
        }
        else if(c == '-'){

            if (!touchPoints.isEmpty()) {
                SoundPlayer.delete();
                touchPoints.remove(touchPoints.size() - 1);
            } else {
                SoundPlayer.ding();
            }
            Log.i("Remove Touch Point", "size: " + touchPoints.size());
            return "delete input, size:" + touchPoints.size();

        }
        else if(c == '+'){
            String res = parse();
            SoundPlayer.tts(InstructionSet.instructions.get(res));
            return "Parse Result: " + res + "-" + InstructionSet.instructions.get(res);
        }
        return "";
    }


}
