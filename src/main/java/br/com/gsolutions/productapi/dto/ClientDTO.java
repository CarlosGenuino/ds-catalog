package br.com.gsolutions.productapi.dto;

import br.com.gsolutions.productapi.entities.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
	
    private Long id;

    private String name;

    private String cpf;

    private Double income;

    private Instant birthdate;

    private Integer children;

    public ClientDTO(Client entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.cpf = entity.getCpf();
        this.income = entity.getIncome();
        this.birthdate = entity.getBirthdate();
        this.children = entity.getChildren();
    }
}
