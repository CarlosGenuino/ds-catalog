package br.com.gsolutions.productapi.entities;

import br.com.gsolutions.productapi.dto.CategoryDTO;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;


@Entity
public class Category implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Category() {
    }

    public Category(CategoryDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
    }

    @PrePersist
    public void prePersist(){
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.updatedAt = Instant.now();
    }
}
