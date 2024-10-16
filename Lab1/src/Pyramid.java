public class Pyramid implements Printable{

    Figure base;
    double height;

    public Pyramid(Figure base, double height){
        this.base = base;
        this.height = height;
    }

    public double volume() {
        return (1/3) * base.calculatePerimeter() *height;
    }

    @Override
    public void print()
    {
        System.out.println("Volume of a pyramid: " + volume());
    }
}
