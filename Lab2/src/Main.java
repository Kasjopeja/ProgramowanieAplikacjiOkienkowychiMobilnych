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

        ShelterManager shelterManager = new ShelterManager();
        shelterManager.addShelter("aaa", 5);

        shelterManager.addToShelter("aaa", cat);
        shelterManager.addToShelter("aaa", dog);
        shelterManager.addToShelter("aaa", parrot);
        shelterManager.addToShelter("aaa", rabbit);

        //test wszytskich metod AnimalShelter

        shelterManager.shelters.get("aaa").changeCondition(parrot,AnimalCondition.Kwarantanna);
        shelterManager.shelters.get("aaa").countByCondition(AnimalCondition.Kwarantanna);
        shelterManager.shelters.get("aaa").changeAge(cat, 2);
        shelterManager.shelters.get("aaa").removeAnimal(cat);
        shelterManager.shelters.get("aaa").getAnimal(dog);
        shelterManager.shelters.get("aaa").sortByName();
        shelterManager.shelters.get("aaa").sortByPrice();
        shelterManager.shelters.get("aaa").search("Buddy");
        shelterManager.shelters.get("aaa").searchPartial("LLY");
        shelterManager.shelters.get("aaa").summary();
        shelterManager.shelters.get("aaa").max();

        //test wszystkich metod ShelterManager

        shelterManager.addShelter("bbb", 3);

        shelterManager.addToShelter("bbb", cat);
        shelterManager.addToShelter("bbb", dog);
        shelterManager.addToShelter("bbb", parrot);
        shelterManager.addToShelter("bbb", rabbit);

        shelterManager.summary();
        shelterManager.removeShelter("bbb");
        shelterManager.findEmpty();

    }
}