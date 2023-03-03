package br.com.gsolutions.productapi.dto;

import br.com.gsolutions.productapi.services.validation.UserInsertValid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@UserInsertValid
public class UserInsertDTO extends UserDTO {

    @NotNull
    private String password;

}
