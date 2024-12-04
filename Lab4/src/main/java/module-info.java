module com.example.lab4 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.swing;
    requires jakarta.persistence;
    requires java.naming;
    requires static lombok;
    requires org.hibernate.orm.core;


    opens com.example.lab4 to javafx.fxml, org.hibernate.orm.core;
    exports com.example.lab4;
}