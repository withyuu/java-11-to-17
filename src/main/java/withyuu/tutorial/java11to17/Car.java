package withyuu.tutorial.java11to17;

public class Car implements Vehicle {

  @Override
  public void run() {
    System.out.println("A car is running");
  }

  public void drift() {
    System.out.println("A car is drifting");
  }

}
