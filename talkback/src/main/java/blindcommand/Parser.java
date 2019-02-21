package blindcommand;

public interface Parser {
    enum ParserType {
        DEFAULT, NO_DICT, LIST
    };
    ParseResult getCurrent();
    void next();
    void previous();
    void addTouchPoint(long time, float x, float y);
    void clear();
}
