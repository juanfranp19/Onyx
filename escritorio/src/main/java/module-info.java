module onyx.escritorio {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens onyx.escritorio to javafx.fxml;
    opens onyx.escritorio.network to com.fasterxml.jackson.databind;
    opens onyx.escritorio.models to com.fasterxml.jackson.databind;

    exports onyx.escritorio;
    exports onyx.escritorio.network;
    exports onyx.escritorio.models;
    exports onyx.escritorio.utils;
    exports onyx.escritorio.controllers;
    opens onyx.escritorio.controllers to javafx.fxml;

}