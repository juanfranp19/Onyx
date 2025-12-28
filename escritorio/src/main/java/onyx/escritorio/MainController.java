package onyx.escritorio;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainController {


    @FXML
    private Button btnLogin;

    @FXML
    private TextField inputuser;

    @FXML
    private TextField inputpassword;


    public void initialize() {
        btnLogin.setDisable(true);
    }


}
