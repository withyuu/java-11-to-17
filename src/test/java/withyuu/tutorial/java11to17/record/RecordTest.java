package withyuu.tutorial.java11to17.record;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import withyuu.tutorial.java11to17.record.annotation.OnField;
import withyuu.tutorial.java11to17.record.annotation.OnFieldAndParameter;
import withyuu.tutorial.java11to17.record.annotation.OnMethod;
import withyuu.tutorial.java11to17.record.annotation.OnParameter;
import withyuu.tutorial.java11to17.record.annotation.OnType;
import withyuu.tutorial.java11to17.record.annotation.OnTypeUse;

class RecordTest {

  @Test
  void accessor() {
    StudentScore s = new StudentScore("Malfoy", 15.0);
    assertThat(s.name()).isEqualTo("Malfoy");
    assertThat(s.score()).isCloseTo(15.0, Percentage.withPercentage(0.1));
  }

  @Test
  void equalsWithObjectOfOtherTypes() {
    Rectangle r = new Rectangle(3, 4);
    DoubleSizeRectangle dr = new DoubleSizeRectangle(3, 4);
    assertThat(r.equals(dr)).isFalse();
  }

  @Test
  void equalsWithSameTypeButDifferentValue() {
    Rectangle r1 = new Rectangle(3, 4);
    Rectangle r2 = new Rectangle(5, 4);
    assertThat(r1.equals(r2)).isFalse();
  }

  @Test
  void equalsWithFloatingPointPrecision() {
    StudentScore s1 = new StudentScore("James", 0.3);
    StudentScore s2 = new StudentScore("Nut", 0.2 + 0.1);
    assertThat(s1.equals(s2)).isFalse();
  }

  @Test
  void hashCodeOfRecord() {
    Rectangle r1 = new Rectangle(3, 4);
    Rectangle r2 = new Rectangle(3, 4);
    assertThat(r1).hasSameHashCodeAs(r2);
  }

  @Test
  void toStringOfRecord() {
    Rectangle r1 = new Rectangle(3, 4);
    assertThat(r1).hasToString("Rectangle[width=3, height=4]");
  }

  @Test
  void localRecord() {
    List<String> aGraders = findGradeAStudent();
    assertThat(aGraders).containsExactly("Granger", "Lovegood");
  }

  @Test
  void customConstructorValidation() {
    assertThrows(IllegalArgumentException.class, () -> {
      DoubleSizeRectangle dr = new DoubleSizeRectangle(3, 0);
    });
  }

  @Test
  void customConstructorParameterModification() {
    DoubleSizeRectangle dr = new DoubleSizeRectangle(3, 4);
    assertThat(dr.width()).isEqualTo(6);
    assertThat(dr.height()).isEqualTo(8);
  }

  @Test
  void customToString() {
    StudentScore sirius = new StudentScore("Sirius", 72.5);
    assertThat(sirius).asString().isEqualTo("Sirius gets 72.50 score!");
  }

  @Test
  void annotationOnType() {
    TestSubject t = new TestSubject(1, 2, 3, 4, 5);
    assertThat(t.getClass()).hasAnnotation(OnType.class);
  }

  @Test
  void annotationOnField() throws Exception {
    TestSubject t = new TestSubject(1, 2, 3, 4, 5);
    Field p3Field = t.getClass().getDeclaredField("p3");
    assertThat(p3Field.getAnnotation(OnField.class)).isNotNull();
  }

  @Test
  void annotationOnMethod() throws Exception {
    TestSubject t = new TestSubject(1, 2, 3, 4, 5);
    Method p2Accessor = t.getClass().getDeclaredMethod("p2");
    assertThat(p2Accessor.getAnnotation(OnMethod.class)).isNotNull();
  }

  @Test
  void annotationOnParameter() {
    TestSubject t = new TestSubject(1, 2, 3, 4, 5);
    Constructor<?>[] constructors = t.getClass().getConstructors();
    Constructor<?> constructor = constructors[0];
    Parameter[] parameters = constructor.getParameters();
    assertThat(parameters[0].getAnnotation(OnParameter.class)).isNotNull();
  }

  @Test
  void annotationOnFieldAndParameter() throws Exception {
    TestSubject t = new TestSubject(1, 2, 3, 4, 5);
    Field p3Field = t.getClass().getDeclaredField("p4");

    Constructor<?>[] constructors = t.getClass().getConstructors();
    Constructor<?> constructor = constructors[0];
    Parameter[] parameters = constructor.getParameters();

    assertThat(p3Field.getAnnotation(OnFieldAndParameter.class)).isNotNull();
    assertThat(parameters[3].getAnnotation(OnFieldAndParameter.class)).isNotNull();
  }

  private List<String> findGradeAStudent() {
    // local record
    record StudentGrade(StudentScore result, String grade) {

    }

    return getExamResults() // List<StudentScore>
        .stream()
        .map(result -> new StudentGrade(result, calculateGrade(result.score())))
        .filter(g -> g.grade().equals("A"))
        .map(StudentGrade::result)
        .map(StudentScore::name)
        .collect(Collectors.toList());
  }

  private List<StudentScore> getExamResults() {
    return Arrays.asList(
        new StudentScore("Granger", 100.0),
        new StudentScore("Potter", 79.99),
        new StudentScore("Wisley", 63.5),
        new StudentScore("Lovegood", 223.14)
    );
  }

  private String calculateGrade(double score) {
    if (score >= 80) {
      return "A";
    } else if (score >= 70) {
      return "B";
    } else if (score >= 60) {
      return "C";
    } else if (score >= 50) {
      return "D";
    }
    return "F";
  }

}
