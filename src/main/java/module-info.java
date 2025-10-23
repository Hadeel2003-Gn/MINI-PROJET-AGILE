module com.example.hadeellina {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.hadeellina to javafx.fxml;
    exports com.example.hadeellina;
}