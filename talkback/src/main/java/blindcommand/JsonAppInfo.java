package blindcommand;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"appName"})
public class JsonAppInfo {
    String packageName;
    String appName;
    String appPinyin;
}
