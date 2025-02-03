package br.com.gsolutions.productapi.dto;

import br.com.gsolutions.productapi.services.validation.UserUpdateValid;
import lombok.AllArgsConstructor;

@UserUpdateValid
@AllArgsConstructor
public class UserUpdateDTO extends UserDTO {
    public UserUpdateDTO(long id, String firstName, String lastName, String mail) {
        this.setId(id);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(mail);
    }
}
