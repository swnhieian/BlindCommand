package blindcommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoDictParser implements Parser {
    List<Key> allKeys;
    String candidate = "";

    public NoDictParser(List<Key> keys) {
        allKeys = keys;
    }

    @Override
    public void addTouchPoint(long time, float x, float y) {
        for (Key k: allKeys) {
            if (k.contains(x, y)) {
                candidate += k.name;
            }
        }
    }

    @Override
    public ParseResult getCurrent() {
        return new ParseResult(new Instruction(candidate, candidate, candidate, new JsonAppInfo()), -1, 0, false);
    }

    @Override
    public void next() {
        return;
    }

    @Override
    public void previous() {
        return;
    }
    public void nextDiff() {
        next();
    }
    public void previousDiff() {
        previous();
    }

    @Override
    public void clear() {
        candidate = "";
    }
}
