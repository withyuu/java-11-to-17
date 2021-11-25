# Feature ใหม่ใน Java 17: #3 Restore Always-Strict Floating-Point Semantics

ก่อนจะเข้าเนื้อหาใดๆ ขอสารภาพก่อนว่าทำงานมาเกินสิบปีผมเพิ่งรู้จัก keyword `strictfp` วันนี้นี่แหละครับ 555

สำหรับตัว Feature **Restore Always-Strict Floating-Point Semantics** นี่คิดว่าคงน้อยคนมากที่จะได้ใช้ เอาเป็นว่าบทความนี้เรามาเรียนรู้ประวัติศาสตร์ของ `strictfp` กันดีกว่า

## ปัญหาของ Floating Point
เป็นที่รู้กันดีว่าคอมพิวเตอร์ซึ่งมีหน่วยความจำจำกัดไม่สามารถแสดงค่าจำนวนจริงทั้งหมดได้ การเก็บทศนิยมในภาษาต่างๆ 
จึงใช้การประมาณค่าด้วย standard [IEEE 754](https://en.wikipedia.org/wiki/IEEE_754)

การประมาณค่านี้ทำให้เราไม่สามารถคำนวณค่าทศนิยมที่ถูกเป๊ะๆ ได้ดังตัวอย่างด้านล่าง

**Java**
```
$ jshell
|  Welcome to JShell -- Version 17
|  For an introduction type: /help intro

jshell> 0.1 + 0.2;
$1 ==> 0.30000000000000004
```
**Node.js**
```
$ node
Welcome to Node.js v16.13.0.
Type ".help" for more information.
> 0.1 + 0.2
0.30000000000000004
```
**Python**
```
$ python3
Python 3.8.10 (default, Sep 28 2021, 16:10:42) 
Type "help", "copyright", "credits" or "license" for more information.
>>> 0.1 + 0.2
0.30000000000000004
```

ซึ่งนี่ก็ยัง**ไม่ใช่สิ่งที่ strictfp จะเข้ามาแก้ไข**แต่อย่างใด :P
แค่จะแสดงให้เห็นว่าการแสดง floating point ของแต่ละภาษาใช้ IEEE 754 เป็น standard

## ประวัติศาสตร์ของ strictfp
การมาของ keyword `strictfp` นั้นจริงๆ แล้วมีความเกี่ยวเนื่องกับ CPU architecture ในอดีต เพราะฉะนั้นต้องย้อนไปดูประวัติศาสตร์กันนิดนึง

### ช่วงก่อน Java 1.2
Java ก่อน version 1.2 จะบังคับให้การคำนวณ floating point ทั้งหมดเป็นไปตาม standard IEEE 754
วิธีนี้ทำให้การคำนวณ floating point ไม่ว่าจะอยู่บน CPU architecture ไหนก็จะได้ผลการคำนวณเท่ากันเสมอ
อย่างไรก็ตาม ณ เวลานั้นการคำนวณ floating point ด้วย standard IEEE 754 บน CPU architecture x86
ยุ่งยากและ performance ไม่ดีเท่าที่ควร

### Java 1.2 ถึง Java 16
จากเหตุผลด้านบนที่ว่าการคำนวณ floating point ทำได้ช้า Java จึงออก keyword ใหม่มานั่นก็คือ `strictfp` นั่นเอง
การคำนวณ floating point ที่ระบุว่าเป็น strictfp จะบังคับให้การคำนวณเป็นไปตาม standard IEEE 754 (strict)
ในทางตรงกันข้าม ถ้าไม่ระบุว่าเป็น strictfp การคำนวณ floating point ก็จะแล้วแต่ CPU architecture ของเครื่องที่
JVM run อยู่เลยซึ่งก็จะเร็วกว่าการคำนวณแบบ strict

Code snippet ของ [class StrictMath ใน Java 16](https://github.com/openjdk/jdk16/blob/4de3a6be9e60b9676f2199cd18eadb54a9d6e3fe/src/java.base/share/classes/java/lang/StrictMath.java#L81)
```
public static strictfp double toRadians(double angdeg) {
  // Do not delegate to Math.toRadians(angdeg) because
  // this method has the strictfp modifier.
  return angdeg * DEGREES_TO_RADIANS;
}
```

วิธีการใช้ strictfp สามารถดูได้จาก [ที่นี่](https://www.baeldung.com/java-strictfp)

## Java 17: Always-Strict Floating-Point Semantics
เมื่อเวลาผ่านไป CPU ตั้งแต่ Pentium 4 เป็นต้นมามี SSE2 extension ซึ่งช่วยให้การคำนวณ floating point แบบ strict เร็วขึ้นมาก ซึ่งทั้ง
Intel และ AMD ก็ support SSE2 มาระยะเวลาหนึ่งแล้ว เป็นเหตุผลให้ Java กลับมาบังคับใช้ strict mode กับทุกๆ การคำนวณ floating point ไปเลยเหมือนสมัยก่อน Java 1.2
และเราก็ไม่จำเป็นต้องใช้ keyword `strictfp` อีกต่อไป

Code snippet ของ [class StrictMath ใน Java 17](https://github.com/openjdk/jdk17/blob/74007890bb9a3fa3a65683a3f480e399f2b1a0b6/src/java.base/share/classes/java/lang/StrictMath.java#L89)
```
public static double toRadians(double angdeg) {
  return Math.toRadians(angdeg);
}
```

## สรุป
คิดว่าคนส่วนใหญ่คงไม่ได้ยุ่งกับ keyword strictfp นี้สักเท่าไหร่ แต่สำหรับคนที่เคยใช้ strictfp มาก่อนหน้านี้ ถ้า upgrade มาเป็น
Java 17 เมื่อไหร่ก็ไม่ต้องใช้แล้วนะครับ :)

