module com.example.lab3 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.swing;

    opens com.example.lab3 to javafx.fxml;
    exports com.example.lab3;
}