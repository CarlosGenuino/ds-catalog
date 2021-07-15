package br.com.gsolutions.productapi.entities;

import br.com.gsolutions.productapi.dto.CategoryDTO;
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
public class Category implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4416784870191430977L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant createdAt;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant updatedAt;

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
