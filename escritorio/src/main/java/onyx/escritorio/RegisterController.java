package onyx.escritorio;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @FXML
    private void handleRegister(ActionEvent event) {
        boolean isValid = true;

        // Reset styles
        resetStyle(inputEmail);
        resetStyle(inputPassword);
        resetStyle(inputRepeatPassword);

        // Validate Email
        if (!EMAIL_PATTERN.matcher(inputEmail.getText()).matches()) {
            addErrorStyle(inputEmail);
            isValid = false;
        }

        // Validate Password Match
        String pass = inputPassword.getText();
        String repeatPass = inputRepeatPassword.getText();

        if (pass.isEmpty() || !pass.equals(repeatPass)) {
            addErrorStyle(inputPassword);
            addErrorStyle(inputRepeatPassword);
            isValid = false;
        }

        if (isValid) {
            System.out.println("Registro exitoso para: " + inputEmail.getText());
            // TODO: Implement actual registration logic here.
            // For now, we can just switch back to login or show a success message.
            handleBack(event);
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
}
