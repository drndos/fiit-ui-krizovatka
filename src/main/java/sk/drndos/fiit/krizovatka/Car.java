/**
 * Car.java Filip Bednárik <drndos@drndos.sk>
 *
 * @ 1.4.2013 21:07
 */
package sk.drndos.fiit.krizovatka;

import java.awt.Color;
import lombok.Data;

/**
 * Car class
 *
 * @author DeathX
 */
@Data
public class Car {

  private final Color2 color;
  private final boolean vertical;
  private int x;
  private int y;
  private final int size;

  Car(String color, boolean vertical, int x, int y, int size) {
    this.color = Color2.valueOf(color);
    this.vertical = vertical;
    this.x = x;
    this.y = y;
    this.size = size;
  }

  Car(Color color, boolean vertical, int x, int y, int size) {
    this.color = new Color2(color);
    this.vertical = vertical;
    this.x = x;
    this.y = y;
    this.size = size;
  }

  Car(Car a) {
    this.color = a.getColor();
    this.vertical = a.isVertical();
    this.x = a.getX();
    this.y = a.getY();
    this.size = a.getSize();
  }

  @Override
  public String toString() {
    return "(" + color.name() + " " + size + " " + y + " " + x + " " + ((vertical) ? "v" : "h") + ")";

  }

  boolean isBlocking(int x1, int y1) {
    if (vertical) {
      if (x == x1) {
        return ((y + size - 1) >= y1) && ((y) <= y1);
      }
    } else {
      if (y == y1) {
        return ((x + size - 1) >= x1) && ((x) <= x1);
      }
    }
    return false;
  }

}
