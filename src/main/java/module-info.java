module com.example.hotel20 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.hotel20 to javafx.fxml;
    exports com.example.hotel20;
}