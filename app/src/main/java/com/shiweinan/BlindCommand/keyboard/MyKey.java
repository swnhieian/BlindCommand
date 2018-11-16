package com.shiweinan.BlindCommand.keyboard;

import com.shiweinan.BlindCommand.touch.TouchPoint;

import com.shiweinan.BlindCommand.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Locale;

@Data
@AllArgsConstructor
public class MyKey {
    private char c;
    private double width;
    private double height;
    private double left;
    private double right;
    private double up;
    private double bottom;
    private double cx; // center x
    private double cy; // center y

    private Vector2 center;

    /*
      ---- up
      |  |
 left |  | right
      ---- bottom
  o - > x
  |
  L y
    */

    public MyKey(char c, double left, double up, double width, double height){
        this(c, width, height, left, left + width, up, up + height, left + width * 0.5, up + height * 0.5,
                new Vector2(left + width * 0.5, up + height * 0.5));
    }
    public String info(){
        return String.format(Locale.ENGLISH,"Key: %c, left-up: (%f, %f), right-bottom: (%f, %f)", c, left, up, right, bottom);
    }


    public String center(){
        return String.format(Locale.ENGLISH,"Key: %c, center: (%f, %f)", c, cx, cy);
    }

    public String allInfo(){
        return String.format(Locale.ENGLISH,"Key: %c, left-up: (%f, %f), right-bottom: (%f, %f), center: (%f, %f)", c, left, up, right, bottom, cx, cy);
    }
    public boolean contains(TouchPoint tp){
        return tp.getX() >= left && tp.getX() < right && tp.getY() >= up && tp.getY() < bottom;
    }

}
