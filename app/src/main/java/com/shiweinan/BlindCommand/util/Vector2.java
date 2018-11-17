package com.shiweinan.BlindCommand.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Vector2 {
    public double x;
    public double y;
    public static double sqrDistance(Vector2 v1, Vector2 v2){
        double dx = v1.x - v2.x;
        double dy = v1.y - v2.y;
        return dx * dx + dy * dy;
    }
    public static double distance(Vector2 v1, Vector2 v2){
        double dx = v1.x - v2.x;
        double dy = v1.y - v2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    public static Vector2 add(Vector2 v1, Vector2 v2){
        return new Vector2(v1.x + v2.x, v1.y + v2.y);
    }

    public static Vector2 sub(Vector2 v1, Vector2 v2){
        return new Vector2(v1.x - v2.x, v1.y - v2.y);
    }


}
