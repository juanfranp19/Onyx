package onyx.escritorio;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainLayoutController {

    @FXML
    private HBox navGrupos;

    @FXML
    private HBox navTareas;

    @FXML
    private HBox navPendientes;

    @FXML
    private StackPane contentArea;

    private HBox currentActiveNav;

    @FXML
    public void initialize() {
        currentActiveNav = navGrupos;
        loadView("grupos-view.fxml");
    }

    @FXML
    private void showGrupos() {
        setActiveNav(navGrupos);
        loadView("grupos-view.fxml");
    }

    @FXML
    private void showTareas() {
        setActiveNav(navTareas);
        loadPlaceholder("Tareas", "Vista de tareas - Próximamente");
    }

    @FXML
    private void showPendientes() {
        setActiveNav(navPendientes);
        loadPlaceholder("Pendientes", "Vista de pendientes - Próximamente");
    }

    @FXML
    private void showSettings(ActionEvent event) {
        loadPlaceholder("Ajustes", "Configuración de la aplicación - Próximamente");
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), stage.getScene().getWidth(), stage.getScene().getHeight());
            scene.getStylesheets().add(getClass().getResource("/Main.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActiveNav(HBox newActiveNav) {
        if (currentActiveNav != null) {
            currentActiveNav.getStyleClass().remove("active");
        }
        newActiveNav.getStyleClass().add("active");
        currentActiveNav = newActiveNav;
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(fxmlFile));
            Node view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            loadPlaceholder("Error", "No se pudo cargar la vista: " + fxmlFile);
        }
    }

    private void loadPlaceholder(String title, String message) {
        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(title);
        titleLabel.getStyleClass().add("placeholder-title");

        javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(message);
        messageLabel.getStyleClass().add("placeholder-message");

        javafx.scene.layout.VBox placeholder = new javafx.scene.layout.VBox(16, titleLabel, messageLabel);
        placeholder.setAlignment(javafx.geometry.Pos.CENTER);
        placeholder.getStyleClass().add("placeholder-container");

        contentArea.getChildren().clear();
        contentArea.getChildren().add(placeholder);
    }
}
