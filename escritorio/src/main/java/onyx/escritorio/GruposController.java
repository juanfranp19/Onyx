package onyx.escritorio;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import onyx.escritorio.network.ApiClient;
import onyx.escritorio.utils.DialogUtils;
import onyx.escritorio.utils.Session;
import onyx.escritorio.models.Grupo;
import java.util.List;

public class GruposController {

    @FXML
    private VBox emptyState;

    @FXML
    private ScrollPane contentList;

    private VBox groupsContainer;

    public void initialize() {
        if (contentList.getContent() instanceof VBox) {
            groupsContainer = (VBox) contentList.getContent();
        }
        cargarGrupos();
    }

    private void cargarGrupos() {
        Integer userId = Session.getInstance().getUserId();
        if (userId == null) return;

        ApiClient.getGruposUsuario(userId).thenAccept(grupos -> {
            Platform.runLater(() -> {
                if (grupos == null || grupos.isEmpty()) {
                    showEmptyState();
                } else {
                    showGruposList(grupos);
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

    private void showGruposList(List<Grupo> grupos) {
        emptyState.setVisible(false);
        emptyState.setManaged(false);
        contentList.setVisible(true);
        contentList.setManaged(true);
        
        groupsContainer.getChildren().clear();
        
        for (Grupo grupo : grupos) {
            groupsContainer.getChildren().add(createGrupoCard(grupo));
        }
    }

    private VBox createGrupoCard(Grupo grupo) {
        VBox card = new VBox(8);
        card.getStyleClass().add("content-card");
        card.setPadding(new Insets(20, 24, 20, 24));
        
        Label nombre = new Label(grupo.getNombre());
        nombre.getStyleClass().add("card-title");
        
        // Icono + Nombre
        HBox header = new HBox(12);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label icon = new Label("📂");
        icon.setStyle("-fx-font-size: 20px;");
        header.getChildren().addAll(icon, nombre);

        Label descripcion = new Label(grupo.getDescripcion());
        descripcion.getStyleClass().add("card-description");
        descripcion.setWrapText(true);

        card.getChildren().addAll(header, descripcion);
        
        return card;
    }

    @FXML
    private void handleCreateGroup() {
        DialogUtils.CreateGroupResult result = DialogUtils.showCreateGroupDialog();

        if (result.confirmed) {
            Integer userId = Session.getInstance().getUserId();
            if (userId == null) {
                showError("No hay sesión activa. Por favor, vuelve a iniciar sesión.");
                return;
            }

            ApiClient.createGrupo(result.name, result.description, userId)
                .thenAccept(success -> {
                    Platform.runLater(() -> {
                        if (success) {
                            showInfo("Grupo creado correctamente");
                            cargarGrupos(); // Recargar lista
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                        showError("Error al crear el grupo:\n" + msg);
                    });
                    return null;
                });
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
