package com.shiweinan.BlindCommand.util;

import android.util.Log;

import com.shiweinan.BlindCommand.touch.TouchPoint;

import java.util.ArrayList;
import java.util.List;

public class SimpleParser implements CommandParser {
    static SimpleParser instance;
    private List<TouchPoint> touchPoints;
    private List<Integer> seq;

    private int width;
    private int height;


    private SimpleParser(){
        touchPoints = new ArrayList<>();
        seq = new ArrayList<>();
    }

    private int convert(TouchPoint touchPoint){
        float relX = touchPoint.getX() / width;
        float relY = touchPoint.getY() / height;

        int x = (int)Math.floor(relX * 3);
        int y = (int)Math.floor(relY * 3);

        return y * 3 + x;
    }

    @Override
    public List<String> parse(){

        List<String> res = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(int i : seq){
            sb.append((char)(i + '0'));
        }
        res.add(sb.toString());
        return res;
    }

    @Override
    public void setKeyboardInfo(int width, int height){
        this.width = width;
        this.height = height;
        Log.i("Set Keyboard Size",width + " " + height);
    }

    public static SimpleParser getInstance(){
        if(instance == null)
            instance = new SimpleParser();
        return instance;
    }


    @Override
    public void add(TouchPoint touchPoint) {

        //int x = convert(touchPoint);
        int x = touchPoint.getKeyNumber();

        Log.i("Touch Point Info: ",touchPoint.info());
        Log.i("Convert To Int: ", "" + x);
        if(x == 1){

            String res = parse().get(0);
            Log.i("Parse Result: ", res);
            accept();
        }

        else {
            this.touchPoints.add(touchPoint);
            seq.add(x);
        }

    }

    @Override
    public void accept(){
        // do something
        this.touchPoints.clear();
        this.seq.clear();
    }

    @Override
    public TouchPoint delete(){
        int lastIndex = seq.size() - 1;
        seq.remove(lastIndex);
        return this.touchPoints.remove(lastIndex);
    }

    @Override
    public void deleteAll(){
        this.touchPoints.clear();
        this.seq.clear();
    }
}
