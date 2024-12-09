package com.example.lab4;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

public class AdminController {

    @Setter
    private ShelterApplication mainApp;

    @FXML
    private SwingNode animalsNode;

    @FXML
    private SwingNode shelterSelectionNode;

    @FXML
    private TextField filterTextBox;

    @FXML
    private ComboBox<String> stateComboBox;

    private  List<AnimalShelter> shelters;
    private AnimalShelter currentShelter;
    private List<Animal> filteredAnimals;
    private JTable animalTable;

    private List<AnimalShelter> getAllShelters(Session session) {
        return session.createQuery(
                "SELECT DISTINCT s FROM AnimalShelter s LEFT JOIN FETCH s.animalList", AnimalShelter.class
        ).getResultList();
    }


    @FXML
    public void initialize() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        shelters = getAllShelters(session);

        currentShelter = shelters.get(0); // Domyślne schronisko
        createAnimalJTable(animalsNode, currentShelter);

        createShelterSelection(shelterSelectionNode);
//        stateComboBox.getItems().addAll("Zdrowe", "Chore", "Kwarantanna", "WTrakcieAdopcji");
//        stateComboBox.setValue("Zdrowe"); // Ustaw wartość domyślną

    }

    private void createAnimalJTable(SwingNode swingNode, AnimalShelter shelter) {
        SwingUtilities.invokeLater(() -> {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();

                // Załaduj dane kolekcji w sesji
                AnimalShelter managedShelter = session.get(AnimalShelter.class, shelter.getId());
                List<Animal> animalList = managedShelter.getAnimalList();

                if (animalList == null || animalList.isEmpty()) {
                    swingNode.setContent(new JLabel("No animals in the shelter."));
                    return;
                }

                // Stwórz model tabeli
                GenericTableModel<Animal> animalTableModel = new GenericTableModel<>(Animal.class);
                animalTableModel.setData(animalList);

                JTable animalTable = new JTable(animalTableModel);
                animalTable.setFillsViewportHeight(true);

                JScrollPane scrollPane = new JScrollPane(animalTable);
                swingNode.setContent(scrollPane);

                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
                swingNode.setContent(new JLabel("Error loading animals."));
            }
        });
    }


    private void createShelterSelection(SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            // Tworzymy JComboBox, w którym przechowujemy obiekty AnimalShelter
            JComboBox<AnimalShelter> shelterComboBox = new JComboBox<>();

            // Dodajemy obiekty AnimalShelter do ComboBoxa
            for (AnimalShelter shelter : shelters) {
                shelterComboBox.addItem(shelter);
            }

            // Ustawienie renderer-a, aby wyświetlać tylko nazwy schronisk
            shelterComboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    // Jeśli element to obiekt AnimalShelter, wyświetl jego nazwę
                    if (value instanceof AnimalShelter) {
                        value = ((AnimalShelter) value).getShelterName(); // Wyświetlanie tylko nazwy
                    }
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            });

            // Akcja po wybraniu schroniska z ComboBoxa
            shelterComboBox.addActionListener(e -> {
                // Pobieramy pełny obiekt AnimalShelter na podstawie wyboru
                AnimalShelter selectedShelter = (AnimalShelter) shelterComboBox.getSelectedItem();
                if (selectedShelter != null) {
                    currentShelter = selectedShelter;  // Ustawiamy wybrane schronisko
                    createAnimalJTable(animalsNode, currentShelter);  // Tworzymy tabelę zwierząt dla wybranego schroniska
                }
            });

            // Ustawiamy JComboBox jako zawartość SwingNode
            swingNode.setContent(shelterComboBox);
        });
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
    private void handleEdit() {
        int selectedRow = animalTable.getSelectedRow();
        if (selectedRow != -1) {
            // Pobierz zwierzę z bazy danych
            Animal animal = currentShelter.getAnimalList().get(selectedRow);

            // Otwórz okno edycji
            boolean saveClicked = showEditDialog(animal);
            if (saveClicked) {
                // Zapisz zmiany do bazy danych
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    session.beginTransaction();
                    session.update(animal); // Aktualizuj obiekt w bazie danych
                    session.getTransaction().commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                createAnimalJTable(animalsNode, currentShelter);
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
        Animal newAnimal = new Animal("New Animal", "Unknown", 0, 0.0, 0.0, AnimalCondition.Zdrowe, currentShelter);

        // Otwórz okno edycji
        boolean saveClicked = showEditDialog(newAnimal);
        if (saveClicked) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();

                // Zapisz nowe zwierzę do bazy danych
                session.save(newAnimal);
                session.getTransaction().commit();

                // Wyświetlenie potwierdzenia dodania
                JOptionPane.showMessageDialog(
                        null,
                        "The animal \"" + newAnimal.getName() + "\" has been successfully added.",
                        "Addition Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Dodaj nowe zwierzę do listy w currentShelter
                currentShelter.getAnimalList().add(newAnimal);

                // Zaktualizowanie tabeli po dodaniu nowego zwierzęcia
                createAnimalJTable(animalsNode, currentShelter); // Zaktualizowanie tabeli po dodaniu zwierzęcia
            } catch (Exception e) {
                e.printStackTrace();
                // Wyświetlenie błędu, jeśli dodawanie nie powiedzie się
                JOptionPane.showMessageDialog(
                        null,
                        "An error occurred while adding the animal.",
                        "Addition Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }


    @FXML
    private void handleDelete() {
        SwingUtilities.invokeLater(() -> {
            int selectedRow = animalTable.getSelectedRow();
            if (selectedRow != -1) {
                // Pobierz wybrane zwierzę z tabeli
                Animal animal = currentShelter.getAnimalList().get(selectedRow);

                // Potwierdzenie usunięcia
                int confirmation = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to delete " + animal.getName() + "?",
                        "Delete Confirmation",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmation == JOptionPane.YES_OPTION) {
                    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                        session.beginTransaction();

                        // Pobierz zwierzę z bazy danych
                        Animal animalToDelete = session.get(Animal.class, animal.getId());
                        if (animalToDelete != null) {
                            session.delete(animalToDelete); // Usuń zwierzę z bazy danych
                            session.getTransaction().commit();

                            // Wyświetlenie potwierdzenia usunięcia
                            JOptionPane.showMessageDialog(
                                    null,
                                    "The animal \"" + animal.getName() + "\" has been successfully deleted.",
                                    "Deletion Successful",
                                    JOptionPane.INFORMATION_MESSAGE
                            );

                            // Usuń zwierzę z listy w aktualnym schronisku (currentShelter)
                            currentShelter.getAnimalList().remove(animal); // Usuń zwierzę z listy

                            // Zaktualizowanie tabeli po usunięciu
                            createAnimalJTable(animalsNode, currentShelter); // Zaktualizowanie tabeli po usunięciu zwierzęcia
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Wyświetlenie błędu, jeśli usunięcie nie powiedzie się
                        JOptionPane.showMessageDialog(
                                null,
                                "An error occurred while deleting the animal.",
                                "Deletion Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
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
            // Load the shelter dialog view
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/lab4/shelter-dialog-view.fxml"));
            VBox page = loader.load();

            // Set up the dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Shelter");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(page));

            // Get the controller for the dialog
            ShelterDialogView controller = loader.getController();
            controller.setDialogStage(dialogStage);

            // Show the dialog and wait for the user to close it
            dialogStage.showAndWait();

            // Check if the user clicked the Save button
            if (controller.isSaveClicked()) {
                String name = controller.getShelterName();
                int capacity = controller.getShelterCapacity();

                // Persist the new shelter to the database
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    session.beginTransaction();
                    try {
                        AnimalShelter newShelter = new AnimalShelter();
                        newShelter.setShelterName(name);
                        newShelter.setMaxCapacity(capacity);

                        session.save(newShelter); // Save the new shelter
                        session.getTransaction().commit(); // Commit the transaction
                    } catch (Exception ex) {
                        throw ex;
                    }
                }

                // Refresh the combo box or list of shelters in the UI
                SwingUtilities.invokeLater(() -> {
                    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                        // Pobierz zaktualizowaną listę schronisk z bazy danych
                        List<AnimalShelter> updatedShelters = session.createQuery("FROM AnimalShelter", AnimalShelter.class).getResultList();

                        // Zaktualizuj globalną listę schronisk
                        shelters.clear();
                        shelters.addAll(updatedShelters);

                        createShelterSelection(shelterSelectionNode);

                    } catch (Exception e) {
                        e.printStackTrace(); // Loguj wyjątki
                        shelterSelectionNode.setContent(new JLabel("Error loading shelters.")); // Wyświetl komunikat o błędzie
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
        }
    }

    @FXML
    private void handleFilterEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String filterText = filterTextBox.getText().toLowerCase();

            // Pobierz dane zwierząt ze schroniska w sesji Hibernate
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();

                // Załaduj aktualne schronisko wraz z jego zwierzętami
                AnimalShelter managedShelter = session.get(AnimalShelter.class, currentShelter.getId());
                Hibernate.initialize(managedShelter.getAnimalList());

                // Filtrowanie zwierząt na podstawie nazwy lub gatunku
                filteredAnimals = managedShelter.getAnimalList().stream()
                        .filter(animal -> animal.getName().toLowerCase().contains(filterText) ||
                                animal.getSpecies().toLowerCase().contains(filterText))
                        .toList();

                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
                // Wyświetl komunikat o błędzie, jeśli filtracja się nie powiedzie
                JOptionPane.showMessageDialog(
                        null,
                        "Error filtering animals: " + e.getMessage(),
                        "Filtering Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

            // Odśwież tabelę z wynikami filtrowania
            SwingUtilities.invokeLater(() -> {
                if (filteredAnimals == null || filteredAnimals.isEmpty()) {
                    animalsNode.setContent(new JLabel("No animals found."));
                    return;
                }

                // Stwórz model tabeli z przefiltrowanymi zwierzętami
                GenericTableModel<Animal> animalTableModel = new GenericTableModel<>(Animal.class);
                animalTableModel.setData(filteredAnimals);

                JTable animalTable = new JTable(animalTableModel);
                animalTable.setFillsViewportHeight(true);

                JScrollPane scrollPane = new JScrollPane(animalTable);
                animalsNode.setContent(scrollPane);
            });
        }
    }

//    @FXML
//    private void handleStateChange() {
//        String selectedState = stateComboBox.getValue();
//
//        // Filtrowanie zwierząt na podstawie stanu
//        filteredAnimals = currentShelter.getAnimalList().stream()
//                .filter(animal -> animal.getCondition().toString().equalsIgnoreCase(selectedState))
//                .toList();
//
//        // Odśwież tabelę z wynikami filtrowania
//        refreshAnimalTable();
//    }
//
//    private void refreshAnimalTable() {
//        SwingUtilities.invokeLater(() -> {
//            if (filteredAnimals == null || filteredAnimals.isEmpty()) {
//                animalsNode.setContent(new JLabel("No animals found."));
//                return;
//            }
//
//            GenericTableModel<Animal> animalTableModel = new GenericTableModel<>(Animal.class);
//            animalTableModel.setData(filteredAnimals);
//
//            JTable animalTable = new JTable(animalTableModel);
//            animalTable.setFillsViewportHeight(true);
//
//            JScrollPane scrollPane = new JScrollPane(animalTable);
//
//            animalsNode.setContent(scrollPane);
//        });
//    }
//
//    public void handleSortShelter()
//    {
//        SwingUtilities.invokeLater(() -> {
//            // Pobierz zawartość SwingNode jako JComboBox
//            JComboBox<String> comboBox = (JComboBox<String>) shelterSelectionNode.getContent();
//            if (comboBox != null) {
//                // Pobierz listę schronisk i posortuj według pojemności
//                List<AnimalShelter> shelters = data.shelterManager.getAllShelters();
//                shelters.sort(Comparator.comparingInt(AnimalShelter::getMaxCapacity)); // Sortowanie rosnące według maxCapacity
//
//                comboBox.removeAllItems(); // Usuń istniejące elementy
//                for (AnimalShelter shelter : shelters) {
//                    comboBox.addItem(shelter.getShelterName()); // Dodaj elementy w posortowanej kolejności
//                }
//            }
//        });
//    }
//
//    private void refreshTable() {
//        createAnimalJTable(animalsNode, currentShelter);
//    }
}