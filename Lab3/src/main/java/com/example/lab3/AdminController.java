package com.example.lab3;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.List;

public class AdminController {

    private ShelterApplication mainApp;

    @FXML
    private SwingNode animalsNode;

    @FXML
    private SwingNode shelterSelectionNode;

    private ExampleData data;
    private AnimalShelter currentShelter; // Aktualnie wybrane schronisko
    private JTable animalTable;

    @FXML
    public void initialize() {
        data = new ExampleData();
        data.load();

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
            GenericTableModel<Animal> animalTableModel = new GenericTableModel<>(Animal.class);
            animalTableModel.setData(shelter.getAnimalList());

            animalTable = new JTable(animalTableModel);
            animalTable.setFillsViewportHeight(true);

            JScrollPane scrollPane = new JScrollPane(animalTable);

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

    private void refreshTable() {
        createAnimalJTable(animalsNode, currentShelter);
    }
}
