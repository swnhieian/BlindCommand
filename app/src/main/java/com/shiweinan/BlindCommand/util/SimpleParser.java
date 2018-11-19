package com.shiweinan.BlindCommand.util;


import android.media.SoundPool;
import android.util.Log;

import com.shiweinan.BlindCommand.keyboard.MyKey;
import com.shiweinan.BlindCommand.touch.TouchPoint;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;

public class SimpleParser {
    enum State{INPUT, CHOOSE}

    static SimpleParser instance;
    private List<TouchPoint> touchPoints;

    private State state = State.INPUT;

    private List<Entry> candidateSet = null;
    private int candidateIndex = 0;

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
               case 0: x_offset = 0.0f; break;
               case 1: x_offset = keyWidth * 0.5f; break;
               case 2: x_offset = keyWidth * 1.5f; break;
           }
           for (int j = 0; j < keyValue[i].length; j++){
               MyKey t = new MyKey(keyValue[i][j], x_offset + keyWidth *j, keyHeight * i, keyWidth, keyHeight);
               keys.add(t);
               map.put(keyValue[i][j], t);
           }
       }

       //keys.add(new MyKey('-', 0, keyHeight * 2, keyWidth * 1.5, keyHeight));
        // keys.add(new MyKey('+', keyWidth * 8.5, keyHeight * 2, keyWidth * 1.5, keyHeight));
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
        /*
        public int lastPos;
        public int curPos;
        */
        public double poss;

        public String info(){
            return String.format(Locale.ENGLISH,"(%s, %f)", instruction, poss);
        }

    }

    public List<Entry> parse(){
        List<Entry> set = new ArrayList<>();
        // first filter
        for(String ist: InstructionSet.set){
            if(ist.length() >= touchPoints.size()){
                set.add(new Entry(ist, 1000.0));
            }
        }

        for(int i = 1; i < touchPoints.size(); i ++) {
            double maxPoss = 0;
            for (Entry e : set) {
                char curChar = e.instruction.charAt(i - 1);
                char lastChar = e.instruction.charAt(i);
                Vector2 expectedShift = Vector2.sub(keyOfValue(lastChar).getCenter(), keyOfValue(curChar).getCenter());
                Vector2 actualShift = Vector2.sub(touchPoints.get(i).getPosition(), touchPoints.get(i - 1).getPosition());
                double sd = Vector2.sqrDistance(expectedShift, actualShift);
                e.poss *= Gauss2(1.0, sd);
                maxPoss = Math.max(e.poss, maxPoss);
            }

            Iterator<Entry> it = set.iterator();
            while (it.hasNext()) {
                Entry e = it.next();
                if (e.poss < maxPoss * 0.01) {
                    it.remove();
                }
            }
        }
        Log.i("Entry set size", "parse: " + set.size());

        for(Entry e: set){
            Log.i("Entry Info", "parse: " + e.info());
        }

        Collections.sort(set, new Comparator<Entry>() {
            @Override
            public int compare(Entry e1, Entry e2) {
                if (e1.poss > e2.poss) {
                    return 1;
                }
                if (e1.poss < e2.poss) {
                    return -1;
                }
                return 0;
            }
        });

        return set;
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

    public String performSwipeLeft(){
        if(state == State.INPUT) {
            if (!touchPoints.isEmpty()) {
                touchPoints.remove(touchPoints.size() - 1);
                SoundPlayer.delete();
            } else {
                SoundPlayer.ding();
            }
            Log.i("Remove Touch Point", "size: " + touchPoints.size());
            return "delete input, size:" + touchPoints.size();
        }
        else{
           if(candidateIndex > 0) {
               candidateIndex--;
               Log.i("select candidate","candidateIndex: " + candidateIndex);
               select(candidateSet.get(candidateIndex).instruction);
           }
           return "select candidate, candidateIndex: " + candidateIndex;
        }
    }
    public String performSwipeRight(){

        if(touchPoints.size() <= 1)
            return "Parse error: Only " + touchPoints.size() + " touch.";

        if(state == State.INPUT) {
            state = State.CHOOSE;


            //List<Entry> parseResult = parse();
            candidateSet = parse();
            String res = "";
            if(candidateSet.size() > 0){
                candidateIndex = 0;
                res = candidateSet.get(0).instruction;
                select(candidateSet.get(candidateIndex).instruction);
            }

            return "Parse Result: " + res + "-" + InstructionSet.instructions.get(res);
        }
        else{
            if(candidateIndex + 1 < candidateSet.size()){
                candidateIndex ++;
            }
            select(candidateSet.get(candidateIndex).instruction);
            Log.i("select candidate","candidateIndex: " + candidateIndex);
            return "select candidate, candidateIndex: " + candidateIndex;
        }
    }
    public String performSwipeUp(){
        if(state == State.CHOOSE){
           accept(candidateSet.get(candidateIndex).instruction);
           state = State.INPUT;
        }
        else{
           Log.i("nop","performSwipeUp");
        }
        return "swipe up";
    }
    public String performSwipeDown(){
        if(state == State.CHOOSE){
            state = State.INPUT;
            touchPoints.clear();
            candidateSet = null;
            candidateIndex = 0;
        }
        else{
            Log.i("nop","performSwipeDown");
        }
        return "swipe down";
    }
    public String performTouch(TouchPoint touchPoint){
        if(state == State.CHOOSE) {
            Log.i("state change", "performTouch : choose -> input");
            state = State.INPUT;
        }

        char c = '?';
        for (MyKey key : keys) {
            if (key.contains(touchPoint)) {
                c = key.getC();
                Log.i("Distance", "dis: " + dis(touchPoint, key) + " Gauss: " + Gauss2(1.0, sqrdis(touchPoint, key)));
                Log.i("Info", "TouchPoint " + touchPoint.info());
                Log.i("Info", "Key Info " + key.allInfo());
                break;
            }
        }
        SoundPlayer.click();

        touchPoints.add(touchPoint);
        Log.i("Add Touch Point", "press: " + c);
        return "input " + c;
    }
    public void accept(String instruction){
        SoundPlayer.tts(InstructionSet.instructions.get(instruction));
        touchPoints.clear();
    }
    public void select(String instruction){
        SoundPlayer.tts(InstructionSet.instructions.get(instruction));
    }
}
