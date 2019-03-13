package blindcommand;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "meta"})
public class Instruction {
    public String id;
    public String name;
    public String pinyin;
    public JsonAppInfo meta;
    public int frequency;


    public boolean inSameApp(Instruction ins){
        return this.meta.appName.equals(ins.meta.appName);
    }

    public boolean hasSameCommand(Instruction ins){
        return this.pinyin.equals(ins.pinyin);
    }

    @Override
    public String toString() {
        return id+","+name+","+pinyin+","+frequency+","+meta.toString();
    }
}