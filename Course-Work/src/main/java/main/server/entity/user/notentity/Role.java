package main.server.entity.user.notentity;

public enum Role {
    STUDENT,
    PROFESSOR,
    MANAGER;

    private String prefix = "ROLE_";

    Role() { }

    @Override
    public String toString() {
        return prefix + super.toString();
    }
}
