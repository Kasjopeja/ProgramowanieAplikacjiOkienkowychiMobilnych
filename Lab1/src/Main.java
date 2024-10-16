import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();

        switch (option) {
            case 0:
                System.out.println("Enter edge and height");
                int edge = scanner.nextInt();
                int h = scanner.nextInt();
                Triangle a = new Triangle(edge,h);
                a.print();
                break;
            case 1:
                System.out.println("Enter two edges");
                int width = scanner.nextInt();
                int height = scanner.nextInt();
                Rectangle b = new Rectangle(width,height);
                b.print();
                break;
            case 2:
                System.out.println("Enter radius");
                int radius = scanner.nextInt();
                Circle c = new Circle(radius);
                c.print();
                break;
            case 3:
                System.out.println("Enter edge and two heights");
                int edgep = scanner.nextInt();
                int hp = scanner.nextInt();
                int heightp = scanner.nextInt();
                Pyramid d = new Pyramid(new Triangle(edgep,hp), heightp);
                d.print();
                break;
        }
    }
}