package Autofill;

public class User {
    private String username;
    private String role;
    private String data;
    private String group;
    
    public User() {
        username = null;
        role = null;
        data = null;
        group = null;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getRole() {
        return this.role;
    }
    
    public void setData(String data) {
        this.data = data;
    } 
    
    public String getData() {
        return this.data;
    }
    
    public void setGroup(String group) {
        this.group = group;
    }
    
    public String getGroup() {
        return this.group;
    }
}
