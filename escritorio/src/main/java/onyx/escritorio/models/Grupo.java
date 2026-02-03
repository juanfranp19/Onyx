package onyx.escritorio.models;

public class Grupo {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer creadorId;
    private String fechaCreacion;

    public Grupo() {}

    public Grupo(Integer id, String nombre, String descripcion, Integer creadorId, String fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.creadorId = creadorId;
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getCreadorId() { return creadorId; }
    public void setCreadorId(Integer creadorId) { this.creadorId = creadorId; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
