class Rectangle extends Figure implements Printable {

    double width;
    double height;

    public Rectangle(double width, double height) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be greater than zero.");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be greater than zero.");
        }

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
