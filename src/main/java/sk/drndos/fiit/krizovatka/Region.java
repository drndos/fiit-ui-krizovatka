/**
 * Region.java Filip Bednárik <drndos@drndos.sk>
 *
 * @ 1.4.2013 21:07
 */
package sk.drndos.fiit.krizovatka;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Region inside picture represents object of a car
 *
 * @author DeathX
 */
@Data
@AllArgsConstructor
public class Region {

  private final int x1;
  private final int y1;
  private final int x2;
  private final int y2;

  @Override
  public String toString() {
    return "x1:" + x1 + " y1:" + y1 + " x2:" + x2 + " y2:" + y2;
  }

}
