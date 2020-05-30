package main.server.web.authentication;

import main.server.entity.user.notentity.Role;

import java.io.Serializable;

public class AuthRequest implements Serializable {
    private String userName;
    private String password;
    private Role role;

    public AuthRequest() {  }

    public AuthRequest(String userName, String password, Role role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
