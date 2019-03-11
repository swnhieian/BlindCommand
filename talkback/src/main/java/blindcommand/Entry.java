package blindcommand;

import java.util.Locale;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Entry {
    String command;
    Instruction instruction;
    double poss;
    boolean appFirst;

    public String info(){
        return String.format(Locale.ENGLISH,"(%s, %s, %f)", command, instruction.pinyin, poss);
    }
}
