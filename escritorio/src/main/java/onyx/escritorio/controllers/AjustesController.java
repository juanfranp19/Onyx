package onyx.escritorio.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import onyx.escritorio.network.ApiClient;
import onyx.escritorio.utils.DialogUtils;
import onyx.escritorio.utils.Session;

public class AjustesController {

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblUserIcon;

    public void initialize() {
        Session session = Session.getInstance();
        if (session.isLoggedIn()) {
            txtUsername.setText(session.getUsername());
            txtEmail.setText(session.getEmail());
            // Init icon letter
            if (session.getUsername() != null && !session.getUsername().isEmpty()) {
                lblUserIcon.setText(session.getUsername().substring(0, 1).toUpperCase());
            } else {
                lblUserIcon.setText("?");
            }
        }
    }

    @FXML
    private void handleSaveChanges() {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || email.isEmpty()) {
            DialogUtils.showErrorDialog("El nombre de usuario y el email no pueden estar vacíos.");
            return;
        }

        Session session = Session.getInstance();
        Integer userId = session.getUserId();

        if (userId == null) {
            DialogUtils.showErrorDialog("No hay sesión activa.");
            return;
        }

        ApiClient.updateUsuario(userId, username, email, password).thenAccept(success -> {
            Platform.runLater(() -> {
                if (success) {
                    DialogUtils.showSuccessDialog("Perfil Actualizado", "Los cambios se guardaron correctamente.");
                    session.setUser(userId, username, email);
                    lblUserIcon.setText(username.substring(0, 1).toUpperCase());
                    txtPassword.clear();
                } else {
                    DialogUtils.showErrorDialog("No se pudieron guardar los cambios.");
                }
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                DialogUtils.showErrorDialog("Error de conexión con el servidor.");
            });
            return null;
        });
    }
}
