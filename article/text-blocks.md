# Feature ใหม่ใน Java 17: #6 Text Blocks

![Text Blocks](image/text-blocks.jpg)

Text Block เป็น literal แบบใหม่ที่ช่วยให้การเขียนข้อความที่มีหลายบรรทัดและต้องมีการจัด format ง่ายขึ้น

หลายๆ ครั้งเราจำเป็นต้องเขียน code snippet เช่น XML, JSON, HTML เป็น String literal ใน Java
ซึ่งเวลาเขียนทีก็ค่อนข้างลำบาก เช่น
```java
String html = "<html>\n" +
              "    <body>\n" +
              "        <p>Don't think about elephants</p>\n" +
              "    </body>\n" +
              "</html>\n";
```
เราต้องมาคอยใส่ `\n` และ `+` ทุกบรรทัด แต่ใน Java 17[^1] เราสามารถเขียนโค้ดด้านบนด้วย Text Block
ได้ดังนี้
```java
String html = """
    <html>
        <body>
            <p>Don't think about elephants</p>
        </body>
    </html>
    """;
```
ชีวิตดีขึ้นมากเลยทีเดียว

การใช้งานทั่วไปของ Text Block อันที่จริงก็มีเท่านี้แหละ แต่ถ้าใครต้องการความเป๊ะและรายละเอียดยิบย่อยของ Text Block
เราไปดูกันต่อเลย

*Note: Text Block ของ Java ทำงานต่างกับ Raw String ของ Kotlin นะครับ เวลาสลับไปมา
ระหว่าง 2 ภาษานี้ต้องระวังนิดนึง*

## New Line ในบรรทัดสุดท้าย

ถ้าสังเกตตัวอย่างด้านบนดีๆ จะเห็นว่า Text Block ด้านบนเมื่อแปลงเป็น String literal แล้ว ที่บรรทัดสุดท้ายจะมี `\n` ติดมาตรง
`</html>\n`

นั่นเป็นเพราะ closing delimiter `"""` ของเราอยู่คนละบรรทัดกับ `</html>` ทำให้มี new line character ติดมานั่นเอง
ถ้าไม่อยากให้มีติดมาเราก็แค่ขยับ `"""` ไปอยู่ท้ายบรรทัดสุดท้าย หรืออีกวิธีคือใช้ \<line terminator> (จะพูดถึงอีกทีในหัวข้อ escape sequences)
```java
@Test
void noTrailingNewline() {
  String textBlock = """
      <html>
          <body>
              <p>It was you. You were my ghost.</p>
          </body>
      </html>""";
  String expected = "<html>\n" +
                    "    <body>\n" +
                    "        <p>It was you. You were my ghost.</p>\n" +
                    "    </body>\n" +
                    "</html>";
  assertThat(textBlock).isEqualTo(expected);
}
```

## Leading White Space

white space ด้านหน้าจะโดนเอาออกเท่ากันทุกบรรทัด โดยจะเอาออกจนเท่ากับตำแหน่งของตัวอักษรตัวแรกของบรรทัดใดบรรทัดหนึ่ง

งงมั้ยครับ?

งงเนอะ ดูตัวอย่างดีกว่า ตัวอย่างด้านล่างใช้ `.` แทน white space ที่ถูกเอาออกไป
```java
String textBlock = """
....<html>   
....    <body>  
....        <p>If you're good at something, never do it for free</p>   
....    </body>  
....</html>   
....""";
```
ถ้าเราเลื่อน closing delimiter มาทางซ้ายก็จะได้ leading white space นำหน้าแต่ละบรรทัด
```java
String textBlock = """
....    <html>
....        <body>
....            <p>
....                Memories can be distorted. They're just an interpretation,
....                they're not a record, and they're irrelevant if you have the facts.
....            </p>
....        </body>
....    </html>
....""";
```

## Trailing White Space

white space ด้านหลังจะโดนเอาออกทั้งหมดทุกบรรทัดเลย เพราะโดยทั่วไป white space ด้านหลังมักจะมาโดยไม่ตั้งใจอยู่แล้ว
อย่างไรก็ตาม ถ้าอยากให้มี trailing white space ก็สามารถทำได้ด้วยการใช้ escape sequence `\s`
(จะพูดถึงอีกทีในหัวข้อ escape sequences)

## Escape Sequence

