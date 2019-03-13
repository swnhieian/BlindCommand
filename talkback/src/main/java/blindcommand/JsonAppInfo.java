package blindcommand;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"appName"})
public class JsonAppInfo {
    public String packageName = "";
    public String appName = "";
    public String appPinyin = "";
    public Integer[] resolution;

    @Override
    public String toString() {
        return appName;
    }
}
