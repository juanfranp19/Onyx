package onyx.escritorio.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import onyx.escritorio.models.Tarea;

import java.time.format.DateTimeFormatter;

public class TareaDetalleController {

    @FXML
    private Label lblTaskName;

    @FXML
    private Label lblTaskDescription;

    @FXML
    private Label lblTaskDueDate;

    @FXML
    private Label lblTaskGroup;

    private Tarea tarea;
    private Runnable onBack;

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
        mostrarDatosTarea();
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

    @FXML
    private javafx.scene.control.CheckBox chkCompletada;

    private void mostrarDatosTarea() {
        lblTaskName.setText(tarea.getTitulo());

        String desc = tarea.getDescripcion();
        if (desc != null && !desc.isBlank()) {
            lblTaskDescription.setText(desc);
        } else {
            lblTaskDescription.setText("Sin descripción");
            lblTaskDescription.setStyle("-fx-opacity: 0.5; -fx-font-style: italic;");
        }

        if (tarea.getFechaVencimiento() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            lblTaskDueDate.setText("Vence el " + tarea.getFechaVencimiento().format(formatter));
        } else {
            lblTaskDueDate.setText("Sin fecha de vencimiento");
        }

        if (tarea.getGrupo() != null) {
            lblTaskGroup.setText("Grupo: " + tarea.getGrupo().getNombre());
        } else {
            lblTaskGroup.setText("Sin grupo asociado");
        }

        // Avoid firing event during programmatic set
        chkCompletada.setOnAction(null);
        chkCompletada.setSelected(Boolean.TRUE.equals(tarea.getCompletada()));
        chkCompletada.setOnAction(e -> handleCheckCompletada());

        applyCompletedStyle();
    }

    private void applyCompletedStyle() {
        if (Boolean.TRUE.equals(tarea.getCompletada())) {
            lblTaskName.setStyle("-fx-text-fill: #888888; -fx-strikethrough: true;");
        } else {
            lblTaskName.setStyle("");
        }
    }

    @FXML
    private void handleCheckCompletada() {
        if (tarea == null)
            return;
        boolean isCompleted = chkCompletada.isSelected();

        onyx.escritorio.network.ApiClient.updateTareaCompletada(tarea.getId(), isCompleted)
                .thenAccept(success -> {
                    javafx.application.Platform.runLater(() -> {
                        if (success) {
                            tarea.setCompletada(isCompleted);
                            applyCompletedStyle();
                        } else {
                            chkCompletada.setSelected(!isCompleted); // Revert
                            onyx.escritorio.utils.DialogUtils
                                    .showErrorDialog("No se pudo actualizar el estado de la tarea.");
                        }
                    });
                })
                .exceptionally(ex -> {
                    javafx.application.Platform.runLater(() -> {
                        chkCompletada.setSelected(!isCompleted); // Revert
                        onyx.escritorio.utils.DialogUtils.showErrorDialog("Error de conexión al guardar el estado.");
                    });
                    return null;
                });
    }

    @FXML
    private void handleEditTask() {
        if (tarea == null)
            return;
        Integer userId = onyx.escritorio.utils.Session.getInstance().getUserId();
        if (userId == null)
            return;

        onyx.escritorio.network.ApiClient.getGruposUsuario(userId).thenAccept(grupos -> {
            javafx.application.Platform.runLater(() -> {
                java.time.LocalDateTime currentLD = tarea.getFechaVencimiento() != null
                        ? tarea.getFechaVencimiento().atStartOfDay()
                        : null;
                Integer currentGrupoId = tarea.getGrupo() != null ? tarea.getGrupo().getId() : null;

                onyx.escritorio.utils.DialogUtils.CreateTaskResult result = onyx.escritorio.utils.DialogUtils
                        .showEditTaskDialog(
                                tarea.getTitulo(), tarea.getDescripcion(), currentLD, currentGrupoId, grupos);

                if (result.confirmed) {
                    onyx.escritorio.network.ApiClient
                            .updateTarea(tarea.getId(), result.titulo, result.descripcion, result.fechaVencimiento,
                                    result.grupoId)
                            .thenAccept(success -> {
                                javafx.application.Platform.runLater(() -> {
                                    if (success) {
                                        onyx.escritorio.utils.DialogUtils.showSuccessDialog("Tarea actualizada",
                                                "La tarea se ha actualizado correctamente.");
                                        tarea.setTitulo(result.titulo);
                                        tarea.setDescripcion(result.descripcion);
                                        if (result.fechaVencimiento != null) {
                                            tarea.setFechaVencimiento(result.fechaVencimiento.toLocalDate());
                                        } else {
                                            tarea.setFechaVencimiento(null);
                                        }
                                        for (onyx.escritorio.models.Grupo g : grupos) {
                                            if (g.getId().equals(result.grupoId)) {
                                                tarea.setGrupo(g);
                                                break;
                                            }
                                        }
                                        mostrarDatosTarea();
                                    } else {
                                        onyx.escritorio.utils.DialogUtils
                                                .showErrorDialog("Error al actualizar la tarea.");
                                    }
                                });
                            })
                            .exceptionally(ex -> {
                                javafx.application.Platform.runLater(() -> {
                                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                                    onyx.escritorio.utils.DialogUtils
                                            .showErrorDialog("Error al editar la tarea:\n" + msg);
                                });
                                return null;
                            });
                }
            });
        });
    }

    @FXML
    private void handleDeleteTask() {
        if (tarea == null)
            return;

        boolean confirmed = onyx.escritorio.utils.DialogUtils.showDeleteConfirmationDialog("tarea");
        if (confirmed) {
            onyx.escritorio.network.ApiClient.deleteTarea(tarea.getId()).thenAccept(success -> {
                javafx.application.Platform.runLater(() -> {
                    if (success) {
                        onyx.escritorio.utils.DialogUtils.showSuccessDialog("Eliminada",
                                "La tarea ha sido eliminada correctamente.");
                        if (onBack != null) {
                            onBack.run();
                        }
                    } else {
                        onyx.escritorio.utils.DialogUtils.showErrorDialog("No se pudo eliminar la tarea.");
                    }
                });
            }).exceptionally(ex -> {
                javafx.application.Platform.runLater(() -> {
                    onyx.escritorio.utils.DialogUtils.showErrorDialog("Error de conexión al eliminar.");
                });
                return null;
            });
        }
    }
}
