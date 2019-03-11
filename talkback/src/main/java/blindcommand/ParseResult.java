package blindcommand;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ParseResult {
    public Instruction instruction;
    public int index;
    public int size;
    public boolean hasSameName;
    boolean readAppFirst;
}
