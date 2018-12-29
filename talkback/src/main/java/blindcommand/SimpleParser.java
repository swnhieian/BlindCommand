package blindcommand;

// import android.util.Log;

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
    private static final String TAG = "Parser.";
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
        final String SUBTAG = "addTouchPoint";
        Log.d(TAG + SUBTAG, String.format("Touch point: %s, size: %d" , tp.toString(), touchPoints.size()));
        SoundPlayer.click();
        String res = parseInput();
        SoundPlayer.tts(res);
    }
    public void delete() {
        final String SUBTAG = "delete";
        int preSize = touchPoints.size();
        if (touchPoints.size() == 0) {
            SoundPlayer.ding();
        } else if (touchPoints.size() == 1) {
            touchPoints.remove(touchPoints.size() - 1);
            SoundPlayer.ding();
        } else {
            touchPoints.remove(touchPoints.size() - 1);
            SoundPlayer.delete();
        }
        int curSize = touchPoints.size();
        Log.d(TAG + SUBTAG, String.format("size: %d -> %d", preSize, curSize));
    }
    public int getSize() {
        return touchPoints.size();
    }
    public String parseInput() {
        final String SUBTAG = "parseInput";
        candidateSet = parse();
        removeDuplicateWithOrder(candidateSet);
        String res = "";
        if(candidateSet.size() > 0){
            candidateIndex = 0;
            res = candidateSet.get(0).instruction.getInstruction();
        }
        Log.d(TAG + SUBTAG, "Parse Result: ");
        for(Entry entry: candidateSet){
            Log.d(TAG + SUBTAG, "\t" + entry.toString());
        }
        return res;
    }
    public String next() {
        final String SUBTAG = "next";
        if (candidateSet.size() == 0) {
            Log.d(TAG + SUBTAG, "Candidate list size: 0");
            return "";
        }
        candidateIndex = (candidateIndex + 1) % candidateSet.size();
        Entry selectedCandidate = candidateSet.get(candidateIndex);
        Log.d(TAG + SUBTAG, "candidate index: " + candidateIndex);
        Log.d(TAG + SUBTAG, "candidate: " + selectedCandidate.toString());
        return selectedCandidate.instruction.getInstruction();
    }
    public String previous() {
        final String SUBTAG = "previous";
        if (candidateSet.size() == 0) {
            Log.d(TAG + SUBTAG, "Candidate list size: 0");
            return "";
        }
        candidateIndex = (candidateIndex + candidateSet.size() - 1) % candidateSet.size();
        Entry selectedCandidate = candidateSet.get(candidateIndex);
        Log.d(TAG + SUBTAG, "candidate index: " + candidateIndex);
        Log.d(TAG + SUBTAG, "candidate: " + selectedCandidate.toString());
        return selectedCandidate.instruction.getInstruction();
    }
    public String current() {
        final String SUBTAG = "current";
        if (candidateSet.size() == 0) {
            Log.d(TAG + SUBTAG, "Candidate list size: 0");
            return "";
        }
        Entry selectedCandidate = candidateSet.get(candidateIndex);
        Log.d(TAG + SUBTAG, "candidate index: " + candidateIndex);
        Log.d(TAG + SUBTAG, "candidate: " + selectedCandidate.toString());
        return selectedCandidate.instruction.getInstruction();
    }
    public void clear() {
        final String SUBTAG = "clear";
        if (candidateSet != null)
            candidateSet.clear();
        if (touchPoints != null)
            touchPoints.clear();
        SoundPlayer.ding();

        Log.d(TAG + SUBTAG, "Clear input list.");
    }

    public void setKeyboardInfo(int width, int height){
        final String SUBTAG = "setKeyboardInfo";
        this.width = (double)width;
        this.height = (double)height;
        Log.d(TAG + SUBTAG,width + " " + height);
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
        public Instruction instruction;
        public double poss;
        public String content;

        public Entry(Instruction instruction, double poss){
            this(instruction, poss, instruction.getInstruction());
        }

        public String info(){
            return String.format(Locale.ENGLISH,"(%s, %f)", instruction, poss);
        }

        @Override
        public String toString(){
            return String.format(Locale.ENGLISH, "(%s, %s, %.2f)", instruction.getCommand(), instruction.getInstruction(), poss);
        }

    }

    public List<Entry> parse(){
        List<Entry> set = new ArrayList<>();
        // first filter
        for(String ist: InstructionSet.set){
            if(ist.length() >= touchPoints.size()){
                set.add(new Entry(InstructionSet.instructions.get(ist), 0));
            }
        }
        // 第一次点击考虑绝对位置
        Vector2 absFirstTouchPos = touchPoints.get(0).getPosition();
        Vector2 relFirstTouchPos = relativeCoordinate(absFirstTouchPos);
        for(Entry e: set){
            Vector2 firstKeyCenter = keyOfValue(e.instruction.getCommand().charAt(0)).getCenter();
            Vector2 firstKeyRelCenter = relativeCoordinate(firstKeyCenter);
            //System.out.println("" + sd + " " + e.instruction);
//            e.poss += logGaussian(absFirstTouchPos.x, firstKeyCenter.x, 5.0/3);
//            e.poss += logGaussian(absFirstTouchPos.y, firstKeyCenter.y, 0.5);
              e.poss += logGaussian(relFirstTouchPos.x, firstKeyRelCenter.x, 5.0/3);
              e.poss += logGaussian(relFirstTouchPos.y, firstKeyRelCenter.y, 0.5);
            //e.poss *= Gauss2(1.0, sd);
        }


        //  之后的点击考虑相对位置
        for(int i = 1; i < touchPoints.size(); i ++) {
            double maxPoss = Double.NEGATIVE_INFINITY;
            Vector2 actualShift = Vector2.sub(touchPoints.get(i).getPosition(), touchPoints.get(i - 1).getPosition());
            Vector2 relActualShift = relativeCoordinate(actualShift);

            for (Entry e : set) {
                char curChar = e.instruction.getCommand().charAt(i);
                char lastChar = e.instruction.getCommand().charAt(i - 1);
                Vector2 relExpectedShift = relativeCoordinate(Vector2.sub(keyOfValue(curChar).getCenter(), keyOfValue(lastChar).getCenter()));

                e.poss += logGaussian(relActualShift.x, relExpectedShift.x, 5.0/3);
                e.poss += logGaussian(relActualShift.y, relExpectedShift.y, 0.5);
                //e.poss *= Gauss2(1.0, sd);
                //System.out.println("" + sd + " " + curChar + " " + e.instruction + " "  + e.poss);
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
                if (e1.instruction.getCommand().length() < e2.instruction.getCommand().length()){
                    return -1;
                }
                if (e1.instruction.getCommand().length() > e2.instruction.getCommand().length()){
                    return 1;
                }
                return 0;
            }
        });
        System.out.println("Entry set size" +  "parse: " + set.size());

        for(Entry e: set){
            System.out.println("Entry Info" + "parse: " + e.info());
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

        Log.i(TAG + "initKeys", "initKeys: " + keyWidth + " " + keyHeight);
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

        for(MyKey key: keys){
            Log.i("insert key", "initKeys: " + key.info());
        }
    }
    private static double Gauss2(double sigma, double sd){
        return Math.exp(-0.5 * sd / sigma / sigma);
    }

    private static double Gaussian(double x, double mu, double sigma) {
        return Math.exp(-(x-mu)*(x-mu)/2/sigma/sigma)/(sigma * Math.sqrt(2*Math.PI));
    }

    private static double logGaussian(double x, double mu, double sigma) {
        return -Math.log(sigma*Math.sqrt(2*Math.PI)) - (x - mu)*(x-mu)/2/sigma/sigma;
    }

    private MyKey keyOfValue(char c){
        return map.get(c);
    }
}
