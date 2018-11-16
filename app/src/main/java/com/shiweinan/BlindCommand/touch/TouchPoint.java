package com.shiweinan.BlindCommand.touch;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Locale;

@Data
@AllArgsConstructor
public class TouchPoint {

    int keyNumber;
    double x;
    double y;
    double rawX;
    double rawY;

    public String allInfo() {
        return String.format(Locale.ENGLISH,"(x, y): (%f, %f), (rawX, rawY): (%f, %f)", x,y,rawX,rawY);
    }
    public String info() {
        return String.format(Locale.ENGLISH,"(x, y): (%f, %f)", x, y);
    }
}
