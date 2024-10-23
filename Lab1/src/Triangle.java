import static java.lang.Math.sqrt;

class Triangle extends Figure implements Printable {

    double a;
    double height;

    public Triangle(double a, double height) {
        if (a <= 0) {
            throw new IllegalArgumentException("Base 'a' must be greater than zero.");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be greater than zero.");
        }

        this.a = a;
        this.height = height;
    }

    @Override
    public double calculateArea() {
        return 0.5 * a * height;
    }

    //zakładamy trójkąt równoramienny
    @Override
    public double calculatePerimeter() {
        return a + sqrt((a/2) * (a/2) + (height*height) * 2);
    }

    @Override
    public void print() {
        System.out.println("Area of a triangle: " + calculateArea());
        System.out.println("Perimeter of a triangle: " + calculatePerimeter());
    }
}
