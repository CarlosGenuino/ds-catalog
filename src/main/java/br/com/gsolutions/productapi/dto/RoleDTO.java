package br.com.gsolutions.productapi.dto;

import br.com.gsolutions.productapi.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Long id;
    private String authority;

    public RoleDTO (Role entity){
        id = entity.getId();
        authority = entity.getAuthority();
    }

}