ใน Java **Text Block ไม่ใช่ Raw String ฉะนั้น escape sequence ต่างๆ ยังทำงานเหมือนเดิม**
```java
@Test
void escapeSequencesAreInterpreted() {
  String textBlock = """
          We can still use \t as tab.
          And we can still use \n for a new line
          And we can escape \' and \" though it is unnecessary.
          Because we can use ' and " directly.
          But if we want to use three double quotes we must escape one of them.
          Like this \"""
          this "\""
          or this ""\"
          """;
  String expected = "We can still use \t as tab.\n" +
                    "And we can still use \n" +
                    " for a new line\n" +
                    "And we can escape \' and \" though it is unnecessary.\n" +
                    "Because we can use ' and \" directly.\n" +
                    "But if we want to use three double quotes we must escape one of them.\n" +
                    "Like this \"\"\"\n" +
                    "this \"\"\"\n" +
                    "or this \"\"\"\n";
  assertThat(textBlock).isEqualTo(expected);
}
```
ตามตัวอย่างด้านบนเรายังใช้ `\t` `\n` และ escape sequence ทุกตัวได้เหมือนเดิม สำหรับ double quote เราไม่จำเป็นต้อง
escape ก็ได้ ยกเว้นถ้าเราอยากพิมพ์ double quote ติดกัน 3 ตัว เราต้อง escape เป็น `\"` ตัวนึงเพื่อไม่ให้กลายเป็น `"""` delimiter

การที่ Text Block escape sequence ได้นี่เองทำให้เราต้องระวังมากๆ เวลา copy text จากที่อื่นมาวางต้องคอยดูว่าใน text
นั้นมี escape sequence อยู่หรือเปล่า เช่นใน JSON อาจจะมี `\n` หรือ sequence อื่นๆ อยู่ใน value ก็ได้ ซึ่งจะทำให้
alignment ของ text ที่เราตั้งใจไว้ผิดเพี้ยนไป :(
```java
String json = """
    {
      "someKey": "copied text might contain \n inside the text"
    }
    """;
```

## Escape Sequence ใหม่

Java ออก escape sequence ใหม่มาใช้คู่กับ Text Block

### \\\<line-terminator>

บางครั้งเราต้องการพิมพ์ข้อความบรรทัดเดียวยาวๆ จะทำเป็น String literal ก็ไม่สะดวก จะทำเป็น Text Block
ก็จะโดนบังคับขึ้นบรรทัดใหม่ ในกรณีนี้เราสามารถใช้ `\` เป็นตัวอักษรสุดท้ายในบรรทัดเพื่อไม่ให้มีการขึ้นบรรทัดใหม่ได้
ตามตัวอย่างด้านล่าง
```java
@Test
void useBackslashToContinueInTheSameLine() {
  String textBlock = """
      We shall go on to the end. We shall fight in France, we shall fight on \
      the seas and oceans, we shall fight with growing confidence and growing \
      strength in the air. We shall defend our island, whatever the cost may be...\
      """;
  String expected = "We shall go on to the end. We shall fight in France, we shall fight on " +
      "the seas and oceans, we shall fight with growing confidence and growing " +
      "strength in the air. We shall defend our island, whatever the cost may be...";
  assertThat(textBlock).isEqualTo(expected);
}
```
*Note: \\\<line-terminator> ใช้ได้กับเฉพาะ Text Block เท่านั้น ใช้กับ String literal ไม่ได้*

### \s Single Space

เนื่องจาก Text Block จะ trim trailing white space ออกเสมอ ถ้าเราอยากให้ text ของเรามี trailing white space
สามารถทำได้โดยการใช้ `\s` เป็นรั้วกั้น
```java
@Test
void useBackslashSAsASingleSpaceToAvoidTrimming() {
  String textBlock = """
      A: How would you like to die?    \s
      B: Old.                          \s
      A: You chose the wrong profession.""";
  String expected = "A: How would you like to die?     \n" +
                    "B: Old.                           \n" +
                    "A: You chose the wrong profession.";
  assertThat(textBlock).isEqualTo(expected);
}
```
*Note: \s ใช้ได้กับทั้ง Text Block และ String literal*

## การ Concat Text Block

Text Block เป็น String ธรรมดา สามารถ concat กับ String อื่นๆ ด้วยเครื่องหมาย `+` ได้ตามปกติ แต่เนื่องจากการเขียน
Text Block ต้องเขียนหลายบรรทัด เวลาเขียนจะดูไม่ค่อยสวยงามเท่าไหร่
```java
String thingToSay = "Abracadabra";
String textBlock = """
    A: Do you have anything to say?
    B: \
    """ + thingToSay + """
    .\
    """;
```

วิธีที่สวยกว่าคือใช้ class method String::format แทน

แต่วิธีที่สวยที่สุดคือใช้ instance method ใหม่ String::formatted
```java
String thingToSay = "Abracadabra";
String textBlock = """
    A: Do you have anything to say?
    B: %s.\
    """.formatted(thingToSay);
```

## สรุป

- Text Block ช่วยในการเขียน multiline string เช่นพวก code snippet ต่างๆ
- Text Block ไม่ใช่ raw string! ยังมีการใช้ escape sequence เหมือนเดิม
- การจัด format ของ Text Block มีเรื่องยิบย่อยที่ต้องรู้เยอะ แนะนำให้ลองเล่นเองหรือตามไปดู
[source code](https://github.com/withyuu/java-11-to-17) ของบทความเพิ่มเติมครับ


[^1]: Text Block feature release ใน Java 15
