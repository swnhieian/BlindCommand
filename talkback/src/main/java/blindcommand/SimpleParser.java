package blindcommand;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleParser implements  Parser {
    final String LOGTAG = "SimpleParser";
//    final static double IN_SAME_APP_BONUS = Math.log(4.0);
//    final static double IN_SYSTEM_BONUS = Math.log(2.0);
    final static double FREQUENCY_WEIGHT = 1.0;
    final static double LENGTH_WEIGHT = 1.0;
    final static double STAY_SAME_APP = 0.8;
    HashMap<Character, Key> allKeys;
    ArrayList<TouchPoint> touchPoints;
    List<Entry> candidateList;
    InstructionSet instructionSet;

    final static double OFFSET_X = - 0.90 / 6.39;
    final static double OFFSET_Y = 3.37 / 10.07;
    final static double SIGMA_X = 2.92 / 6.39;
    final static double SIGMA_Y = 6.47 / 10.07;

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
        Log.i(LOGTAG, "touchPoint:"+x+"," +y);
        touchPoints.add(new TouchPoint(time, x, y));
        parse();
    }
    public void clear() {
        touchPoints.clear();
        candidateList.clear();
        currentIndex = 0;
    }
    public ParseResult getCurrent() {
        if (candidateList.size() == 0) {
            return new ParseResult(new Instruction("null", "无结果", "WuJieGuo", new JsonAppInfo(), 4),
                                    -1, 0, false, false);
        }
        ParseResult pr = new ParseResult(candidateList.get(currentIndex).instruction, currentIndex, candidateList.size(), false, candidateList.get(currentIndex).appFirst);
        for(Entry candidate: candidateList){
            if(candidate.instruction.hasSameCommand(pr.instruction) && !candidate.instruction.inSameApp(pr.instruction)){
                pr.hasSameName = true;
                break;
            }
        }
        return pr;
    }
    private static double logGaussian(double x, double mu, double sigma) {
        return -Math.log(sigma*Math.sqrt(2*Math.PI)) - (x - mu)*(x-mu)/2/sigma/sigma;
    }
    public void parse() {
        final String packageName = Utility.getPackageName();
        List<Entry> set = new ArrayList<>();
        Map<String, Double> transmit;
        transmit = new HashMap<>();
        transmit.put(packageName, STAY_SAME_APP);
        double total = 0.0;
        for(JsonAppInfo app: Utility.allApps){
            if(app.packageName.equals(packageName)) continue;
            total += app.useFrequency;
        }
        for(JsonAppInfo app: Utility.allApps){
            if(app.packageName.equals(packageName)) continue;
            transmit.put(app.packageName, (1 - STAY_SAME_APP) / total * app.useFrequency);
        }
        for (String ins : instructionSet.dict) {
            String[] insArray = ins.split("\\|");
//            for(String s: insArray){
//                System.out.print(s);
//            }
//            System.out.println("");
            if (insArray[0].length() == touchPoints.size()) {
                Instruction instruction = instructionSet.instructions.get(ins);
                System.out.println(instruction.meta.packageName);
                double initial_poss = Math.log(transmit.get(instruction.meta.packageName));
//                if(instruction.meta.packageName.equals(packageName)){
//                    initial_poss += IN_SAME_APP_BONUS;
//                }
//                if(instruction.meta.packageName.equals("System")){
//                    initial_poss += IN_SYSTEM_BONUS;
//                }
                if(instruction.meta.packageName.equals(packageName)){
                    if(insArray[1].equals("9") || insArray[1].equals("10")){
                        set.add(new Entry(insArray[0], instruction, initial_poss, false));
                    }
                }
                else {
                    set.add(new Entry(insArray[0], instruction, initial_poss, insArray[1].equals("2") || insArray[1].equals("3")));
                }
            }
        }
        Key firstKey = allKeys.get('a');
        for (Entry entry: set) {
            firstKey = allKeys.get(Character.toLowerCase(entry.command.charAt(0)));
            entry.poss += logGaussian(firstKey.x, touchPoints.get(0).x + OFFSET_X * firstKey.width, SIGMA_X * firstKey.width);
            entry.poss += logGaussian(firstKey.y, touchPoints.get(0).y + OFFSET_Y * firstKey.height, SIGMA_Y * firstKey.height);
        }
        double maxPoss = Double.NEGATIVE_INFINITY;
        for (int i=1; i<touchPoints.size(); i++) {
            double relX = touchPoints.get(i).x - touchPoints.get(i - 1).x;
            double relY = touchPoints.get(i).y - touchPoints.get(i - 1).y;
            for (Entry e: set) {
                char curChar = e.command.charAt(i);
                char lastChar = e.command.charAt(i-1);
                double relXExpected = allKeys.get(curChar).x - allKeys.get(lastChar).x;
                double relYExpected = allKeys.get(curChar).y - allKeys.get(lastChar).y;
                e.poss += logGaussian(relX, relXExpected, SIGMA_X * firstKey.width);
                e.poss += logGaussian(relY, relYExpected, SIGMA_Y * firstKey.height);
               // System.out.println(e.instruction + " " + e.poss);
                maxPoss = Math.max(e.poss, maxPoss);
            }
//            Iterator<Entry> it = set.iterator();
//            while (it.hasNext()) {
//                Entry e = it.next();
//                if (e.poss < maxPoss * 100) {
//                    it.remove();
//                }
//            }
        }
        System.out.println(packageName);
        final double lambda = 1. / set.size();
        Collections.sort(set, new Comparator<Entry>() {
            @Override
            public int compare(Entry e1, Entry e2) {
                double p1 =  e1.poss + FREQUENCY_WEIGHT * Math.log(e1.instruction.frequency + lambda) - LENGTH_WEIGHT * e1.command.length();
                double p2 =  e2.poss + FREQUENCY_WEIGHT * Math.log(e2.instruction.frequency + lambda) - LENGTH_WEIGHT * e2.command.length();
                return - Double.compare(p1, p2);
            }
        });
        //remove duplicate commands
        candidateList.clear();
        Set<Instruction> instructions = new HashSet<>();
        for (Iterator<Entry> iter = set.iterator(); iter.hasNext();) {
            Entry element = iter.next();
            if (instructions.add(element.instruction))
                candidateList.add(element);
        }
        currentIndex = 0;

        //System.out.println("Entry set size" +  "parse: " + set.size());

        int cnt = 0;
        for(Entry e: candidateList){
            System.out.println(e.info());
            if(cnt >= 30)
                break;
            cnt ++;
        }
    }

    public void previous() {
        if (candidateList.size() == 0) return;
        currentIndex = (currentIndex + candidateList.size() - 1) % candidateList.size();
    }
    public void next() {
        if (candidateList.size() == 0) return;
        currentIndex = (currentIndex + 1) % candidateList.size();
    }

    public void previousDiff() {
        if (candidateList.size() == 0) return;
        ParseResult pre = getCurrent();
        previous();
        while (getCurrent().instruction.id.equals(pre.instruction.id)) {
            previous();
        }
        pre = getCurrent();
        while (getCurrent().instruction.id.equals(pre.instruction.id)) {
            previous();
        }
        next();
    }
    public void nextDiff() {
        if (candidateList.size() == 0) return;
        ParseResult pre = getCurrent();
        next();
        while (getCurrent().instruction.id.equals(pre.instruction.id)) {
            next();
        }
    }


}
