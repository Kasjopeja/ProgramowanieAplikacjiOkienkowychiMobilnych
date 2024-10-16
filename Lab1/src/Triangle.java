class Triangle extends Figure implements Printable {

    double a;
    double height;

    public Triangle(double a, double height) {
        this.a = a;
        this.height = height;
    }

    @Override
    public double calculateArea() {
        return 0.5 * a * height;
    }

    @Override
    public double calculatePerimeter() {
        return 3 * a;
    }

    @Override
    public void print() {
        System.out.println("Area of a triangle: " + calculateArea());
        System.out.println("Perimeter of a triangle: " + calculatePerimeter());
    }
}
