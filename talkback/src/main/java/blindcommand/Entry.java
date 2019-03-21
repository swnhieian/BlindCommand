package blindcommand;

import java.util.Locale;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Entry {
    String command;
    Instruction instruction;
    double poss;
    boolean appFirst;
    public Entry(Instruction instruction, double poss) {
        this.instruction = instruction;
        this.poss = poss;
    }

    public String info(){
        return String.format(Locale.ENGLISH,"(%s, %s, %f, %f, %s)", command, instruction.name, poss, instruction.frequency, instruction.meta.packageName);
    }
}
