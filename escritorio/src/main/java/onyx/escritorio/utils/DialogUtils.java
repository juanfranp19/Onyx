package onyx.escritorio.utils;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import onyx.escritorio.MainApplication;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class DialogUtils {

    public static class CreateGroupResult {
        public String name;
        public String description;
        public boolean confirmed;

        public CreateGroupResult(String name, String description, boolean confirmed) {
            this.name = name;
            this.description = description;
            this.confirmed = confirmed;
        }
    }

    public static CreateGroupResult showCreateGroupDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initOwner(MainApplication.getPrimaryStage());

        AtomicReference<CreateGroupResult> result = new AtomicReference<>(new CreateGroupResult(null, null, false));

        // Content
        VBox content = new VBox(16);
        content.getStyleClass().add("error-dialog-content"); // Reusing style for now
        content.setMinWidth(400);

        Label titleLabel = new Label("Nuevo Grupo");
        titleLabel.getStyleClass().add("error-dialog-title");

        TextField nameInput = new TextField();
        nameInput.setPromptText("Nombre del grupo");
        nameInput.getStyleClass().add("modern-input");

        TextArea descInput = new TextArea();
        descInput.setPromptText("Descripción (opcional)");
        descInput.setPrefRowCount(3);
        descInput.getStyleClass().add("modern-input");
        descInput.setMaxHeight(80);

        // Buttons
        Button btnCancel = new Button("Cancelar");
        btnCancel.getStyleClass().add("ghost-button");
        btnCancel.setOnAction(e -> dialogStage.close());

        Button btnCreate = new Button("Crear Grupo");
        btnCreate.getStyleClass().add("primary-button"); // Changed to primary-button
        btnCreate.setOnAction(e -> {
            if (!nameInput.getText().isBlank()) {
                result.set(new CreateGroupResult(nameInput.getText(), descInput.getText(), true));
                dialogStage.close();
            } else {
                nameInput.getStyleClass().add("input-error");
            }
        });

        HBox buttons = new HBox(12, btnCancel, btnCreate);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        content.getChildren().addAll(titleLabel, nameInput, descInput, buttons);

        // Root
        StackPane root = new StackPane(content);
        root.getStyleClass().add("error-dialog-overlay");
        root.setPadding(new javafx.geometry.Insets(20));
        root.setStyle("-fx-background-color: rgba(0,0,0,0.5);"); // Semi-transparent background

        Scene scene = new Scene(root);
        scene.setFill(null);
        scene.getStylesheets().add(DialogUtils.class.getResource("/Main.css").toExternalForm());

        dialogStage.setScene(scene);
        
        // Center
        Stage owner = MainApplication.getPrimaryStage();
        if (owner != null) {
            dialogStage.setOnShown(e -> {
                dialogStage.setX(owner.getX() + (owner.getWidth() - dialogStage.getWidth()) / 2);
                dialogStage.setY(owner.getY() + (owner.getHeight() - dialogStage.getHeight()) / 2);
            });
        }

        dialogStage.showAndWait();

        return result.get();
    }
}
