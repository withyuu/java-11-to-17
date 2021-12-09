package withyuu.tutorial.java11to17.switchexpr;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SwitchExprTest {

  @Test
  void traditionalSwitchWithoutBreak() {
    Color color = Color.ULTRAMARINE;
    String result;
    switch (color) {
      case CRIMSON, VERMILION:
        result = "Shades of Red";
      case HONEYDEW, EMERALD:
        result = "Shades of Green";
      case ULTRAMARINE, NAVY_BLUE:
        result = "Shades of Blue";
      default:
        result = "Undefined Shade";
    }
    assertThat(result).isEqualTo("Undefined Shade");
  }

  @Test
  void traditionalSwitchWithBreak() {
    Color color = Color.ULTRAMARINE;
    String result;
    switch (color) {
      case CRIMSON, VERMILION:
        result = "Shades of Red";
        break;
      case HONEYDEW, EMERALD:
        result = "Shades of Green";
        break;
      case ULTRAMARINE, NAVY_BLUE:
        result = "Shades of Blue";
        break;
      default:
        result = "Undefined Shade";
    }
    assertThat(result).isEqualTo("Shades of Blue");
  }

  // The scope of a variable inside the switch blocks expand through all the cases inside the same block
  @Test
  void scopeProblemOfTraditionalSwitch() {
    Color color = Color.ULTRAMARINE;
    String result;
    switch (color) {
      case CRIMSON, VERMILION:
        int temp = 0;
        result = "Shades of Red";
        break;
      case HONEYDEW, EMERALD:
        // Cannot define `temp` here because it is already defined in this block scope
        // int temp = 1;
        result = "Shades of Green";
        break;
      case ULTRAMARINE, NAVY_BLUE:
        // Cannot define `temp` here because it is already defined in this block scope
        // int temp = 2;
        result = "Shades of Blue";
        break;
      default:
        // Cannot define `temp` here because it is already defined in this block scope
        // int temp = 2;
        result = "Undefined Shade";
    }
  }

  @Test
  void switchWithArrowLabel() {
    Color color = Color.ULTRAMARINE;
    String result;
    switch (color) {
      case CRIMSON, VERMILION -> result = "Shades of Red";
      case HONEYDEW, EMERALD -> result = "Shades of Green";
      case ULTRAMARINE, NAVY_BLUE -> result = "Shades of Blue";
      default -> result = "Undefined Shade";
    }
    assertThat(result).isEqualTo("Shades of Blue");
  }

  @Test
  void switchExpression() {
    Color color = Color.ULTRAMARINE;
    String result =
        switch (color) {
          case CRIMSON, VERMILION -> "Shades of Red";
          case HONEYDEW, EMERALD -> "Shades of Green";
          case ULTRAMARINE, NAVY_BLUE -> "Shades of Blue";
          default -> "Undefined Shade";
        };
    assertThat(result).isEqualTo("Shades of Blue");
  }

  @Test
  void switchExpressionWithFullBlockAndYieldValue() {
    Color color = Color.EBONY;
    String result =
        switch (color) {
          case CRIMSON, VERMILION -> "Shades of Red";
          case HONEYDEW, EMERALD -> "Shades of Green";
          case ULTRAMARINE, NAVY_BLUE -> "Shades of Blue";
          default -> {
            System.out.println("Unhandled Color: " + color);
            yield "Undefined Shade";
          }
        };
    assertThat(result).isEqualTo("Undefined Shade");
  }

  @Test
  void switchExpressionWithTraditionalSwitchBlock() {
    Color color = Color.ULTRAMARINE;
    String result =
        switch (color) {
          case CRIMSON, VERMILION:
            yield "Shades of Red";
          case HONEYDEW, EMERALD:
            yield "Shades of Green";
          case ULTRAMARINE, NAVY_BLUE:
            yield "Shades of Blue";
          default:
            System.out.println("Unhandled Color: " + color);
            yield "Undefined Shade";
        };
    assertThat(result).isEqualTo("Shades of Blue");
  }

  // COMPILATION ERROR:
  // the switch expression does not cover all possible input values
//  @Test
//  void switchRulesMustCoverAllPossibleValues() {
//    Color color = Color.ULTRAMARINE;
//    String result =
//        switch (color) {
//          case CRIMSON, VERMILION -> "Shades of Red";
//          case HONEYDEW, EMERALD -> "Shades of Green";
//          case ULTRAMARINE, NAVY_BLUE -> "Shades of Blue";
//        };
//    assertThat(result).isEqualTo("Shades of Blue");
//  }

  // COMPILATION ERROR:
  // switch rules in switch expressions must either provide a value or throw
//  @Test
//  void switchRulesMustYieldAValueOrThrow() {
//    Color color = Color.EBONY;
//    String result =
//        switch (color) {
//          case CRIMSON, VERMILION -> "Shades of Red";
//          case HONEYDEW, EMERALD -> "Shades of Green";
//          case ULTRAMARINE, NAVY_BLUE -> "Shades of Blue";
//          default -> {
//            System.out.println("Unhandled Color: " + color);
//          }
//        };
//    assertThat(result).isEqualTo("Undefined Shade");
//  }

}
