import java.util.*;

public class AnimalShelter {
    String shelterName;
    List<Animal> animalList = new ArrayList<Animal>();
    int maxCapacity;

    AnimalShelter(String shelterName, int maxCapacity) {
        this.shelterName = shelterName.toLowerCase();
        this.maxCapacity = maxCapacity;
    }

    public void addAnimal(Animal animal) {
        if (animalList.size() >= maxCapacity) {
            System.err.println("No more room in the shelter available");
            return;
        }

        if (animalList.contains(animal)) {
            System.out.println(animal.species + " " + animal.name + " already present in shelter.");
            return;
        }

        animalList.add(animal);
        System.out.println(animal.species + " " + animal .name+ " was added to the shelter.");
    }

    public void removeAnimal(Animal animal) {
        if (animalList.contains(animal)) {
            animalList.remove(animal);
            System.out.println(animal.species + " " + animal.name + " was removed from the shelter.");
        }
    }

    public void getAnimal(Animal animal) {
        if (animalList.contains(animal)) {
            animal.condition = AnimalCondition.WTrakcieAdopcji;
            animalList.remove(animal);
            System.out.println(animal.species + " " + animal.name + " was adopted");
        }
    }

    public void changeCondition(Animal animal, AnimalCondition condition) {
        if (animalList.contains(animal)) {
            animal.condition = condition;
            System.out.println(animal.species + " " + animal.name + " condition was changed to " + condition);
        }
    }

    public void changeAge(Animal animal, int age) {
        if (animalList.contains(animal)) {
            animal.age = age;
            System.out.println(animal.species + " " + animal.name + " age was changed to " + age);
        }
    }

    public void countByCondition(AnimalCondition condition) {
        int i = 0;
        for (Animal animal : animalList) {
            if (animal.condition == condition) {
                i = i + 1;
            }
        }
        System.out.println(i + " instances of " + condition);
    }

    public void sortByName() {
        animalList.sort(Comparator.comparing(animal -> animal.name));
        summary();
    }

    public void sortByPrice() {
        animalList.sort(Comparator.comparing(animal -> animal.price));
        summary();
    }

    public void search(String name) {
        for (Animal animal : animalList) {
            if (animal.name.equals(name.toLowerCase())) {
                System.out.println(animal.species + " " + animal.name + " was found (index): " + animalList.indexOf(animal));
            }
        }
    }

    public void searchPartial(String name) {
        for (Animal animal : animalList) {
            if (animal.name.contains(name.toLowerCase())) {
                System.out.println(animal.species + " " + animal.name + " was found (index): " + animalList.indexOf(animal));
            }
        }
    }

    public void summary() {
        for(Animal animal : animalList) {
            animal.print();
        }
    }

    public void max() {
        System.out.println(Collections.max(animalList).name);
    }
}
