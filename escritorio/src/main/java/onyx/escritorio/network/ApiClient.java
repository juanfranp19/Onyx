package onyx.escritorio.network;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import onyx.escritorio.models.Grupo;
import onyx.escritorio.utils.Session;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    public static CompletableFuture<List<Grupo>> getGruposUsuario(Integer userId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/grupos/usuario/" + userId))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return mapper.readValue(response.body(), new TypeReference<List<Grupo>>() {
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new ArrayList<>();
                        }
                    }
                    return new ArrayList<>();
                });
    }

    public static CompletableFuture<Boolean> login(String username, String password) {
        Map<String, String> data = Map.of(
                "nombreUsuario", username,
                "passwordHash", password);

        try {
            String json = mapper.writeValueAsString(data);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/usuarios/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            try {
                                JsonNode userNode = mapper.readTree(response.body());
                                Session.getInstance().setUser(
                                        userNode.get("id").asInt(),
                                        userNode.get("nombreUsuario").asText(),
                                        userNode.get("email").asText());
                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                        return false;
                    });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public static CompletableFuture<Boolean> register(String username, String email, String password) {
        Map<String, String> data = Map.of(
                "nombreUsuario", username,
                "email", email,
                "passwordHash", password);

        try {
            String json = mapper.writeValueAsString(data);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/usuarios"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> response.statusCode() == 200 || response.statusCode() == 201);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public static CompletableFuture<Boolean> createGrupo(String nombre, String descripcion, Integer creadorId) {
        Map<String, Object> data = Map.of(
                "nombre", nombre,
                "descripcion", descripcion,
                "creadorId", creadorId);

        try {
            String json = mapper.writeValueAsString(data);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/grupos"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200 || response.statusCode() == 201) {
                            return true;
                        } else {
                            System.out.println("Error creating group: " + response.body());
                            throw new RuntimeException("Error " + response.statusCode() + ": " + response.body());
                        }
                    });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public static CompletableFuture<List<onyx.escritorio.models.Tarea>> getTareasUsuario(Integer userId) {
        // Primero obtenemos los grupos del usuario, luego las tareas de cada grupo
        return getGruposUsuario(userId).thenCompose(grupos -> {
            if (grupos == null || grupos.isEmpty()) {
                return CompletableFuture.completedFuture(new ArrayList<>());
            }

            List<CompletableFuture<List<onyx.escritorio.models.Tarea>>> futures = new ArrayList<>();
            for (Grupo grupo : grupos) {
                futures.add(getTareasPorGrupo(grupo.getId()));
            }

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        List<onyx.escritorio.models.Tarea> allTareas = new ArrayList<>();
                        for (CompletableFuture<List<onyx.escritorio.models.Tarea>> f : futures) {
                            try {
                                allTareas.addAll(f.get());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return allTareas;
                    });
        });
    }

    private static CompletableFuture<List<onyx.escritorio.models.Tarea>> getTareasPorGrupo(Integer grupoId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tareas/grupo/" + grupoId))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return mapper.readValue(response.body(),
                                    new TypeReference<List<onyx.escritorio.models.Tarea>>() {
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new ArrayList<onyx.escritorio.models.Tarea>();
                        }
                    }
                    return new ArrayList<onyx.escritorio.models.Tarea>();
                });
    }

    public static CompletableFuture<Boolean> createTarea(String titulo, String descripcion,
            java.time.LocalDateTime fechaVencimiento, Integer grupoId, Integer creadorId) {

        Map<String, Object> data = new java.util.HashMap<>();
        data.put("titulo", titulo);
        data.put("descripcion", descripcion);
        data.put("grupo_id", grupoId);
        data.put("creador_id", creadorId);
        if (fechaVencimiento != null) {
            data.put("fechaVencimiento", fechaVencimiento.toString());
        }

        try {
            String json = mapper.writeValueAsString(data);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/tareas"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200 || response.statusCode() == 201) {
                            return true;
                        } else {
                            System.out.println("Error creating task: " + response.body());
                            throw new RuntimeException("Error " + response.statusCode() + ": " + response.body());
                        }
                    });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
