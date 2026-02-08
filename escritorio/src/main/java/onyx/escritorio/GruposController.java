package onyx.escritorio;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import onyx.escritorio.network.ApiClient;
import onyx.escritorio.utils.DialogUtils;
import onyx.escritorio.utils.Session;
import onyx.escritorio.models.Grupo;

import java.io.IOException;
import java.util.List;

public class GruposController {

    @FXML
    private VBox emptyState;

    @FXML
    private ScrollPane contentList;

    @FXML
    private VBox groupsContainer;

    public void initialize() {
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

        card.setCursor(javafx.scene.Cursor.HAND);
        card.setOnMouseClicked(e -> abrirDetalleGrupo(grupo));

        return card;
    }

    private void abrirDetalleGrupo(Grupo grupo) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("grupo-detalle-view.fxml"));
            Node view = loader.load();

            GrupoDetalleController controller = loader.getController();
            controller.setGrupo(grupo);
            controller.setOnBack(this::volverAGrupos);

            StackPane contentArea = (StackPane) emptyState.getScene().lookup(".content-area");
            if (contentArea != null) {
                view.setOpacity(0);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);

                FadeTransition fade = new FadeTransition(Duration.millis(300), view);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void volverAGrupos() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("grupos-view.fxml"));
            Node view = loader.load();

            StackPane contentArea = (StackPane) emptyState.getScene().lookup(".content-area");
            if (contentArea != null) {
                view.setOpacity(0);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);

                FadeTransition fade = new FadeTransition(Duration.millis(300), view);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
        DialogUtils.showErrorDialog(message);
    }
    
    private void showInfo(String message) {
        DialogUtils.showSuccessDialog("Grupo creado", message);
    }
}
