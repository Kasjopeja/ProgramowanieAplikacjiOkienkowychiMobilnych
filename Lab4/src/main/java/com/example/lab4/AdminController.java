package com.example.lab4;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lombok.Setter;
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
        return session.createQuery("FROM AnimalShelter", AnimalShelter.class)
                .getResultList();
    }

    @FXML
    public void initialize() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        shelters = getAllShelters(session);

        currentShelter = shelters.get(0); // Domyślne schronisko
        createAnimalJTable(animalsNode, currentShelter);

        createShelterSelection(shelterSelectionNode);
        //stateComboBox.getItems().addAll("Zdrowe", "Chore", "Kwarantanna", "WTrakcieAdopcji");
        //stateComboBox.setValue("Zdrowe"); // Ustaw wartość domyślną

    }

    private void createAnimalJTable(SwingNode swingNode, AnimalShelter shelter) {
        SwingUtilities.invokeLater(() -> {
            // Sprawdź, czy obiekt shelter jest null
            if (shelter == null) {
                //System.err.println("Shelter is null. Cannot create animal table.");
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

            // Ukryj kolumnę shelter (indeks kolumny, np. 0 - jeśli to pierwsza kolumna)
            int shelterColumnIndex = 7; // Zmień na odpowiedni indeks kolumny, którą chcesz ukryć
            TableColumn shelterColumn = animalTable.getColumnModel().getColumn(shelterColumnIndex);
            shelterColumn.setMaxWidth(0);
            shelterColumn.setMinWidth(0);
            shelterColumn.setPreferredWidth(0);
            shelterColumn.setWidth(0);
            animalTable.removeColumn(shelterColumn); // Usuń całkowicie tę kolumnę, jeśli nie chcesz jej wcale wyświetlać

            // Umieść JTable w JScrollPane
            JScrollPane scrollPane = new JScrollPane(animalTable);

            // Ustaw JScrollPane jako zawartość SwingNode
            swingNode.setContent(scrollPane);
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




//
//    @FXML
//    private void handleAddShelter() {
//        try {
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(getClass().getResource("/com/example/lab4/shelter-dialog-view.fxml"));
//            VBox page = loader.load();
//
//            Stage dialogStage = new Stage();
//            dialogStage.setTitle("Add New Shelter");
//            dialogStage.initModality(Modality.WINDOW_MODAL);
//            dialogStage.setScene(new Scene(page));
//
//            ShelterDialogView controller = loader.getController();
//            controller.setDialogStage(dialogStage);
//
//            dialogStage.showAndWait();
//
//            if (controller.isSaveClicked()) {
//                String name = controller.getShelterName();
//                int capacity = controller.getShelterCapacity();
//
//                data.shelterManager.addShelter(name, capacity);
//                refreshSheltersComboBox(); // Odśwież listę schronisk
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void addAnimalToShelter(Animal newAnimal, Session session) {
//
//        session.beginTransaction();
//
//        currentShelter.addAnimal(newAnimal);  // Dodaj do schroniska w bazie danych
//
//        session.save(newAnimal); // Użyj save do zapisania nowego obiektu
//
//        session.getTransaction().commit();
//
//    }
//
//    public void addShelter(AnimalShelter shelter) {
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        Transaction transaction = session.beginTransaction();
//        session.save(shelter);
//        transaction.commit();
//        session.close();
//    }
//
//    private void refreshSheltersComboBox() {
//        SwingUtilities.invokeLater(() -> {
//            // Teraz zawartość SwingNode jest na pewno JComboBox<String>
//            JComboBox<String> comboBox = (JComboBox<String>) shelterSelectionNode.getContent();
//            if (comboBox != null) {
//                List<AnimalShelter> shelters = data.shelterManager.getAllShelters();
//                comboBox.removeAllItems(); // Usuń istniejące elementy
//                for (AnimalShelter shelter : shelters) {
//                    comboBox.addItem(shelter.getShelterName()); // Dodaj posortowane elementy
//                }
//            }
//        });
//    }
//
//    @FXML
//    private void handleFilterEnter(KeyEvent event) {
//        if (event.getCode() == KeyCode.ENTER) {
//            String filterText = filterTextBox.getText().toLowerCase();
//
//            // Filtrowanie zwierząt na podstawie nazwy lub gatunku
//            filteredAnimals = currentShelter.getAnimalList().stream()
//                    .filter(animal -> animal.getName().toLowerCase().contains(filterText) ||
//                            animal.getSpecies().toLowerCase().contains(filterText))
//                    .toList();
//
//            // Odśwież tabelę z wynikami filtrowania
//            refreshAnimalTable();
//        }
//    }
//
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