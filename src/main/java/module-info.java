module com.mycompany.proyectotiendajuegos.aaron.franco {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; 
    requires org.mybatis;
    
    
    opens com.mycompany.proyectotiendajuegos.aaron.franco to javafx.fxml;
    exports com.mycompany.proyectotiendajuegos.aaron.franco;
}