module com.mycompany.proyectotiendajuegos.aaron.franco {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.proyectotiendajuegos.aaron.franco to javafx.fxml;
    exports com.mycompany.proyectotiendajuegos.aaron.franco;
}
