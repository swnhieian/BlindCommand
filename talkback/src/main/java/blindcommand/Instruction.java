package blindcommand;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode(of = {"pinyin", "meta"})
public class Instruction {
    String id;
    String name;
    String pinyin;
    JsonAppInfo meta;


    public boolean inSameApp(Instruction ins){
        return this.meta.appName.equals(ins.meta.appName);
    }

    public boolean hasSameCommand(Instruction ins){
        return this.pinyin.equals(ins.pinyin);
    }
}