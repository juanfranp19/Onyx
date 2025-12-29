package onyx.escritorio;

import javafx.beans.binding.Bindings;
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

public class MainController {

    @FXML
    private Button btnLogin;

    @FXML
    private TextField inputuser;

    @FXML
    private PasswordField inputpassword;

    public void initialize() {
        btnLogin.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> inputuser.getText().isBlank() || inputpassword.getText().isBlank(),
                        inputuser.textProperty(),
                        inputpassword.textProperty()));
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
