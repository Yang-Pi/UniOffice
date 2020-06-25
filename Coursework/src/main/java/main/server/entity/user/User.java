package main.server.entity.user;

import main.server.entity.user.notentity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "Users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user", nullable = false, unique = true)
    private String userName;

    @Column(name = "password", nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();

    public User() { }

    public User(String userName, String password, List<Role> roles) {
        this.userName = userName;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> sRoles = new ArrayList<>();
        for (Role role : roles) {
            sRoles.add(role.toString());
        }

        return sRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public List<Role> getRoles() {
        return roles;
    }

    public List<String> getSRoles() {
        List<String> sRoles = new ArrayList<>();
        for (Role role : roles) {
            sRoles.add(role.toString());
        }
        return sRoles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role newRole) {
        boolean isNewRole = true;
        for(Role role : roles) {
            if (newRole == role) {
                isNewRole = false;
            }
        }

        if (isNewRole) {
            roles.add(newRole);
        }
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
