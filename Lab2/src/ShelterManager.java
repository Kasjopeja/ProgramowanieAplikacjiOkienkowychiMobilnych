import java.util.HashMap;
import java.util.Map;

public class ShelterManager {
    Map<String, AnimalShelter> shelters;

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

    }
}
