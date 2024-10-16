class Circle extends Figure implements Printable {

    double radius;
    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
      }

    @Override
    public double calculatePerimeter() {
        return  2 * Math.PI * radius;
    }

    @Override
    public void print() {
        System.out.println("Area of a circle: " + calculateArea());
        System.out.println("Perimeter of a circle: " + calculatePerimeter());
    }
}
