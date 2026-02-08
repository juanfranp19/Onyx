package onyx.escritorio;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import onyx.escritorio.network.ApiClient;
import onyx.escritorio.utils.DialogUtils;
import onyx.escritorio.utils.Session;
import onyx.escritorio.models.Tarea;
import onyx.escritorio.models.Grupo;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TareasController {

    @FXML
    private VBox emptyState;

    @FXML
    private ScrollPane contentList;

    @FXML
    private VBox tareasContainer;

    private List<Grupo> gruposUsuario;

    public void initialize() {
        cargarTareas();
    }

    private void cargarTareas() {
        Integer userId = Session.getInstance().getUserId();
        if (userId == null)
            return;

        // Primero cargar los grupos del usuario para el diálogo
        ApiClient.getGruposUsuario(userId).thenAccept(grupos -> {
            this.gruposUsuario = grupos;
        });

        // Luego cargar las tareas
        ApiClient.getTareasUsuario(userId).thenAccept(tareas -> {
            Platform.runLater(() -> {
                if (tareas == null || tareas.isEmpty()) {
                    showEmptyState();
                } else {
                    showTareasList(tareas);
                }
            });
        });
    }

    private void showEmptyState() {
        emptyState.setVisible(true);
        emptyState.setManaged(true);
        contentList.setVisible(false);
        contentList.setManaged(false);
    }

    private void showTareasList(List<Tarea> tareas) {
        emptyState.setVisible(false);
        emptyState.setManaged(false);
        contentList.setVisible(true);
        contentList.setManaged(true);

        tareasContainer.getChildren().clear();

        for (Tarea tarea : tareas) {
            tareasContainer.getChildren().add(createTareaCard(tarea));
        }
    }

    private VBox createTareaCard(Tarea tarea) {
        VBox card = new VBox(8);
        card.getStyleClass().add("content-card");
        card.setPadding(new Insets(20, 24, 20, 24));

        Label titulo = new Label(tarea.getTitulo());
        titulo.getStyleClass().add("card-title");

        // Icono + Título
        HBox header = new HBox(12);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label icon = new Label("📋");
        icon.setStyle("-fx-font-size: 20px;");
        header.getChildren().addAll(icon, titulo);

        Label descripcion = new Label(tarea.getDescripcion());
        descripcion.getStyleClass().add("card-description");
        descripcion.setWrapText(true);

        card.getChildren().addAll(header, descripcion);

        // Fecha de vencimiento si existe
        if (tarea.getFechaVencimiento() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            Label fecha = new Label("📅 Vence: " + tarea.getFechaVencimiento().format(formatter));
            fecha.getStyleClass().add("card-date");
            card.getChildren().add(fecha);
        }

        return card;
    }

    @FXML
    private void handleCreateTask() {
        if (gruposUsuario == null || gruposUsuario.isEmpty()) {
            showError("Primero debes pertenecer a un grupo para crear tareas.");
            return;
        }

        DialogUtils.CreateTaskResult result = DialogUtils.showCreateTaskDialog(gruposUsuario);

        if (result.confirmed) {
            Integer userId = Session.getInstance().getUserId();
            if (userId == null) {
                showError("No hay sesión activa. Por favor, vuelve a iniciar sesión.");
                return;
            }

            ApiClient.createTarea(result.titulo, result.descripcion, result.fechaVencimiento, result.grupoId, userId)
                    .thenAccept(success -> {
                        Platform.runLater(() -> {
                            if (success) {
                                showInfo("Tarea creada correctamente");
                                cargarTareas(); // Recargar lista
                            }
                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                            showError("Error al crear la tarea:\n" + msg);
                        });
                        return null;
                    });
        }
    }

    private void showError(String message) {
        DialogUtils.showErrorDialog(message);
    }

    private void showInfo(String message) {
        DialogUtils.showSuccessDialog("Tarea creada", message);
    }
}
