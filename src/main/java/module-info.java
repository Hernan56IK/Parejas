module com.example.minigamerecu {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.minigamerecu to javafx.fxml;
    opens com.example.minigamerecu.controller to javafx.fxml;
    opens com.example.minigamerecu.model to javafx.fxml;
    
    exports com.example.minigamerecu;
    exports com.example.minigamerecu.manager;
    exports com.example.minigamerecu.model;
}