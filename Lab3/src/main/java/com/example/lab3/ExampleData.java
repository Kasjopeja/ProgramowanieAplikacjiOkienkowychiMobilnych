package com.example.lab3;

public class ExampleData {

    ShelterManager shelterManager = new ShelterManager();

    public void ExampleData(){}

    public void load()
    {
        Animal cat = new Animal("Whiskers", "Cat", 3, 300.50, 4.5, AnimalCondition.Zdrowe);
        Animal dog = new Animal("Buddy", "Dog", 5, 500.00, 20.0, AnimalCondition.WTrakcieAdopcji);
        Animal parrot = new Animal("Polly", "Parrot", 2, 150.75, 0.9, AnimalCondition.Kwarantanna);
        Animal rabbit = new Animal("Thumper", "Rabbit", 1, 120.20, 2.2, AnimalCondition.Chore);

        shelterManager.addShelter("one", 5);

        shelterManager.addToShelter("one", cat);
        shelterManager.addToShelter("one", dog);
        shelterManager.addToShelter("one", parrot);
        shelterManager.addToShelter("one", rabbit);

        Animal hamster = new Animal("Nibbles", "Hamster", 1, 50.00, 0.2, AnimalCondition.Zdrowe);
        Animal snake = new Animal("Slither", "Snake", 4, 220.40, 1.5, AnimalCondition.Kwarantanna);
        Animal turtle = new Animal("Shelly", "Turtle", 10, 310.00, 3.0, AnimalCondition.WTrakcieAdopcji);
        Animal horse = new Animal("Star", "Horse", 7, 1200.75, 500.0, AnimalCondition.Zdrowe);
        Animal goat = new Animal("Billy", "Goat", 3, 400.30, 60.0, AnimalCondition.Chore);
        Animal fish = new Animal("Goldie", "Goldfish", 1, 30.50, 0.1, AnimalCondition.Zdrowe);

        shelterManager.addShelter("two", 10);

        shelterManager.addToShelter("two", hamster);
        shelterManager.addToShelter("two", snake);
        shelterManager.addToShelter("two", turtle);
        shelterManager.addToShelter("two", horse);
        shelterManager.addToShelter("two", goat);
        shelterManager.addToShelter("two", fish);

    }

    public AnimalShelter getDeafultShelter(){
        return shelterManager.getShelterByName("one");
    }
}
