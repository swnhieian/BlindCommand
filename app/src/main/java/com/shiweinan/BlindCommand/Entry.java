package com.shiweinan.BlindCommand;

import java.util.Locale;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Entry {
    String instruction;
    double poss;

    public String info(){
        return String.format(Locale.ENGLISH,"(%s, %f)", instruction, poss);
    }
}
