package onyx.escritorio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class MainApplication extends Application {
    private static final double INITIAL_WIDTH = 1280;
    private static final double INITIAL_HEIGHT = 720;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        try (InputStream iconStream = MainApplication.class.getResourceAsStream("/onyx/escritorio/app.png")) {
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), INITIAL_WIDTH, INITIAL_HEIGHT);
        scene.getStylesheets().add(
                getClass().getResource("/Main.css").toExternalForm()
        );

        stage.setTitle("Onyx");
        stage.setScene(scene);

        // Tamaño mínimo y máximo (solo se puede reducir)
//        stage.setMinWidth(INITIAL_WIDTH / 1.1);
//        stage.setMinHeight(INITIAL_HEIGHT / 1.2);
//        stage.setMaxWidth(INITIAL_WIDTH);
//        stage.setMaxHeight(INITIAL_HEIGHT);

        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        Application.launch(MainApplication.class, args);
    }
}
