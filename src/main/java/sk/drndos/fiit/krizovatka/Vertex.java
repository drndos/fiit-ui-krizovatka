/**
 * Vertex.java Filip Bednárik <drndos@drndos.sk>
 *
 * @ 1.4.2013 21:07
 */
package sk.drndos.fiit.krizovatka;

import java.util.HashMap;
import lombok.Data;

/**
 * Vertex which contains current configuration of cars
 *
 * @author DeathX
 */
@Data
public class Vertex {

  private Vertex lastVertex = null;
  private HashMap<String, Car> value;
  private String move = null;

  /**
   * Default constructor which adds HashMap of all cars to this object
   */
  public Vertex(HashMap<String, Car> value) {
    this.value = value;
  }

  /**
   *
   */
  public Vertex() {
  }
}
