package com.shiweinan.BlindCommand;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SimpleParser {
    HashMap<Character, Key> allKeys;
    ArrayList<TouchPoint> touchPoints;
    List<Entry> candidateList;
    InstructionSet instructionSet;
    int currentIndex = 0;
    public SimpleParser(ArrayList<Key> keys, InstructionSet instructionSet) {
        allKeys = new HashMap<>();
        for (Key k: keys) {
            allKeys.put(Character.toLowerCase(k.name), k);
        }
        this.instructionSet = instructionSet;
        touchPoints = new ArrayList<>();
        candidateList = new ArrayList<>();
    }
    public void addTouchPoint(long time, float x, float y) {
        touchPoints.add(new TouchPoint(time, x, y));
        parse();
    }
    public void clear() {
        touchPoints.clear();
        candidateList.clear();
        currentIndex = 0;
    }
    public Instruction getCurrent() {
        if (candidateList.size() == 0) {
            return new Instruction("null", "无结果");
        }
        return instructionSet.instructions.get(candidateList.get(currentIndex).instruction);
    }
    private static double logGaussian(double x, double mu, double sigma) {
        return -Math.log(sigma*Math.sqrt(2*Math.PI)) - (x - mu)*(x-mu)/2/sigma/sigma;
    }
    public void parse() {
        List<Entry> set =new ArrayList<>();
        for (String ins:instructionSet.dict) {
            if (ins.length() >= touchPoints.size()) {
                set.add(new Entry(ins, 0.0));
            }
        }
        Key firstKey = allKeys.get('a');
        for (Entry entry: set) {
            firstKey = allKeys.get(Character.toLowerCase(entry.instruction.charAt(0)));
            entry.poss += logGaussian(firstKey.x, touchPoints.get(0).x, 5*firstKey.width/3.0);
            entry.poss += logGaussian(firstKey.y, touchPoints.get(0).y, 0.5*firstKey.height);
        }
        double maxPoss = Double.NEGATIVE_INFINITY;
        for (int i=1; i<touchPoints.size(); i++) {
            double relX = touchPoints.get(i).x - touchPoints.get(i - 1).x;
            double relY = touchPoints.get(i).y - touchPoints.get(i - 1).y;
            for (Entry e: set) {
                char curChar = e.instruction.charAt(i);
                char lastChar = e.instruction.charAt(i-1);
                double relXExpected = allKeys.get(curChar).x - allKeys.get(lastChar).x;
                double relYExpected = allKeys.get(curChar).y - allKeys.get(lastChar).y;
                e.poss += logGaussian(relX, relXExpected, 5.0* firstKey.width / 3);
                e.poss += logGaussian(relY, relYExpected, 0.5* firstKey.height);
                System.out.println(e.instruction + " " + e.poss);
                maxPoss = Math.max(e.poss, maxPoss);
            }
            Iterator<Entry> it = set.iterator();
            while (it.hasNext()) {
                Entry e = it.next();
                if (e.poss < maxPoss * 100) {
                    it.remove();
                }
            }
        }
        Collections.sort(set, new Comparator<Entry>() {
            @Override
            public int compare(Entry e1, Entry e2) {
                if (e1.poss > e2.poss) {
                    return -1;
                }
                if (e1.poss < e2.poss) {
                    return 1;
                }
                if (e1.instruction.length() < e2.instruction.length()){
                    return -1;
                }
                if (e1.instruction.length() > e2.instruction.length()){
                    return 1;
                }
                return 0;
            }
        });
        System.out.println("Entry set size" +  "parse: " + set.size());

        for(Entry e: set){
            System.out.println("Entry Info" + "parse: " + e.info());
        }
        candidateList = set;
    }

    public void previous() {
        if (candidateList.size() == 0) return;
        currentIndex = (currentIndex + candidateList.size() - 1) % candidateList.size();
    }
    public void next() {
        if (candidateList.size() == 0) return;
        currentIndex = (currentIndex + 1) % candidateList.size();
    }


}
