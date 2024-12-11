package com.example.lab4;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
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
import org.hibernate.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
        stateComboBox.getItems().addAll("Zdrowe", "Chore", "Kwarantanna", "WTrakcieAdopcji");
        stateComboBox.setValue("Zdrowe"); // Ustaw wartość domyślną

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
            refreshAnimalTable();
        }
    }

    private void refreshAnimalTable() {
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

            // Ukryj kolumnę shelter (indeks kolumny, np. 0 - jeśli to pierwsza kolumna)
            int shelterColumnIndex = 7; // Zmień na odpowiedni indeks kolumny, którą chcesz ukryć
            TableColumn shelterColumn = animalTable.getColumnModel().getColumn(shelterColumnIndex);
            shelterColumn.setMaxWidth(0);
            shelterColumn.setMinWidth(0);
            shelterColumn.setPreferredWidth(0);
            shelterColumn.setWidth(0);
            animalTable.removeColumn(shelterColumn); // Usuń całkowicie tę kolumnę, jeśli nie chcesz jej wcale wyświetlać

            JScrollPane scrollPane = new JScrollPane(animalTable);
            animalsNode.setContent(scrollPane);
        });
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

    public void handleSortShelter() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Pobierz zawartość SwingNode jako JComboBox
                JComboBox<AnimalShelter> comboBox = (JComboBox<AnimalShelter>) shelterSelectionNode.getContent();
                if (comboBox != null) {
                    // Posortuj listę schronisk według pojemności
                    shelters.sort(Comparator.comparingInt(AnimalShelter::getMaxCapacity)); // Sortowanie rosnące według maxCapacity

                    // Odśwież zawartość ComboBox
                    updateComboBox(comboBox);
                }
            } catch (Exception e) {
                e.printStackTrace(); // Loguj wyjątki
                SwingUtilities.invokeLater(() ->
                        shelterSelectionNode.setContent(new JLabel("Error loading shelters.")) // Wyświetl komunikat o błędzie
                );
            }
        });
    }

    private void updateComboBox(JComboBox<AnimalShelter> comboBox) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            SwingUtilities.invokeLater(() -> {
                comboBox.removeAllItems(); // Usuń istniejące elementy
                for (AnimalShelter shelter : shelters) {
                    comboBox.addItem(shelter); // Dodaj pełne obiekty klasy AnimalShelter
                }
                createShelterSelection(shelterSelectionNode); // Utwórz widok selekcji schroniska
            });
        } catch (Exception e) {
            e.printStackTrace(); // Loguj wyjątki
            SwingUtilities.invokeLater(() ->
                    shelterSelectionNode.setContent(new JLabel("Error loading shelters.")) // Wyświetl komunikat o błędzie
            );
        }
    }


    public String dialog_str(String title, String header, String content){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        final String[] value = new String[1];
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            value[0] = input;
        });

        return value[0];
    }

    public void handleSaveSheltersToFile() {
        String fileName = dialog_str("Save binary file", "Enter File Name", "File Name:");
        if (fileName == null || fileName.trim().isEmpty()) {
            System.out.println("File name cannot be empty or null.");
            return;
        }
        try {
            SerializationUtil.saveToFile(fileName, shelters);
            System.out.println("Shelters saved successfully to " + fileName);
        } catch (Exception e) {
            System.err.println("Failed to save shelters: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleLoadSheltersFromFile() {
        String fileName = dialog_str("Load binary file", "Enter File Name", "File Name:");

        if (fileName == null || fileName.trim().isEmpty()) {
            System.out.println("File name cannot be empty.");
            return;
        }

        List<AnimalShelter> loadedShelters = SerializationUtil.loadFromFile(fileName);

        if (loadedShelters == null) {
            System.out.println("Failed to load shelters. Please check the file.");
            return;
        }

        System.out.println("Shelters loaded successfully from " + fileName);

        shelters = loadedShelters;
        createAnimalJTable(animalsNode, currentShelter);
    }

    public void handleExportAnimalsCsv() {
        if (currentShelter == null) {
            System.err.println("No shelter selected. Please select a shelter to export animals.");
            return;
        }
        String fileName = dialog_str("CSV Animal", "Enter file name", "File name:" );
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Animal> animals = session.createQuery("from Animal where shelter.id = :shelterId", Animal.class)
                .setParameter("shelterId", currentShelter.getId())
                .list();
        CsvUtil.exportAnimalsToCsv(fileName, animals);
    }

    public void handleImportAnimalsCsv() throws IOException {
        if (currentShelter == null) {
            System.err.println("No shelter selected. Please select a shelter to import animals.");
            return;
        }
        String fileName = dialog_str("CSV Animal", "Enter file name", "File name:" );
        List<Animal> importedAnimals = CsvUtil.importAnimalsFromCsv(fileName);
        currentShelter.setAnimalList(importedAnimals);
        //saveAnimalsToDatabase(importedAnimals);
        createAnimalJTable(animalsNode, currentShelter);
    }

    private void saveAnimalsToDatabase(List<Animal> importedAnimals) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                for (Animal animal : importedAnimals) {
                    session.saveOrUpdate(animal); // Save or update each animal
                }
                transaction.commit();
                System.out.println("Animals saved successfully to the database.");
            } catch (Exception e) {
                transaction.rollback();
                System.err.println("Error saving animals to the database: " + e.getMessage());
                throw e;
            }
        } catch (Exception e) {
            System.err.println("Error accessing the database: " + e.getMessage());
            throw e;
        }
    }


    public void handleExportShelterCsv() {
        String fileName = dialog_str("CSV shelter", "Enter file name", "File name:" );
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<AnimalShelter> exportshelters = session.createQuery("from AnimalShelter", AnimalShelter.class).list();
        CsvUtil.exportShelterToCsv(fileName, exportshelters);
    }

    public void handleImportShelterCsv() {
        String fileName = dialog_str("CSV shelter", "Enter file name", "File name:" );
        shelters = CsvUtil.importShelterFromCsv(fileName, shelters);
        //saveSheltersToDatabase();
        createShelterSelection(shelterSelectionNode);
    }

    private void saveSheltersToDatabase() {
        // Obtain a Hibernate Session
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                for (AnimalShelter shelter : shelters) {
                    session.saveOrUpdate(shelter); // Save new or update existing shelter
                }
                transaction.commit(); // Commit the transaction if all operations succeed
                System.out.println("Shelters saved successfully to the database.");
            } catch (Exception e) {
                transaction.rollback(); // Rollback in case of any error
                System.err.println("Error saving shelters to the database: " + e.getMessage());
                throw e;
            }
        } catch (Exception e) {
            System.err.println("Error accessing the database: " + e.getMessage());
            throw e;
        }
    }

    private List<Rating> getRatings() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        return session.createQuery("FROM Rating", Rating.class)
                .getResultList();
    }

    public void handleRating() {
        // Pobranie ocen z bazy danych
        List<Rating> ratings = getRatings();

        // Tworzenie ramki głównej
        JFrame frame = new JFrame("Ratings");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        // Nagłówki kolumn
        String[] columnNames = {"ID", "Rating", "Shelter", "Date", "Comment"};

        // Przygotowanie danych do tabeli
        Object[][] data = new Object[ratings.size()][5];
        for (int i = 0; i < ratings.size(); i++) {
            Rating rating = ratings.get(i);
            data[i][0] = rating.getId();
            data[i][1] = rating.getRatingValue();
            data[i][2] = rating.getShelter() != null ? rating.getShelter().getShelterName() : "Unknown";
            data[i][3] = rating.getRatingDate();
            data[i][4] = rating.getComment();
        }

        // Tworzenie modelu tabeli
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(tableModel);

        // Dodanie tabeli do panelu przewijania
        JScrollPane scrollPane = new JScrollPane(table);

        // Dodanie komponentów do ramki
        frame.add(scrollPane, BorderLayout.CENTER);

        // Wyświetlenie okna
        frame.setVisible(true);
    }
}

