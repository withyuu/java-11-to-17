package withyuu.tutorial.java11to17.sealed.after.somelib;

public sealed abstract class Pet permits Cat, Dog, Bird {

  public abstract String talk();

}
