package withyuu.tutorial.java11to17.patternmatchinginstanceof;

import org.junit.jupiter.api.Test;
import withyuu.tutorial.java11to17.patternmatchinginstanceof.Car;
import withyuu.tutorial.java11to17.patternmatchinginstanceof.Vehicle;

public class PatternMatchingForInstanceOfTest {

  @Test
  void basicSyntax() {
    Vehicle obj = getUnknownVehicle();
    if (obj instanceof Car c) {
      c.drift();
    }
  }

  @Test
  void basicScope() {
    Vehicle obj = getUnknownVehicle();
    if (obj instanceof Car c) {
      c.drift();
    }
    // c.drift(); // Cannot refer to c here (compile error)
  }

  @Test
  void invertScope() {
    Vehicle obj = getUnknownVehicle();
    if (!(obj instanceof Car c)) {
      // c.drift(); // Cannot refer to c here (compile error)
      throw new RuntimeException();
    }
    c.drift(); // Can refer to c here since the compiler knows c is a Car
  }

  @Test
  void expressionMatch() {
    Object obj = getUnknownObject();
    if (obj instanceof String s && s.length() > 0) {
      System.out.println(s);
    }
  }

//  @Test
//  void expressionNotMatch() {
//    Object obj = getUnknownObject();
//    if (obj instanceof String s || s.length() > 0) { // Cannot resolve symbol 's'
//      System.out.println(s);
//    }
//  }

  private Vehicle getUnknownVehicle() {
    return new Car();
  }

  private Object getUnknownObject() {
    return "some string";
  }

}
