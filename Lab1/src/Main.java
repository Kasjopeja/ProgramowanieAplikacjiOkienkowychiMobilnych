import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();

        switch (option) {
            case 0:
                Triangle a = new Triangle(3,5);
                a.print();
                break;
            case 1:
                Rectangle b = new Rectangle(3,5);
                b.print();
                break;
            case 2:
                Circle c = new Circle(3);
                c.print();
                break;
        }
    }
}