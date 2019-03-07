package blindcommand;

public interface Parser {
    enum ParserType {
        DEFAULT, NO_DICT, LIST, SPEECH
    }
    ParseResult getCurrent();
    void next();
    void previous();
    void nextDiff();
    void previousDiff();
    void addTouchPoint(long time, float x, float y);
    void clear();
}
