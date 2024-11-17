package com.example.lab3;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.*;

public class AdminController {

    private ShelterApplication mainApp;

    @FXML
    private SwingNode animalsNode;

    @FXML
    private SwingNode shelterSelectionNode;

    @FXML
    private TextField filterTextBox;

    @FXML
    private ComboBox<String> stateComboBox;

    private List<Animal> filteredAnimals;
    private ExampleData data;
    private AnimalShelter currentShelter;
    private JTable animalTable;

    @FXML
    public void initialize() {
        data = new ExampleData();
        data.load();

        stateComboBox.getItems().addAll("Zdrowe", "Chore", "Kwarantanna", "WTrakcieAdopcji");
        stateComboBox.setValue("Zdrowe"); // Ustaw wartość domyślną
        currentShelter = data.getDeafultShelter(); // Domyślne schronisko
        createShelterSelection(shelterSelectionNode);
        createAnimalJTable(animalsNode, currentShelter);
    }

    public void setMainApp(ShelterApplication mainApp) {
        this.mainApp = mainApp;
    }

    private void createShelterSelection(SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            List<AnimalShelter> shelters = data.shelterManager.getAllShelters();

            JComboBox<String> shelterComboBox = new JComboBox<>();
            for (AnimalShelter shelter : shelters) {
                shelterComboBox.addItem(shelter.getName());
            }

            shelterComboBox.addActionListener(e -> {
                String selectedShelterName = (String) shelterComboBox.getSelectedItem();
                currentShelter = data.shelterManager.getShelterByName(selectedShelterName);
                createAnimalJTable(animalsNode, currentShelter);
            });

            swingNode.setContent(shelterComboBox);
        });
    }

    private void createAnimalJTable(SwingNode swingNode, AnimalShelter shelter) {
        SwingUtilities.invokeLater(() -> {
            // Sprawdź, czy obiekt shelter jest null
            if (shelter == null) {
                System.err.println("Shelter is null. Cannot create animal table.");
                swingNode.setContent(new JLabel("No shelter selected.")); // Wyświetl komunikat w SwingNode
                return;
            }

            // Pobierz listę zwierząt
            List<Animal> animalList = shelter.getAnimalList();
            if (animalList == null || animalList.isEmpty()) {
                System.out.println("No animals in the selected shelter.");
                swingNode.setContent(new JLabel("No animals in the shelter.")); // Wyświetl komunikat w SwingNode
                return;
            }

            // Utwórz model tabeli
            GenericTableModel<Animal> animalTableModel = new GenericTableModel<>(Animal.class);
            animalTableModel.setData(animalList);

            // Skonfiguruj JTable
            animalTable = new JTable(animalTableModel);
            animalTable.setFillsViewportHeight(true);

            // Umieść JTable w JScrollPane
            JScrollPane scrollPane = new JScrollPane(animalTable);

            // Ustaw JScrollPane jako zawartość SwingNode
            swingNode.setContent(scrollPane);
        });
    }


    @FXML
    private void handleEdit() {
        int selectedRow = animalTable.getSelectedRow();
        if (selectedRow != -1) {
            Animal animal = currentShelter.getAnimalList().get(selectedRow);

            // Otwórz okno edycji
            boolean saveClicked = showEditDialog(animal);
            if (saveClicked) {
                refreshTable(); // Odśwież tabelę po zapisaniu zmian
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Edit Error");
            alert.setHeaderText("No Animal Selected");
            alert.setContentText("Please select an animal to edit.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleAdd() {
        // Stwórz nowe zwierzę z domyślnymi wartościami
        Animal newAnimal = new Animal("New Animal", "Unknown", 0, 0.0, 0.0, AnimalCondition.Zdrowe);

        // Otwórz okno dodawania
        boolean saveClicked = showEditDialog(newAnimal);
        if (saveClicked) {
            currentShelter.addAnimal(newAnimal); // Dodaj nowe zwierzę do aktualnego schroniska
            refreshTable(); // Odśwież tabelę po dodaniu nowego zwierzęcia
        }
    }

    private boolean showEditDialog(Animal animal) {
        try {
            // Załaduj plik FXML
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("animal-dialog-view.fxml"));
            VBox page = loader.load();

            // Stwórz okno dialogowe
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Animal");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(page));

            // Przekaż dane do kontrolera
            AnimalDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setAnimal(animal);

            // Wyświetl okno i czekaj na zamknięcie
            dialogStage.showAndWait();
            return controller.isSaveClicked();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void handleDelete() {
        SwingUtilities.invokeLater(() -> {
            int selectedRow = animalTable.getSelectedRow();
            if (selectedRow != -1) {
                Animal animal = currentShelter.getAnimalList().get(selectedRow);

                // Potwierdzenie usunięcia
                int confirmation = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to delete " + animal.name + "?",
                        "Delete Confirmation",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmation == JOptionPane.YES_OPTION) {
                    currentShelter.removeAnimal(animal);

                    // Wyświetlenie potwierdzenia usunięcia
                    JOptionPane.showMessageDialog(
                            null,
                            "The animal \"" + animal.name + "\" has been successfully deleted.",
                            "Deletion Successful",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    refreshTable(); // Odśwież tabelę po usunięciu
                }
            } else {
                // Informacja, jeśli nie wybrano zwierzęcia
                JOptionPane.showMessageDialog(
                        null,
                        "No animal selected for deletion!",
                        "Deletion Error",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });
    }

    @FXML
    private void handleAddShelter() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/lab3/shelter-dialog-view.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Shelter");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(page));

            ShelterDialogView controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                String name = controller.getShelterName();
                int capacity = controller.getShelterCapacity();

                data.shelterManager.addShelter(name, capacity);
                refreshSheltersComboBox(); // Odśwież listę schronisk
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshSheltersComboBox() {
        SwingUtilities.invokeLater(() -> {
            JComboBox<String> comboBox = (JComboBox<String>) shelterSelectionNode.getContent();
            if (comboBox != null) {
                List<AnimalShelter> shelters = data.shelterManager.getAllShelters();
                comboBox.removeAllItems(); // Usuń istniejące elementy
                for (AnimalShelter shelter : shelters) {
                    comboBox.addItem(shelter.getName()); // Dodaj posortowane elementy
                }
            }
        });
    }

    @FXML
    private void handleFilterEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String filterText = filterTextBox.getText().toLowerCase();

            // Filtrowanie zwierząt na podstawie nazwy lub gatunku
            filteredAnimals = currentShelter.getAnimalList().stream()
                    .filter(animal -> animal.getName().toLowerCase().contains(filterText) ||
                            animal.getSpecies().toLowerCase().contains(filterText))
                    .toList();

            // Odśwież tabelę z wynikami filtrowania
            refreshAnimalTable();
        }
    }

    @FXML
    private void handleStateChange() {
        String selectedState = stateComboBox.getValue();

        // Filtrowanie zwierząt na podstawie stanu
        filteredAnimals = currentShelter.getAnimalList().stream()
                .filter(animal -> animal.getCondition().toString().equalsIgnoreCase(selectedState))
                .toList();

        // Odśwież tabelę z wynikami filtrowania
        refreshAnimalTable();
    }

    private void refreshAnimalTable() {
        SwingUtilities.invokeLater(() -> {
            if (filteredAnimals == null || filteredAnimals.isEmpty()) {
                animalsNode.setContent(new JLabel("No animals found."));
                return;
            }

            GenericTableModel<Animal> animalTableModel = new GenericTableModel<>(Animal.class);
            animalTableModel.setData(filteredAnimals);

            JTable animalTable = new JTable(animalTableModel);
            animalTable.setFillsViewportHeight(true);

            JScrollPane scrollPane = new JScrollPane(animalTable);

            animalsNode.setContent(scrollPane);
        });
    }

    private void refreshTable() {
        createAnimalJTable(animalsNode, currentShelter);
    }
}
