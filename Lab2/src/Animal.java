import java.util.Objects;

public class Animal implements Comparable<Animal>{
    String name;
    String species;
    int age;
    double price;
    double weight;
    AnimalCondition condition;

    public Animal(String name, String species, int age, double price, double weight, AnimalCondition condition) {
        this.name = name;
        this.species = species;
        this.age = age;
        this.price = price;
        this.weight = weight;
        this.condition = condition;
    }

    public void print() {
        System.out.println("Name: " + this.name + "\nSpecies: " + this.species + "\nAge: " + this.age + "\nPrice: " + this.price + "\nWeight: " + this.weight + "\nCondition: " + this.condition);
    }

    @Override
    public int compareTo(Animal animal) {
        if(Objects.equals(this.name, animal.name) && this.age == animal.age && Objects.equals(this.species, animal.species))
            return 0;
      return 1;
    }
}

