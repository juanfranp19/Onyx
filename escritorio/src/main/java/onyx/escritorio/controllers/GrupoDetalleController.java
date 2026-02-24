package onyx.escritorio.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import onyx.escritorio.models.Grupo;
import onyx.escritorio.models.Tarea;
import onyx.escritorio.network.ApiClient;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class GrupoDetalleController {

    @FXML
    private Label lblGroupName;

    @FXML
    private Label lblGroupDescription;

    @FXML
    private Label lblGroupDate;

    @FXML
    private VBox tareasContainer;

    @FXML
    private VBox emptyTareasState;

    @FXML
    private ScrollPane tareasScrollPane;

    private Grupo grupo;
    private Runnable onBack;

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
        mostrarDatosGrupo();
        cargarTareasGrupo();
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    @FXML
    private void handleBack() {
        if (onBack != null) {
            onBack.run();
        }
    }

    private void mostrarDatosGrupo() {
        lblGroupName.setText(grupo.getNombre());

        String desc = grupo.getDescripcion();
        if (desc != null && !desc.isBlank()) {
            lblGroupDescription.setText(desc);
        } else {
            lblGroupDescription.setText("Sin descripción");
            lblGroupDescription.setStyle("-fx-opacity: 0.5; -fx-font-style: italic;");
        }

        if (grupo.getFechaCreacion() != null) {
            try {
                // Try parsing ISO datetime
                java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(grupo.getFechaCreacion());
                lblGroupDate.setText("Creado el " + dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } catch (Exception e) {
                lblGroupDate.setText("Creado el " + grupo.getFechaCreacion());
            }
        } else {
            lblGroupDate.setText("");
        }
    }

    private void cargarTareasGrupo() {
        if (grupo == null || grupo.getId() == null)
            return;

        ApiClient.getTareasPorGrupoPublic(grupo.getId()).thenAccept(tareas -> {
            Platform.runLater(() -> {
                if (tareas == null || tareas.isEmpty()) {
                    showEmptyTareas();
                } else {
                    showTareasList(tareas);
                }
            });
        }).exceptionally(ex -> {
            Platform.runLater(this::showEmptyTareas);
            return null;
        });
    }

    private void showEmptyTareas() {
        emptyTareasState.setVisible(true);
        emptyTareasState.setManaged(true);
        tareasScrollPane.setVisible(false);
        tareasScrollPane.setManaged(false);
    }

    private void showTareasList(List<Tarea> tareas) {
        emptyTareasState.setVisible(false);
        emptyTareasState.setManaged(false);
        tareasScrollPane.setVisible(true);
        tareasScrollPane.setManaged(true);

        tareasContainer.getChildren().clear();

        for (Tarea tarea : tareas) {
            tareasContainer.getChildren().add(createTareaCard(tarea));
        }
    }

    private VBox createTareaCard(Tarea tarea) {
        VBox card = new VBox(6);
        card.getStyleClass().add("detail-tarea-card");
        card.setPadding(new Insets(16, 20, 16, 20));

        // Header: icon + title
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        boolean completada = Boolean.TRUE.equals(tarea.getCompletada());
        Label icon = new Label(completada ? "📋✅" : "📋⬜");
        icon.setStyle("-fx-font-size: 16px;");

        Label titulo = new Label(tarea.getTitulo());
        titulo.getStyleClass().add("detail-tarea-title");
        if (completada) {
            titulo.setStyle("-fx-text-fill: #9ca3af; -fx-strikethrough: true;");
        }

        header.getChildren().addAll(icon, titulo);
        card.getChildren().add(header);

        // Description
        String desc = tarea.getDescripcion();
        if (desc != null && !desc.isBlank()) {
            Label descripcion = new Label(desc);
            descripcion.getStyleClass().add("detail-tarea-desc");
            descripcion.setWrapText(true);
            card.getChildren().add(descripcion);
        }

        // Due date
        if (tarea.getFechaVencimiento() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            Label fecha = new Label("📅 Vence: " + tarea.getFechaVencimiento().format(formatter));
            fecha.getStyleClass().add("detail-tarea-date");
            card.getChildren().add(fecha);
        }

        // Hacer la tarjeta clickacble para ver el detalle de la tarea
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setOnMouseClicked(e -> abrirDetalleTarea(tarea));

        return card;
    }

    private void abrirDetalleTarea(Tarea tarea) {
        // Asegurarnos de que la tarea tenga el grupo asignado si viene null del JSON
        if (tarea.getGrupo() == null) {
            tarea.setGrupo(this.grupo);
        }

        try {
            java.net.URL fxmlUrl = onyx.escritorio.MainApplication.class
                    .getResource("/onyx/escritorio/tarea-detalle-view.fxml");
            if (fxmlUrl == null)
                throw new java.io.IOException("FXML resource not found: /onyx/escritorio/tarea-detalle-view.fxml");
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(fxmlUrl);
            javafx.scene.Node view = loader.load();

            TareaDetalleController controller = loader.getController();
            controller.setTarea(tarea);
            // Al volver, queremos restaurar la vista actual.
            controller.setOnBack(this::volverAGrupoDetalle);

            javafx.scene.layout.StackPane contentArea = null;
            if (onyx.escritorio.MainApplication.getPrimaryStage() != null
                    && onyx.escritorio.MainApplication.getPrimaryStage().getScene() != null) {
                contentArea = (javafx.scene.layout.StackPane) onyx.escritorio.MainApplication.getPrimaryStage()
                        .getScene().lookup(".content-area");
            }
            if (contentArea != null) {
                view.setOpacity(0);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);

                javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(
                        javafx.util.Duration.millis(300), view);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();
            }
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    }

    private void volverAGrupoDetalle() {
        try {
            java.net.URL fxmlUrl = onyx.escritorio.MainApplication.class
                    .getResource("/onyx/escritorio/grupo-detalle-view.fxml");
            if (fxmlUrl == null)
                throw new java.io.IOException("FXML resource not found: /onyx/escritorio/grupo-detalle-view.fxml");
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(fxmlUrl);
            javafx.scene.Node view = loader.load();

            GrupoDetalleController controller = loader.getController();
            controller.setGrupo(this.grupo);
            controller.setOnBack(this.onBack);

            javafx.scene.layout.StackPane contentArea = null;
            if (onyx.escritorio.MainApplication.getPrimaryStage() != null
                    && onyx.escritorio.MainApplication.getPrimaryStage().getScene() != null) {
                contentArea = (javafx.scene.layout.StackPane) onyx.escritorio.MainApplication.getPrimaryStage()
                        .getScene().lookup(".content-area");
            }
            if (contentArea != null) {
                view.setOpacity(0);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);

                javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(
                        javafx.util.Duration.millis(300), view);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();
            }
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleEditGroup() {
        if (grupo == null)
            return;
        onyx.escritorio.utils.DialogUtils.CreateGroupResult result = onyx.escritorio.utils.DialogUtils
                .showEditGroupDialog(grupo.getNombre(), grupo.getDescripcion());
        if (result.confirmed) {
            ApiClient.updateGrupo(grupo.getId(), result.name, result.description)
                    .thenAccept(success -> {
                        Platform.runLater(() -> {
                            if (success) {
                                onyx.escritorio.utils.DialogUtils.showSuccessDialog("Grupo actualizado",
                                        "Los datos del grupo se han actualizado correctamente.");
                                grupo.setNombre(result.name);
                                grupo.setDescripcion(result.description);
                                mostrarDatosGrupo();
                            } else {
                                onyx.escritorio.utils.DialogUtils.showErrorDialog("Error al actualizar el grupo.");
                            }
                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                            onyx.escritorio.utils.DialogUtils.showErrorDialog("Error al editar el grupo:\n" + msg);
                        });
                        return null;
                    });
        }
    }

    @FXML
    private void handleDeleteGroup() {
        if (grupo == null)
            return;

        boolean confirmed = onyx.escritorio.utils.DialogUtils.showDeleteConfirmationDialog("grupo");
        if (confirmed) {
            ApiClient.deleteGrupo(grupo.getId()).thenAccept(success -> {
                Platform.runLater(() -> {
                    if (success) {
                        onyx.escritorio.utils.DialogUtils.showSuccessDialog("Eliminado",
                                "El grupo ha sido eliminado correctamente.");
                        if (onBack != null) {
                            onBack.run();
                        }
                    } else {
                        onyx.escritorio.utils.DialogUtils.showErrorDialog("No se pudo eliminar el grupo.");
                    }
                });
            }).exceptionally(ex -> {
                Platform.runLater(() -> {
                    onyx.escritorio.utils.DialogUtils.showErrorDialog("Error de conexión al eliminar.");
                });
                return null;
            });
        }
    }
}
