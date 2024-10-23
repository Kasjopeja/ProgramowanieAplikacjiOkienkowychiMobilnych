public class Main {
    public static void main(String[] args) {

        Animal cat = new Animal("Whiskers", "Cat", 3, 300.50, 4.5, AnimalCondition.Zdrowe);
        Animal dog = new Animal("Buddy", "Dog", 5, 500.00, 20.0, AnimalCondition.WTrakcieAdopcji);
        Animal parrot = new Animal("Polly", "Parrot", 2, 150.75, 0.9, AnimalCondition.Kwarantanna);
        Animal rabbit = new Animal("Thumper", "Rabbit", 1, 120.20, 2.2, AnimalCondition.Chore);

        cat.print();
        dog.print();
        parrot.print();
        rabbit.print();

        AnimalShelter shelter = new AnimalShelter();

        shelter.addAnimal(cat);
        shelter.addAnimal(dog);
        shelter.addAnimal(parrot);
        shelter.addAnimal(rabbit);

        shelter.removeAnimal(cat);
        shelter.getAnimal(dog);


    }
}