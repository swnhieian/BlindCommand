package com.shiweinan.BlindCommand.touch;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TouchPoint {

    int keyNumber;
    float x;
    float y;
    float rawX;
    float rawY;


    public String info() {
        return String.format("Key: %d, (x, y): (%f, %f), (rawX, rawY): (%f, %f)", keyNumber, x,y,rawX,rawY);
    }
}
