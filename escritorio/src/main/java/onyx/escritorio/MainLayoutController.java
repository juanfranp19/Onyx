package onyx.escritorio;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.geometry.Side;
import javafx.util.Duration;
import onyx.escritorio.utils.Session;

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

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblEmail;

    @FXML
    private Button btnSettings;

    private HBox currentActiveNav;

    @FXML
    public void initialize() {
        currentActiveNav = navGrupos;
        loadView("grupos-view.fxml");
        
        // Cargar datos de sesión
        Session session = Session.getInstance();
        if (session.isLoggedIn()) {
            lblUsername.setText(session.getUsername());
            lblEmail.setText(session.getEmail());
        }
    }

    @FXML
    private void showSettingsMenu(ActionEvent event) {
        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem itemSettings = new MenuItem("⚙  Ajustes");
        itemSettings.getStyleClass().add("menu-item");
        itemSettings.setOnAction(e -> showSettings());
        
        MenuItem itemLogout = new MenuItem("🚪  Cerrar Sesión");
        itemLogout.getStyleClass().add("menu-item");
        itemLogout.setOnAction(e -> logout());
        
        contextMenu.getItems().addAll(itemSettings, itemLogout);
        contextMenu.getStyleClass().add("context-menu");
        
        // Ajuste: usar btnSettings como nodo ancla, Side.RIGHT, y ajustar offset
        // Offset X: 10px para separarlo del botón
        // Offset Y: 0 para centrarlo si JavaFX lo hace bien, o ajustar si sale mal.
        // Si sale muy abajo, probamos coordenadas de pantalla.
        contextMenu.show(btnSettings, Side.RIGHT, 10, 0); 
    }

    @FXML
    private void showGrupos() {
        setActiveNav(navGrupos);
        loadView("grupos-view.fxml");
    }

    @FXML
    private void showTareas() {
        setActiveNav(navTareas);
        loadView("tareas-view.fxml");
    }

    @FXML
    private void showPendientes() {
        setActiveNav(navPendientes);
        loadPlaceholder("Pendientes", "Vista de pendientes - Próximamente");
    }

    private void showSettings() {
        loadPlaceholder("Ajustes", "Configuración de la aplicación - Próximamente");
    }

    private void logout() {
        try {
            Session.getInstance().clear();
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
            
            // Animación de entrada
            view.setOpacity(0);
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
            FadeTransition fade = new FadeTransition(Duration.millis(300), view);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
            
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
        
        placeholder.setOpacity(0);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(placeholder);
        
        FadeTransition fade = new FadeTransition(Duration.millis(300), placeholder);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
}
