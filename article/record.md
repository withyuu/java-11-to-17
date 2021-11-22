# Feature ใหม่ใน Java 17: #2 Records

Record เป็น class ประเภทใหม่ที่แน่ใจว่าหลายๆ คนรอคอยให้ Java มีแบบนี้สักที[^1]

syntax การประกาศ Record ก็เหมือน class ทั่วไปเพียงแต่เปลี่ยนคำว่า `class` เป็น `record` แทน และต่อท้ายในวงเล็บด้วย component สำหรับ Record นั้น
```
public record Rectangle(int width, int height) { }
```
แค่เห็นก็ว้าวแล้วใช่มั้ยครับ<br>
เย่! ในที่สุด Java ก็มี data class เหมือน Kotlin แล้ว!! - **ผิด**<br>
เย่! ในที่สุดก็ไม่ต้องใช้ Lombok แล้ว!! - **ผิด**

ถึงแม้หน้าตาจะคล้ายกับ data class ของ Kotlin และการเอาไปใช้งานบางจุดก็ใกล้เคียงกันมาก แต่ก็ไม่ได้เหมือนกันซะทีเดียว เพราะฉะนั้นเราไปดูรายละเอียดกันก่อนดีกว่า

## ที่มาของปัญหา
แรงจูงใจที่ทำให้เกิด Record class ขึ้นมาคือประโยคที่เราเจอคนบ่นถึง Java กันเป็นประจำ "Java is too verbose" หรือ Java ใช้คำฟุ่มเฟือย เกินไปนั่นเอง
โดยเฉพาะ class ที่ทำหน้าที่เป็นแค่ตัวส่งข้อมูลที่ไม่ได้มีการแก้ไขข้อมูลใดๆ (immutable data carrier) ซึ่งเวลาที่เราจะสร้าง class แบบที่ว่าขึ้นมามีสิ่งที่เราต้องทำเยอะมาก
- ประกาศ class
- ประกาศ private final field
- ประกาศ constructor ที่รับค่ามาใส่ field
- ประกาศ getter method
- สร้าง method equals, hashCode
- และอาจจะสร้าง method toString ด้วยเพื่อความสะดวก

บางคนอาจบอกว่าของแค่นี้ใช้ IDE generate ออกมาให้ก็ได้ วิธีนี้สะดวกคนเขียนแต่คนที่มาอ่านโค้ดเราเห็นแบบนี้ก็ขี้เกียจอ่านอยู่ดี Java ก็เลยสร้าง Record ขึ้นมาเพื่อเป็น immutable data carrier โดยเฉพาะ

## การทำงานของ Record
อย่างที่บอกไว้ว่าเป้าหมายของ Record คือการสร้าง class ที่เป็น immutable data carrier เพราะฉะนั้นจากตัวอย่างของ Record ด้านบนจะเทียบเท่าได้กับ class ดังนี้[^2]
```
public record Rectangle(int width, int height) {

  private final int width;
  private final int height;

  public Rectangle(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public int width() {
    return this.width;
  }

  public int height() {
    return this.height;
  }

  public boolean equals(Object o) {
    if (!(o instanceof Rectangle)) return false;
    Rectangle other = (Rectangle) o;
    return other.width == width && other.height == height;
  }

  public int hashCode() {
    return Objects.hash(width, height);
  }

  public String toString() {
    return String.format("Rectangle[width=%s, y=%s]", width, height);
  }
}
```
สิ่งที่ Record ทำให้เราโดยอัตโนมัติเลย ได้แก่
- private final field ซึ่งชื่อและ type เป็นตามที่เราระบุไว้ใน Record header
- Record Class จะไม่มี default constructor แต่จะสร้าง constructor ที่รับ parameter ตามที่เราระบุไว้ใน Record Header
และทำการ assign ค่าให้กับ field ในข้อแรก
- getter method **ที่ชื่อไม่ได้ขึ้นต้นด้วย** `get` **!!**
- method equals(), hashCode(), และ toString()

ถือว่า Record ช่วยให้เราประหยัดเวลาไปได้พอสมควรทีเดียวสำหรับการสร้าง immutable data class

## Local Record Class
บ่อยครั้งที่เราทำงานกับ stream แล้วอยากให้ `map()` return ค่าออกมามากกว่า 1 ตัว ซึ่งถ้าเป็นในอดีตเราก็จะมอบหน้าที่นี้ให้กับ Tuple class
แต่ตอนนี้เรามีอีกหนึ่งทางเลือกนั่นก็คือการใช้ Local Record Class

เราสามารถประกาศ Record class ภายใน method แล้วใช้งานในนั้นได้เลยดังตัวอย่างด้านล่าง

```
private List<String> findGradeAStudent() {
  // local record
  record StudentGrade(StudentScore result, String grade) {}

  return getExamResults() // getExamReults returns a List of StudentScore
      .stream()
      .map(ss -> new StudentGrade(ss, calculateGrade(ss.score())))
      .filter(g -> g.grade().equals("A"))
      .map(StudentGrade::result)
      .map(StudentScore::name)
      .collect(Collectors.toList());
  }
```

