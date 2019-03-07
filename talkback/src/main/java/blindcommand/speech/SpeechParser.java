package blindcommand.speech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import blindcommand.Instruction;
import blindcommand.InstructionSet;
import blindcommand.JsonAppInfo;
import blindcommand.KbdView;
import blindcommand.ParseResult;
import blindcommand.Parser;
import blindcommand.SoundPlayer;
import blindcommand.Utility;
import blindcommand.speech.SpeechCallback;

public class SpeechParser implements Parser, SpeechCallback {
    InstructionSet instructionSet;
    List<Instruction> candidateList;
    int currentIndex = 0;
    SpeechHelper speechHelper;
    HashMap<String, List<Instruction>> nameToIns;
    KbdView user;
    public SpeechParser(KbdView user, InstructionSet instructionSet){
        this.instructionSet = instructionSet;
        this.user = user;
        nameToIns = new HashMap<>();
        Iterator iter = instructionSet.instructions.entrySet().iterator();
        while(iter.hasNext()){
            HashMap.Entry<String, Instruction> entry =  ((Map.Entry<String, Instruction>)iter.next());
            String insStr = entry.getKey();
            Instruction instruction = entry.getValue();
            String[] insArray = insStr.split("\\|");
            if(insArray[2].equals("0")){
                if(!nameToIns.containsKey(instruction.name)){
                    nameToIns.put(instruction.name, new ArrayList<Instruction>());
                }
                nameToIns.get(instruction.name).add(instruction);
            }
        }
        candidateList = new ArrayList<>();

        StringBuilder bnf = new StringBuilder();
        bnf.append("#BNF+IAT 1.0 UTF-8;\n!grammar action;\n!start <command>;\n<command>:");
        boolean flag = true;
        for(String instructionName : nameToIns.keySet()){
            if(flag){
                bnf.append(instructionName);
                flag = false;
            }
            else{
                bnf.append("|");
                bnf.append(instructionName);
            }
        }
        bnf.append(";\n");
        System.out.println(bnf.toString());
        speechHelper = new SpeechHelper(Utility.service, this, bnf.toString());
    }
    public void onResult(List<SpeechResult> result){
        candidateList.clear();
        for(SpeechResult sr : result){
            if(nameToIns.containsKey(sr.result))
                candidateList.addAll(nameToIns.get(sr.result));
            else{
                System.out.println("************ candidate error ***************");
            }
        }
        currentIndex = 0;
        System.out.println("onresult");
        System.out.println("begin");
        for(Instruction candidate: candidateList){
            System.out.println(candidate.name);
        }
        System.out.println("end");
        user.readParseResult(this.getCurrent());
        System.out.println("end of speech");
    }

    public void onBeginOfSpeech(){
        candidateList.clear();
        currentIndex = 0;
    }
    public void onEndOfSpeech(){
    }

    public ParseResult getCurrent() {
        if (candidateList.size() == 0) {
            return new ParseResult(new Instruction("null", "无结果", "WuJieGuo", new JsonAppInfo()),
                    -1, 0, false);
        }
        ParseResult pr = new ParseResult(candidateList.get(currentIndex), currentIndex, candidateList.size(), false);
        for(Instruction candidate: candidateList){
            if(candidate.hasSameCommand(pr.instruction) && !candidate.inSameApp(pr.instruction)){
                pr.hasSameName = true;
                break;
            }
        }
        return pr;
    }
    public void next() {
        if (candidateList.size() == 0) return;
            currentIndex = (currentIndex + 1) % candidateList.size();
    }
    public void previous() {
        if (candidateList.size() == 0) return;
            currentIndex = (currentIndex + candidateList.size() - 1) % candidateList.size();
    }
    public void nextDiff() {
        next();
    }
    public void previousDiff() {
        previous();
    }
    public void addTouchPoint(long time, float x, float y) {

    }
    public void clear() {
        candidateList.clear();
        currentIndex = 0;
    }

    public void startRecognizing() {
        //SoundPlayer.tts("开始识别");
        Utility.vibrate();
        speechHelper.startRecognizing();
    }

    public void stopRecognizing() {
        speechHelper.stopRecognizing();
    }

}
