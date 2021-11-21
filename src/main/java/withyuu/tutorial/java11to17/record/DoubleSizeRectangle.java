package withyuu.tutorial.java11to17.record;

record DoubleSizeRectangle(int width, int height) implements Shape {

  DoubleSizeRectangle {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Invalid width or height");
    }
    width *= 2;
    height *= 2;
  }

  public int width() {
    return this.width;
  }

  @Override
  public int area() {
    return width * height;
  }
}
