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
        return String.format(Locale.ENGLISH,"(%s, %s, %f, %d, %s)", command, instruction.name, poss, instruction.frequency, instruction.meta.packageName);
    }
}
