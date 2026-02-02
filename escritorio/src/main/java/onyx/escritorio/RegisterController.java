package onyx.escritorio;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import onyx.escritorio.network.ApiClient;

import java.io.IOException;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML
    private TextField inputName;

    @FXML
    private TextField inputEmail;

    @FXML
    private PasswordField inputPassword;

    @FXML
    private PasswordField inputRepeatPassword;

    @FXML
    private Button btnRegister;

    @FXML
    private Button btnBack;

    private final SimpleBooleanProperty isRegistering = new SimpleBooleanProperty(false);

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public void initialize() {
        btnRegister.disableProperty().bind(isRegistering);
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        boolean isValid = true;

        resetStyle(inputName);
        resetStyle(inputEmail);
        resetStyle(inputPassword);
        resetStyle(inputRepeatPassword);

        if (inputName.getText().isBlank()) {
            addErrorStyle(inputName);
            isValid = false;
        }

        if (!EMAIL_PATTERN.matcher(inputEmail.getText()).matches()) {
            addErrorStyle(inputEmail);
            isValid = false;
        }

        String pass = inputPassword.getText();
        String repeatPass = inputRepeatPassword.getText();

        if (pass.isEmpty() || !pass.equals(repeatPass)) {
            addErrorStyle(inputPassword);
            addErrorStyle(inputRepeatPassword);
            isValid = false;
        }

        if (isValid) {
            isRegistering.set(true);
            ApiClient.register(inputName.getText(), inputEmail.getText(), pass)
                .thenAccept(success -> {
                    Platform.runLater(() -> {
                        isRegistering.set(false);
                        if (success) {
                            showSuccess("Registro exitoso", event);
                        } else {
                            showError("Error al registrar usuario. Puede que el nombre o email ya existan.");
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        isRegistering.set(false);
                        showError("Error de conexión con el servidor");
                    });
                    return null;
                });
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), stage.getScene().getWidth(), stage.getScene().getHeight());
            scene.getStylesheets().add(getClass().getResource("/Main.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addErrorStyle(Node node) {
        if (!node.getStyleClass().contains("input-error")) {
            node.getStyleClass().add("input-error");
        }
    }

    private void resetStyle(Node node) {
        node.getStyleClass().remove("input-error");
    }

    private void showError(String message) {
        showDialog("Error", "⚠", message, "error-dialog-icon");
    }

    private void showSuccess(String message, ActionEvent event) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        dialogStage.initOwner(MainApplication.getPrimaryStage());
        dialogStage.setTitle("Éxito");

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(16);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.getStyleClass().add("error-dialog-content"); // Reusing style for now

        javafx.scene.control.Label iconLabel = new javafx.scene.control.Label("✔");
        iconLabel.setStyle("-fx-font-size: 42px; -fx-text-fill: #4ade80;"); // Green check

        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Registro Exitoso");
        titleLabel.getStyleClass().add("error-dialog-title");

        javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(message);
        messageLabel.getStyleClass().add("error-dialog-message");
        messageLabel.setMaxWidth(280);
        messageLabel.setWrapText(true);

        javafx.scene.control.Button okButton = new javafx.scene.control.Button("Ir al Login");
        okButton.getStyleClass().add("error-dialog-button");
        okButton.setOnAction(e -> {
            dialogStage.close();
            handleBack(event);
        });

        content.getChildren().addAll(iconLabel, titleLabel, messageLabel, okButton);
        javafx.scene.layout.VBox.setMargin(okButton, new javafx.geometry.Insets(8, 0, 0, 0));

        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(content);
        root.getStyleClass().add("error-dialog-overlay");
        root.setPadding(new javafx.geometry.Insets(20));

        Scene dialogScene = new Scene(root);
        dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogScene.getStylesheets().add(getClass().getResource("/Main.css").toExternalForm());
        dialogStage.setScene(dialogScene);

        // Center
        Stage owner = MainApplication.getPrimaryStage();
        if (owner != null) {
            dialogStage.setOnShown(e -> {
                dialogStage.setX(owner.getX() + (owner.getWidth() - dialogStage.getWidth()) / 2);
                dialogStage.setY(owner.getY() + (owner.getHeight() - dialogStage.getHeight()) / 2);
            });
        }

        dialogStage.showAndWait();
    }

    private void showDialog(String title, String icon, String message, String iconClass) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        dialogStage.initOwner(MainApplication.getPrimaryStage());
        dialogStage.setTitle(title);

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(16);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.getStyleClass().add("error-dialog-content");

        javafx.scene.control.Label iconLabel = new javafx.scene.control.Label(icon);
        iconLabel.getStyleClass().add(iconClass);

        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(title);
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

        // Center
        Stage owner = MainApplication.getPrimaryStage();
        if (owner != null) {
            dialogStage.setOnShown(e -> {
                dialogStage.setX(owner.getX() + (owner.getWidth() - dialogStage.getWidth()) / 2);
                dialogStage.setY(owner.getY() + (owner.getHeight() - dialogStage.getHeight()) / 2);
            });
        }

        dialogStage.showAndWait();
    }
}
