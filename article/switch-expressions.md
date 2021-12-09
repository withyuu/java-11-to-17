# Feature ใหม่ใน Java 17: #5 Switch Expressions

เชื่อว่าทุกคนคงเคยเขียน switch statement กันมาอยู่แล้ว เพราะฉะนั้นก็คงคุ้นเคยกับเหตุการณ์นี้กันดี
```java
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
```
พอเขียนเสร็จ ลองรันดูก็จะงงว่าทำไม `result` ถึงออกมาเป็น `"Undefined Shade"` ???

สักพักก็จะนึกขึ้นได้ เฮ้ย! switch case มัน fall through! ลืมใส่ `break;`! (ใครสนใจเรื่อง fall through เชิง**ลึก** ขอเชิญ[ทางนี้](https://medium.com/swlh/understanding-switch-case-fall-through-in-java-70b448427b0a)) 

ทางทีมงาน Java เองก็ไม่ได้นิ่งดูดาย ใน Java 17[^1] เลยมีการเพิ่ม feature ให้กับ switch หลักๆ 2 เรื่อง

## 1. Arrow Labels
จากการเขียน switch แบบเดิม `case L :` เราสามารถเขียนเป็น `case L ->` แทนได้ ซึ่งถ้าใช้เป็น Arrow Label แทนแล้วจะไม่มีการ fall through !!
```java
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
```

## 2. Switch Expressions
เราสามารถใช้ switch เป็น expression ได้แล้ว หรือถ้าให้พูดง่ายๆ ก็คือเราได้ return value จากการเรียกใช้ switch โดยค่าที่
return ออกมาก็คือค่าทางด้านขวาของแต่ละ `case` นั่นเอง จากตัวอย่างด้านบนเราสามารถเขียนใหม่ได้เป็น
```java
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
```

### Keyword `yield`
ในกรณีที่ด้านขวาของ `case L ->` มีมากกว่า 1 คำสั่ง เราสามารถเขียนเป็น block แล้วคืนค่าจาก block นั้นโดยใช้ keyword `yield`
```java
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
```

นอกจากนี้เรายังสามารถใช้ switch expression กับการเขียน switch แบบเก่าคู่กับ `yield` ได้ด้วย
```java
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
```

## ข้อควรระวังในการใช้ Switch Expressions
พอเราใช้ switch เป็น expression แล้วเราต้องมั่นใจว่าเราจะได้ return value จาก switch (หรือไม่ก็ throw Exception) ซึ่งอันที่จริงมันก็ตรงไปตรงมา
เหมือนเวลาเราเขียน method ที่มีการ return value นั่นแล

```java
// COMPILATION ERROR:
// the switch expression does not cover all possible input values
@Test
void switchRulesMustCoverAllPossibleValues() {
  Color color = Color.ULTRAMARINE;
  String result =
      switch (color) {
        case CRIMSON, VERMILION -> "Shades of Red";
        case HONEYDEW, EMERALD -> "Shades of Green";
        case ULTRAMARINE, NAVY_BLUE -> "Shades of Blue";
      };
  assertThat(result).isEqualTo("Shades of Blue");
}
```
ตัวอย่างด้านบนนี้จะ **Compile ไม่ผ่าน** เพราะอาจมีกรณีที่ค่าของ `color` ไม่ตรงกับค่าที่มีในแต่ละ `case` เลยซึ่งจะทำให้ไม่มีอะไร
return ออกไปจาก switch expression นี้

ในทางปฏิบัติเราจึงควรห้อย default case ไว้เสมอเวลาใช้ switch expression ซึ่งเราจะ return default value อะไรไป
หรือจะ throw Exception ก็แล้วแต่

## สรุป
ใน Java 17 มี feature ใหม่เกี่ยวกับ switch ให้เราใช้ 2 อย่าง นั่นคือ
1. ใช้ Arrow Label แทน เพื่อป้องกันการ fall through
2. ใช้ switch เป็น expressions เพื่อเอา result จาก switch ได้ โดยอาจต้องใช้ keyword `yield` ช่วยในการ return ค่า

สำหรับใครที่ยังดูตัวอย่างในบทความไม่จุใจ ตามไปดูต่อได้ [ที่นี่](https://github.com/withyuu/java-11-to-17) คร้าบ

[^1]: Switch Expressions release ตั้งแต่ Java version 14