package com.shiweinan.BlindCommand.util;

import com.shiweinan.BlindCommand.touch.TouchPoint;

import java.util.List;

public interface CommandParser {
    // parse current touchPoint info
    String parse();

    void add(List<TouchPoint> touchPointList);

    void add(TouchPoint... touchPoints);

    void add(TouchPoint touchPoint);

    void setKeyboardLayout();

    // delete info at the tail
    void delete();

    // accept the parse result and clear the list
    void accept();


}
