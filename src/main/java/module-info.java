module com.example.bibliotheque_municipale {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.bibliotheque_municipale to javafx.fxml;
    exports com.example.bibliotheque_municipale;
}