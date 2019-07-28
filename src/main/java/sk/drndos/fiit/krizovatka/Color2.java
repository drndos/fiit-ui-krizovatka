/**
 * Color2.java Filip Bednárik <drndos@drndos.sk>
 *
 * @ 1.4.2013 21:07
 */
package sk.drndos.fiit.krizovatka;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * This is special class which replaces need of enum and provides anonymous values
 *
 * @author DeathX
 */
public class Color2 {

  private static final HashMap<String, Color> farby = new HashMap<>();

  static {
    farby.put("cervene", Color.RED);
    farby.put("oranzove", Color.ORANGE);
    farby.put("zlte", Color.YELLOW);
    farby.put("fialove", Color.MAGENTA);
    farby.put("zelene", Color.GREEN);
    farby.put("svetlomodre", Color.CYAN);
    farby.put("sive", Color.LIGHT_GRAY);
    farby.put("ruzove", Color.PINK);
    farby.put("cierne", Color.BLACK);
    farby.put("tmavosive", Color.DARK_GRAY);
    farby.put("tmavomodre", Color.BLUE);
  }

  private Color num;

  /**
   * Default constructor for known color
   */
  public Color2(String name) {
    this.name = name;
    this.num = farby.get(name);
  }

  /**
   * Constructor for unknown color
   */
  public Color2(Color num) {
    this.name = num.toString();
    this.num = num;
    farby.put(name, num);
  }

  private String name;

  /**
   * Legacy from enum type
   */
  static Color2 valueOf(String name) {
    return new Color2(name);
  }

  /**
   * Returns real value
   */
  Color getNum() {
    return num;
  }

  /**
   * Returns name of the value
   */
  public String name() {
    return name;
  }

  /**
   * Returns all Values in Array
   */
  static Color2[] values() {
    ArrayList<Color2> c2 = new ArrayList<>();
    farby.keySet().stream().map(Color2::valueOf).toArray();
    for (String farba : farby.keySet()) {
      c2.add(Color2.valueOf(farba));
    }
    return c2.toArray(new Color2[0]);
    //return (Color2[]) farby.keySet().stream().map(Color2::valueOf).toArray(new Color2[0]);
  }

  @Override
  public String toString() {
    return this.name();
  }
}
