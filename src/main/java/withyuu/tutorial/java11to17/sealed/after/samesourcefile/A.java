package withyuu.tutorial.java11to17.sealed.after.samesourcefile;

// If we define all the permitted class in the same source file as the sealed class,
// we can omit the permits keyword
public sealed class A {

  final class B extends A {}
  private static non-sealed class C extends A {}

}

final class D extends A {}

non-sealed class E extends A {}