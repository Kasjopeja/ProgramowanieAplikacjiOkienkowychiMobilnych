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

    }

    public AnimalShelter getShelter(){
        return shelterManager.getShelterByName("one");
    }
}
