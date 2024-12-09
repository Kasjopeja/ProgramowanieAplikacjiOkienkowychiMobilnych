package com.example.lab4;

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
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class UserController {

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

}
