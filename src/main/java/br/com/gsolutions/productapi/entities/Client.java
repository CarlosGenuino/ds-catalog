package br.com.gsolutions.productapi.entities;

import br.com.gsolutions.productapi.dto.ClientDTO;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String cpf;

    private Double income;

    private Instant birthdate;

    private Integer children;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant updatedAt;

    public Client(ClientDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.cpf = dto.getCpf();
        this.income = dto.getIncome();
        this.birthdate = dto.getBirthdate();
        this.children = getChildren();
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
