class Rectangle extends Figure implements Printable {

    double width;
    double height;

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public double calculateArea() {
        return width * height;
    }

    @Override
    public double calculatePerimeter() {
        return 2 * width + 2 * height;
    }

    @Override
    public void print() {
        System.out.println("Area of a rectangle: " + calculateArea());
        System.out.println("Perimeter of a rectangle: " + calculatePerimeter());
    }
}
