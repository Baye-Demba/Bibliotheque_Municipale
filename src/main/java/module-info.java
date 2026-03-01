module com.example.Bibliotheque_Municipale {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.bibliotheque_municipale to javafx.fxml;
    opens com.example.bibliotheque_municipale.controleur to javafx.fxml;
    opens com.example.bibliotheque_municipale.modele to javafx.base;

    exports com.example.bibliotheque_municipale;
    exports com.example.bibliotheque_municipale.controleur;
    exports com.example.bibliotheque_municipale.modele;
    exports com.example.bibliotheque_municipale.dao;
    exports com.example.bibliotheque_municipale.util;
}
