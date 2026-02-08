package onyx.escritorio;

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
        if (grupo == null || grupo.getId() == null) return;

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

        Label icon = new Label("📋");
        icon.setStyle("-fx-font-size: 16px;");

        Label titulo = new Label(tarea.getTitulo());
        titulo.getStyleClass().add("detail-tarea-title");

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

        return card;
    }
}
