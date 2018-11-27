package blindcommand;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;

public class SimpleParser {
    static SimpleParser instance;
    private List<TouchPoint> touchPoints;
    private SimpleParser(){
        touchPoints = new ArrayList<>();
    }
    public static SimpleParser getInstance(){
        if(instance == null)
            instance = new SimpleParser();
        return instance;
    }

    public void addTouchPoint(TouchPoint tp) {
        touchPoints.add(tp);
        System.out.println("play time" + System.currentTimeMillis());
        SoundPlayer.click();
    }
    public void delete() {
        if (touchPoints.size() <= 1) {
            SoundPlayer.ding();
        } else {
            touchPoints.remove(touchPoints.size() - 1);
            SoundPlayer.delete();
        }
    }
    public int getSize() {
        return touchPoints.size();
    }
    public String parseInput() {
        candidateSet = parse();
        removeDuplicateWithOrder(candidateSet);
        String res = "";
        if(candidateSet.size() > 0){
            candidateIndex = 0;
            res = candidateSet.get(0).instruction;
        }
        return InstructionSet.instructions.get(res);
    }
    public String next() {
        if (candidateSet.size() == 0) return "";
        candidateIndex = (candidateIndex + 1) % candidateSet.size();
        return InstructionSet.instructions.get(candidateSet.get(candidateIndex).instruction);
    }
    public String previous() {
        if (candidateSet.size() == 0) return "";
        candidateIndex = (candidateIndex + 1) % candidateSet.size();
        return InstructionSet.instructions.get(candidateSet.get(candidateIndex).instruction);
    }
    public String current() {
        if (candidateSet.size() == 0) return "";
        return InstructionSet.instructions.get(candidateSet.get(candidateIndex).instruction);
    }
    public void clear() {
        if (candidateSet != null)
            candidateSet.clear();
        if (touchPoints != null)
            touchPoints.clear();
    }





    public void setKeyboardInfo(int width, int height){
        this.width = (double)width;
        this.height = (double)height;
        Log.i("Set Keyboard Size",width + " " + height);
        initKeys();
    }

    private void removeDuplicateWithOrder(List<Entry> entryList){
        Set<String> set = new HashSet<>();
        List<Entry> newList = new ArrayList<>();
        for (Iterator<Entry> iter = entryList.iterator(); iter.hasNext();) {
            Entry element = iter.next();
            if (set.add(element.content))
                newList.add(element);
        }
        entryList.clear();
        entryList.addAll(newList);
    }

    @AllArgsConstructor
    class Entry {
        public String instruction;
        /*
        public int lastPos;
        public int curPos;
        */
        public double poss;
        public String content;

        public Entry(String instruction, double poss){
            this(instruction, poss, InstructionSet.instructions.get(instruction));
        }

        public String info(){
            return String.format(Locale.ENGLISH,"(%s, %f)", instruction, poss);
        }

    }

    public List<Entry> parse(){
        List<Entry> set = new ArrayList<>();
        // first filter
        for(String ist: InstructionSet.set){
            if(ist.length() >= touchPoints.size()){
                set.add(new Entry(ist, 100.0));
            }
        }
        // 第一次点击考虑绝对位置
        Vector2 absFirstTouchPos = touchPoints.get(0).getPosition();
        for(Entry e: set){
            Vector2 firstKeyCenter = keyOfValue(e.instruction.charAt(0)).getCenter();
            double sd = Vector2.sqrDistance(relativeCoordinate(firstKeyCenter), relativeCoordinate(absFirstTouchPos));
            //System.out.println("" + sd + " " + e.instruction);
            e.poss *= Gauss2(1.0, sd);
        }


        //  之后的点击考虑相对位置
        for(int i = 1; i < touchPoints.size(); i ++) {
            double maxPoss = 0.0;
            Vector2 actualShift = Vector2.sub(touchPoints.get(i).getPosition(), touchPoints.get(i - 1).getPosition());

            for (Entry e : set) {
                char curChar = e.instruction.charAt(i);
                char lastChar = e.instruction.charAt(i - 1);
                Vector2 expectedShift = Vector2.sub(keyOfValue(curChar).getCenter(), keyOfValue(lastChar).getCenter());
                double sd = Vector2.sqrDistance(relativeCoordinate(expectedShift), relativeCoordinate(actualShift));
                e.poss *= Gauss2(1.0, sd);
                //System.out.println("" + sd + " " + curChar + " " + e.instruction + " "  + e.poss);
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
        Collections.sort(set, new Comparator<Entry>() {
            @Override
            public int compare(Entry e1, Entry e2) {
                if (e1.poss < e2.poss) {
                    return 1;
                }
                if (e1.poss > e2.poss) {
                    return -1;
                }
                return 0;
            }
        });

        Log.i("Entry set size", "parse: " + set.size());

        for(Entry e: set){
            Log.i("Entry Info", "parse: " + e.info());
        }
        return set;
    }

    private Vector2 keySize;

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


    private Vector2 relativeCoordinate(Vector2 v){
        return Vector2.div(v, keySize);
    }
    private void initKeys(){
        keys = new ArrayList<>();
        map = new HashMap<>();

        double keyWidth = width / 10;
        double keyHeight = height / 3;
        keySize = new Vector2(keyWidth, keyHeight);

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
    private static double Gauss2(double sigma, double sd){
        return Math.exp(-0.5 * sd / sigma / sigma);
    }

    private MyKey keyOfValue(char c){
        return map.get(c);
    }



}
