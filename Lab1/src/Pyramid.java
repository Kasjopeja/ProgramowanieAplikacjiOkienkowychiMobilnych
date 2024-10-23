public class Pyramid implements Printable{

    Figure base;
    double height;

    public Pyramid(Figure base, double height){
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be greater than zero.");
        }
        this.base = base;
        this.height = height;
    }

    public double volume() {
        return 0.3 * base.calculatePerimeter() *height;
    }

    public double surfaceArea() {
        double slantHeight = Math.sqrt(Math.pow(height, 2) + 2 * base.calculateArea() / base.calculatePerimeter());
        return base.calculateArea() + (base.calculatePerimeter() * slantHeight / 2);
    }


    @Override
    public void print()
    {
        System.out.println("Volume of a pyramid: " + volume());
        System.out.println("Surface area of a pyramid: " + surfaceArea());
    }
}
