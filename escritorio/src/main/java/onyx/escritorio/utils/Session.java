package onyx.escritorio.utils;

public class Session {
    private static Session instance;
    private String username;
    private String email;
    private Integer userId;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setUser(Integer id, String username, String email) {
        this.userId = id;
        this.username = username;
        this.email = email;
    }

    public void clear() {
        this.userId = null;
        this.username = null;
        this.email = null;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Integer getUserId() {
        return userId;
    }

    public boolean isLoggedIn() {
        return userId != null;
    }
}
