import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShelterManager {
    Map<String, AnimalShelter> shelters;

    public void addToShelter(String name, Animal animal) {
        this.shelters.get(name).addAnimal(animal);
    }

    public ShelterManager() {
        this.shelters = new HashMap<>();
    }

    public void addShelter(String name, int capacity) {
        AnimalShelter shelter = new AnimalShelter(name, capacity);
        shelters.put(name, shelter);
    }

    public void removeShelter(String name) {
        shelters.remove(name.toLowerCase());
    }

    public void findEmpty() {
        List<String> names = new ArrayList<>();
        for (Map.Entry<String, AnimalShelter> entry : shelters.entrySet()) {
            if (!names.contains(entry.getKey())) {
                names.add(entry.getKey());
            }
        }
    }

    public void summary() {
        for (Map.Entry<String, AnimalShelter> entry : shelters.entrySet()) {
            System.out.println(entry.getValue().shelterName + " is " + String.format("%.2f", (double)(entry.getValue().animalList.size()) / (double) (entry.getValue().maxCapacity) * 100) + "% full");
        }
    }
}
