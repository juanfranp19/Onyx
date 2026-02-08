package onyx.escritorio.models; // Ajusta a tu paquete del cliente

import javafx.beans.property.*;
import java.time.LocalDate;

public class Tarea {

    // Usamos Properties de JavaFX en lugar de tipos nativos
    private final IntegerProperty id;
    private final StringProperty titulo;
    private final StringProperty descripcion;
    private final ObjectProperty<LocalDate> fechaVencimiento;

    // Asumimos que tienes modelos Usuario y Grupo en el cliente también
    // Si solo te interesan los IDs, cámbialos a IntegerProperty
    private final ObjectProperty<Usuario> creador;
    private final ObjectProperty<Grupo> grupo;

    // Constructor vacío (Necesario para Jackson/Gson al recibir JSON)
    public Tarea() {
        this(null, "", "", null, null, null);
    }

    // Constructor completo
    public Tarea(Integer id, String titulo, String descripcion, LocalDate fechaVencimiento, Usuario creador, Grupo grupo) {
        this.id = new SimpleIntegerProperty(id != null ? id : 0);
        this.titulo = new SimpleStringProperty(titulo);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.fechaVencimiento = new SimpleObjectProperty<>(fechaVencimiento);
        this.creador = new SimpleObjectProperty<>(creador);
        this.grupo = new SimpleObjectProperty<>(grupo);
    }

    // --- Getters y Setters (Estilo JavaFX) ---

    // ID
    public Integer getId() { return id.get(); }
    public void setId(Integer id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    // TITULO
    public String getTitulo() { return titulo.get(); }
    public void setTitulo(String titulo) { this.titulo.set(titulo); }
    public StringProperty tituloProperty() { return titulo; }

    // DESCRIPCION
    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }
    public StringProperty descripcionProperty() { return descripcion; }

    // FECHA VENCIMIENTO
    public LocalDate getFechaVencimiento() { return fechaVencimiento.get(); }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento.set(fechaVencimiento); }
    public ObjectProperty<LocalDate> fechaVencimientoProperty() { return fechaVencimiento; }

    // CREADOR
    public Usuario getCreador() { return creador.get(); }
    public void setCreador(Usuario creador) { this.creador.set(creador); }
    public ObjectProperty<Usuario> creadorProperty() { return creador; }

    // GRUPO
    public Grupo getGrupo() { return grupo.get(); }
    public void setGrupo(Grupo grupo) { this.grupo.set(grupo); }
    public ObjectProperty<Grupo> grupoProperty() { return grupo; }

    @Override
    public String toString() {
        return getTitulo(); // Útil si pones el objeto directamente en un ComboBox
    }
}