## การ Customize Record Class
ถ้าเราต้องการเพิ่ม logic หรือแก้ไข default behavior ที่ Record class มีให้ เราก็สามารถทำได้เช่นเดียวกับ class ปกติทั่วไป

### Constructor
เราสามารถใส่ validation หรือดัดแปลงค่า parameter ใน constructor ได้ โดย Record สามารถเขียน constructor ในรูปแบบ `compact canonical constructor` ได้ด้วย
```
record DoubleSizeRectangle(int width, int height) {

  DoubleSizeRectangle {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Invalid width or height");
    }
    width *= 2;
    height *= 2;
  }

}
```
จะมีค่าเท่ากับ
```
record DoubleSizeRectangle(int width, int height) {

  DoubleSizeRectangle(int width, int height) {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Invalid width or height");
    }
    width *= 2;
    height *= 2;

    // Initialization
    this.width = width;
    this.height = height;
  }

}
```
### Accessor methods, equals, hashCode, toString, and other methods
เราสามารถประกาศ accessors, equals, hashCode, และ toString เองเพื่อ customize behavior ของ method เหล่านี้ได้เหมือน class ทั่วๆ ไป
สิ่งที่ควรระวังเช่นเรื่อง [contract ระหว่าง equals กับ hashCode](https://www.baeldung.com/java-equals-hashcode-contracts) ก็ยังเหมือนเดิม 
หรือถ้าจะสร้าง instance method / class (static) method อื่นๆ ขึ้นมาก็ได้เหมือนกัน

### Implement Interface
ถึงแม้ว่า Record จะไม่สามารถ extend class อื่นได้ (ดูคำอธิบายด้านล่าง) แต่สามารถ implement interface ได้ตามปกติ เช่น เราอาาจจะอยากให้
Record ของเรา implement Comparable interface ได้เป็นต้น

### Annotation
การใช้ Annotation กับ Record จะค่อนข้าง tricky นิดนึง เพราะ component ใน Record header อาจจะหมายถึง field, constructor parameter, หรือ accessor method ก็ได้

Annotation ที่วางไว้บน component จะไปโผล่ที่ไหนนั้นขึ้นอยู่กับ @Target ของตัว Annotation เช่นถ้าเป็น @Target(ElementType.FIELD) ตัว Annotation
ก็จะไปโผล่บน private field ของ Record นั้น ตัวอย่างเช่น
```
@OnType
public record TestSubject(
    @OnParameter int p1,
    @OnMethod int p2,
    @OnField int p3,
    @OnFieldAndParameter int p4) {
}
```
จะมีค่าเท่ากับ
```
@OnType
public record TestSubject(int p1, int p2, int p3, int p4) {

  ...
  @OnField private final int p3
  ...

  public TestSubject(
        @OnParameter int p1,
        int p2,
        int p3,
        @OnFieldAndParameter int p4) {
    ...
  }

  ...

  @OnMethod
  public int p2() {
    return this.p2;
  }

  ...

}
```

## สิ่งที่ Record Class ทำไม่ได้
ด้วยความที่ Record ถูกออกแบบมาให้ immutable และ state ของ Record instance ขึ้นอยู่กับ component ใน Record header เท่านั้น เพราะฉะนั้นอะไรก็ตามที่ขัดกับความตั้งใจนี้จะไม่สามารถทำได้
- Record Class ไม่สามารถ extend class อื่นได้
- Record Class ถือเป็น final class และห้ามเป็น abstract
- ไม่สามารถมี instance field อื่นๆ ใน Record Class ได้เพราะจะทำให้ state ของ instance ไม่ขึ้นอยู่กับ Record component เพียงอย่างเดียว
- ไม่สามารถมี native method ได้

## สรุป
Record class ช่วยเพิ่มความสะดวกในการสร้าง immutable data class และยังสามารถใช้แทน Tuple ภายใน method ได้โดยการสร้าง Local Record Class

ถึงแม้จะยังเทียบกับ data class ของ Kotlin และ @Data ของ Lombok ที่สามารถสร้าง mutable data class ได้ด้วยไม่ได้ แต่ก็ถือว่าเป็น feature ที่ใครที่ upgrade เป็น Java 17 แล้วน่าเอามาใช้ครับ

สำหรับใครที่อยากดูการใช้งานของ Record class เพิ่มเติมตามไปดูได้ [ที่นี่](https://github.com/withyuu/java-11-to-17)

[^1]: feature นี้ release ใน Java 16<br>
[^2]: เป็นแค่ตัวอย่างสำหรับเปรียบเทียบนะครับ implementation จริงๆ ขึ้นอยู่กับ VM แต่ละตัวซึ่งอาจต่างจากที่เขียน