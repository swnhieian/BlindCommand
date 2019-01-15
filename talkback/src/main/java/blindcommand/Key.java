package blindcommand;

import android.graphics.RectF;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Key {
    char name;
    float x;
    float y;
    float width;
    float height;
    public RectF getRect() {
        return new RectF(x - width /2 , y - height / 2, x + width / 2, y + height /2);
    }
    public boolean contains(float x, float y) {
        return (x < this.x+width/2) && (x>this.x-width/2) && (y<this.y+height/2) && (y>this.y-height/2);
    }
}
