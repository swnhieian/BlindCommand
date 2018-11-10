package com.shiweinan.BlindCommand.util;

import com.shiweinan.BlindCommand.touch.TouchPoint;

import java.util.List;

public interface CommandParser {
    // parse current touchPoint info
    List<String> parse();

    //void add(List<TouchPoint> touchPointList);


    void add(TouchPoint touchPoint);


    void setKeyboardInfo(int width, int height);

    // delete info at the tail
    TouchPoint delete();

    void deleteAll();

    // accept the parse result and clear the list
    void accept();




}
