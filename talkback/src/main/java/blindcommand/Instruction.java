package blindcommand;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Instruction {
    String id;
    String name;
    String pinyin;
    JsonAppInfo meta;
}