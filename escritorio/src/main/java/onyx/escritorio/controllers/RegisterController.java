package onyx.escritorio.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import onyx.escritorio.MainApplication;
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

    private static final String INPUT_ERROR_CLASS = "input-error";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public void initialize() {
        btnRegister.disableProperty().bind(isRegistering);
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String name = inputName.getText();
        String email = inputEmail.getText();
        String pass = inputPassword.getText();
        String repeatPass = inputRepeatPassword.getText();

        if (validateInputs(name, email, pass, repeatPass)) {
            isRegistering.set(true);
            ApiClient.register(name, email, pass)
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
        if (!node.getStyleClass().contains(INPUT_ERROR_CLASS)) {
            node.getStyleClass().add(INPUT_ERROR_CLASS);
        }
    }

    private void resetStyle(Node node) {
        node.getStyleClass().remove(INPUT_ERROR_CLASS);
    }

    private void showError(String message) {
        showDialog(
            "Error",
            "Error",
            "⚠",
            message,
            "error-dialog-icon",
            null,
            "Entendido",
            null
        );
    }

    private void showSuccess(String message, ActionEvent event) {
        showDialog(
            "Éxito",
            "Registro Exitoso",
            "✔",
            message,
            null,
            "-fx-font-size: 42px; -fx-text-fill: #4ade80;",
            "Ir al Login",
            () -> handleBack(event)
        );
    }

    private void showDialog(
        String windowTitle,
        String titleText,
        String iconText,
        String message,
        String iconClass,
        String iconStyle,
        String buttonText,
        Runnable onConfirm
    ) {
        Stage dialogStage = createDialogStage(windowTitle);

        VBox content = new VBox(16);
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("error-dialog-content");

        Label iconLabel = new Label(iconText);
        if (iconClass != null && !iconClass.isBlank()) {
            iconLabel.getStyleClass().add(iconClass);
        }
        if (iconStyle != null && !iconStyle.isBlank()) {
            iconLabel.setStyle(iconStyle);
        }

        Label titleLabel = new Label(titleText);
        titleLabel.getStyleClass().add("error-dialog-title");

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("error-dialog-message");
        messageLabel.setMaxWidth(280);
        messageLabel.setWrapText(true);

        Button okButton = new Button(buttonText);
        okButton.getStyleClass().add("error-dialog-button");
        okButton.setOnAction(e -> {
            dialogStage.close();
            if (onConfirm != null) {
                onConfirm.run();
            }
        });

        content.getChildren().addAll(iconLabel, titleLabel, messageLabel, okButton);
        VBox.setMargin(okButton, new Insets(8, 0, 0, 0));

        Scene dialogScene = createDialogScene(content);
        dialogStage.setScene(dialogScene);
        centerDialogOnOwner(dialogStage);

        dialogStage.showAndWait();
    }

    private boolean validateInputs(String name, String email, String pass, String repeatPass) {
        clearInputErrorStyles();

        boolean isValid = true;
        if (name.isBlank()) {
            addErrorStyle(inputName);
            isValid = false;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            addErrorStyle(inputEmail);
            isValid = false;
        }

        if (pass.isEmpty() || !pass.equals(repeatPass)) {
            addErrorStyle(inputPassword);
            addErrorStyle(inputRepeatPassword);
            isValid = false;
        }

        return isValid;
    }

    private void clearInputErrorStyles() {
        resetStyle(inputName);
        resetStyle(inputEmail);
        resetStyle(inputPassword);
        resetStyle(inputRepeatPassword);
    }

    private Stage createDialogStage(String title) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initOwner(MainApplication.getPrimaryStage());
        dialogStage.setTitle(title);
        return dialogStage;
    }

    private Scene createDialogScene(VBox content) {
        StackPane root = new StackPane(content);
        root.getStyleClass().add("error-dialog-overlay");
        root.setPadding(new Insets(20));

        Scene dialogScene = new Scene(root);
        dialogScene.setFill(Color.TRANSPARENT);
        dialogScene.getStylesheets().add(getClass().getResource("/Main.css").toExternalForm());
        return dialogScene;
    }

    private void centerDialogOnOwner(Stage dialogStage) {
        Stage owner = MainApplication.getPrimaryStage();
        if (owner != null) {
            dialogStage.setOnShown(e -> {
                dialogStage.setX(owner.getX() + (owner.getWidth() - dialogStage.getWidth()) / 2);
                dialogStage.setY(owner.getY() + (owner.getHeight() - dialogStage.getHeight()) / 2);
            });
        }
    }
}
