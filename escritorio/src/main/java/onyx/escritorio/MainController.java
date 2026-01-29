package onyx.escritorio;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import onyx.escritorio.network.ApiClient;

import java.io.IOException;

public class MainController {

    @FXML
    private Button btnLogin;

    @FXML
    private TextField inputuser;

    @FXML
    private PasswordField inputpassword;

    private final SimpleBooleanProperty isLoggingIn = new SimpleBooleanProperty(false);

    public void initialize() {
        BooleanBinding fieldsEmpty = inputuser.textProperty().isEmpty()
                .or(inputpassword.textProperty().isEmpty());

        btnLogin.disableProperty().bind(fieldsEmpty.or(isLoggingIn));

        btnLogin.setOnAction(this::onLogin);
    }

    @FXML
    private void onLogin(ActionEvent event) {
        String username = inputuser.getText();
        String password = inputpassword.getText();

        isLoggingIn.set(true);

        ApiClient.login(username, password).thenAccept(success -> {
            Platform.runLater(() -> {
                if (success) {
                    try {
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-layout.fxml"));
                        Scene scene = new Scene(fxmlLoader.load(), stage.getScene().getWidth(), stage.getScene().getHeight());
                        scene.getStylesheets().add(getClass().getResource("/Main.css").toExternalForm());
                        stage.setScene(scene);
                    } catch (IOException e) {
                        e.printStackTrace();
                        showError("Error al cargar la vista principal");
                        isLoggingIn.set(false);
                    }
                } else {
                    showError("Usuario o contraseña incorrectos");
                    isLoggingIn.set(false);
                }
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                showError("Error de conexión con el servidor");
                isLoggingIn.set(false);
            });
            return null;
        });
    }

    private void showError(String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        dialogStage.setTitle("Error");

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(16);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.getStyleClass().add("error-dialog-content");

        javafx.scene.control.Label iconLabel = new javafx.scene.control.Label("⚠");
        iconLabel.getStyleClass().add("error-dialog-icon");

        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Error de Login");
        titleLabel.getStyleClass().add("error-dialog-title");

        javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(message);
        messageLabel.getStyleClass().add("error-dialog-message");
        messageLabel.setMaxWidth(280);
        messageLabel.setWrapText(true);

        javafx.scene.control.Button okButton = new javafx.scene.control.Button("Entendido");
        okButton.getStyleClass().add("error-dialog-button");
        okButton.setOnAction(e -> dialogStage.close());

        content.getChildren().addAll(iconLabel, titleLabel, messageLabel, okButton);
        javafx.scene.layout.VBox.setMargin(okButton, new javafx.geometry.Insets(8, 0, 0, 0));

        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(content);
        root.getStyleClass().add("error-dialog-overlay");
        root.setPadding(new javafx.geometry.Insets(20));

        Scene dialogScene = new Scene(root);
        dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogScene.getStylesheets().add(getClass().getResource("/Main.css").toExternalForm());
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    @FXML
    private void showRegisterView(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("register-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), stage.getScene().getWidth(), stage.getScene().getHeight());
            scene.getStylesheets().add(getClass().getResource("/Main.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
