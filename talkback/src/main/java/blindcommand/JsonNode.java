package blindcommand;

import java.util.List;

public class JsonNode {
    public String pageId;
    public String pageName;
    public String pagePinyin;
    public boolean canDirectReach = true;
    public List<JsonNodePath> features;
    public List<JsonClickable> buttons;
    public int frequency;
}
