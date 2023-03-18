package br.com.gsolutions.productapi.entities;

public enum RoleType {
    ROLE_ADMIN(1L, "ROLE_ADMIN", "ADMIN"),
    ROLE_OPERATOR(2L, "ROLE_OPERATOR", "OPERATOR");

    private Long id;
    private String role;
    private String description;

    RoleType(Long id, String role, String description) {
        this.id = id;
        this.role = role;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getDescription() {
        return description;
    }


}
