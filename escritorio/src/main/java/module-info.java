module onyx.escritorio {
    requires javafx.controls;
    requires javafx.fxml;


    opens onyx.escritorio to javafx.fxml;
    exports onyx.escritorio;
}