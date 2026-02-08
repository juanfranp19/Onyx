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
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import onyx.escritorio.MainApplication;

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

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
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

    // ===== CREATE TASK DIALOG =====

    public static class CreateTaskResult {
        public String titulo;
        public String descripcion;
        public java.time.LocalDateTime fechaVencimiento;
        public Integer grupoId;
        public boolean confirmed;

        public CreateTaskResult(String titulo, String descripcion, java.time.LocalDateTime fechaVencimiento,
                Integer grupoId, boolean confirmed) {
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.fechaVencimiento = fechaVencimiento;
            this.grupoId = grupoId;
            this.confirmed = confirmed;
        }
    }

    public static CreateTaskResult showCreateTaskDialog(java.util.List<onyx.escritorio.models.Grupo> grupos) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initOwner(MainApplication.getPrimaryStage());

        java.util.concurrent.atomic.AtomicReference<CreateTaskResult> result = new java.util.concurrent.atomic.AtomicReference<>(
                new CreateTaskResult(null, null, null, null, false));

        // Content
        VBox content = new VBox(16);
        content.getStyleClass().add("error-dialog-content");
        content.setMinWidth(400);

        Label titleLabel = new Label("Nueva Tarea");
        titleLabel.getStyleClass().add("error-dialog-title");

        TextField tituloInput = new TextField();
        tituloInput.setPromptText("Título de la tarea");
        tituloInput.getStyleClass().add("modern-input");

        TextArea descInput = new TextArea();
        descInput.setPromptText("Descripción (opcional)");
        descInput.setPrefRowCount(3);
        descInput.getStyleClass().add("modern-input");
        descInput.setMaxHeight(80);

        // Date Picker
        javafx.scene.control.DatePicker datePicker = new javafx.scene.control.DatePicker();
        datePicker.setPromptText("Fecha de vencimiento (opcional)");
        datePicker.getStyleClass().add("modern-input");
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setEditable(false);
        datePicker.getEditor().setStyle("-fx-opacity: 1;");
        datePicker.getEditor().setCursor(javafx.scene.Cursor.HAND);
        datePicker.setCursor(javafx.scene.Cursor.HAND);

        // Evento click
        datePicker.getEditor().setOnMouseClicked(e -> {
            if (!datePicker.isShowing()) {
                datePicker.show();
            }
        });
        datePicker.setOnMouseClicked(e -> {
            if (!datePicker.isShowing()) {
                datePicker.show();
            }
        });


        // Group ComboBox
        javafx.scene.control.ComboBox<onyx.escritorio.models.Grupo> grupoCombo = new javafx.scene.control.ComboBox<>();
        grupoCombo.getItems().addAll(grupos);
        grupoCombo.setPromptText("Selecciona un grupo");
        grupoCombo.getStyleClass().add("modern-input");
        grupoCombo.setMaxWidth(Double.MAX_VALUE);

        // Display group name in combo
        grupoCombo.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(onyx.escritorio.models.Grupo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        grupoCombo.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(onyx.escritorio.models.Grupo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });

        // Buttons
        Button btnCancel = new Button("Cancelar");
        btnCancel.getStyleClass().add("ghost-button");
        btnCancel.setOnAction(e -> dialogStage.close());

        Button btnCreate = new Button("Crear Tarea");
        btnCreate.getStyleClass().add("primary-button");
        btnCreate.setOnAction(e -> {
            if (!tituloInput.getText().isBlank() && grupoCombo.getValue() != null) {
                java.time.LocalDateTime fechaVenc = datePicker.getValue() != null
                        ? datePicker.getValue().atStartOfDay()
                        : null;
                result.set(new CreateTaskResult(
                        tituloInput.getText(),
                        descInput.getText(),
                        fechaVenc,
                        grupoCombo.getValue().getId(),
                        true));
                dialogStage.close();
            } else {
                if (tituloInput.getText().isBlank()) {
                    tituloInput.getStyleClass().add("input-error");
                }
                if (grupoCombo.getValue() == null) {
                    grupoCombo.getStyleClass().add("input-error");
                }
            }
        });

        HBox buttons = new HBox(12, btnCancel, btnCreate);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        content.getChildren().addAll(titleLabel, tituloInput, descInput, datePicker, grupoCombo, buttons);

        // Root
        StackPane root = new StackPane(content);
        root.getStyleClass().add("error-dialog-overlay");
        root.setPadding(new javafx.geometry.Insets(20));

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
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

    public static void showSuccessDialog(String titleText, String message) {
        showDialog(
                "Éxito",
                titleText,
                "✓",
                message,
                null,
                "-fx-font-size: 28px; -fx-text-fill: #22c55e;",
                "Aceptar",
                null
        );
    }

    public static void showErrorDialog(String message) {
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

    private static void showDialog(
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

        Label iconLabel = null;
        if (iconText != null && !iconText.isBlank()) {
            iconLabel = new Label(iconText);
            if (iconClass != null && !iconClass.isBlank()) {
                iconLabel.getStyleClass().add(iconClass);
            }
            if (iconStyle != null && !iconStyle.isBlank()) {
                iconLabel.setStyle(iconStyle);
            }
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

        if (iconLabel != null) {
            content.getChildren().addAll(iconLabel, titleLabel, messageLabel, okButton);
        } else {
            content.getChildren().addAll(titleLabel, messageLabel, okButton);
        }
        VBox.setMargin(okButton, new javafx.geometry.Insets(8, 0, 0, 0));

        Scene dialogScene = createDialogScene(content);
        dialogStage.setScene(dialogScene);
        centerDialogOnOwner(dialogStage);

        dialogStage.showAndWait();
    }

    private static Stage createDialogStage(String title) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initOwner(MainApplication.getPrimaryStage());
        dialogStage.setTitle(title);
        return dialogStage;
    }

    private static Scene createDialogScene(VBox content) {
        StackPane root = new StackPane(content);
        root.getStyleClass().add("error-dialog-overlay");
        root.setPadding(new javafx.geometry.Insets(20));

        Scene dialogScene = new Scene(root);
        dialogScene.setFill(Color.TRANSPARENT);
        dialogScene.getStylesheets().add(DialogUtils.class.getResource("/Main.css").toExternalForm());
        return dialogScene;
    }

    private static void centerDialogOnOwner(Stage dialogStage) {
        Stage owner = MainApplication.getPrimaryStage();
        if (owner != null) {
            dialogStage.setOnShown(e -> {
                dialogStage.setX(owner.getX() + (owner.getWidth() - dialogStage.getWidth()) / 2);
                dialogStage.setY(owner.getY() + (owner.getHeight() - dialogStage.getHeight()) / 2);
            });
        }
    }
}
