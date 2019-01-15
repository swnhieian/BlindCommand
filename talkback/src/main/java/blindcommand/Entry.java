package blindcommand;

import java.util.Locale;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Entry {
    String command;
    Instruction instruction;
    double poss;

    public String info(){
        return String.format(Locale.ENGLISH,"(%s, %f)", instruction, poss);
    }
}
