package blindcommand;
import android.text.method.Touch;
import android.view.MotionEvent;

import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TouchPoint {

    int keyNumber;
    double x;
    double y;
    double rawX;
    double rawY;
    Vector2 position;

    public TouchPoint(int keyNumber, MotionEvent event) {
        this(keyNumber, event.getX(), event.getY(), event.getRawX(), event.getRawY());
    }
    public TouchPoint(int keyNumber, double x, double y, double rawX, double rawY){
        this(keyNumber, x, y, rawX, rawY, new Vector2(x, y));
    }
    public String allInfo() {
        return String.format(Locale.ENGLISH,"(x, y): (%f, %f), (rawX, rawY): (%f, %f)", x,y,rawX,rawY);
    }
    public String info() {
        return String.format(Locale.ENGLISH,"(x, y): (%f, %f)", x, y);
    }
}