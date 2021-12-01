package withyuu.tutorial.java11to17.sealed.after.somelib;

public sealed class Dog extends Pet {

  public static final class Hound extends Dog {}
  public static final class Terrier extends Dog {}

  @Override
  public String talk() {
    return "Woof!";
  }
}
