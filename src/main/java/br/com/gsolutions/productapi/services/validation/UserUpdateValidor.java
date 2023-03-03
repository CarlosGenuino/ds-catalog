package br.com.gsolutions.productapi.services.validation;

import br.com.gsolutions.productapi.dto.UserInsertDTO;
import br.com.gsolutions.productapi.resources.exceptions.ErrorMessage;
import br.com.gsolutions.productapi.services.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class UserUpdateValidor implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

    @Autowired
    private UserService service;

    @Override
    public void initialize(UserInsertValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(UserInsertDTO userInsertDTO, ConstraintValidatorContext constraintValidatorContext) {
        List<ErrorMessage> errors = new ArrayList<>();
        var user = service.findByEmail(userInsertDTO.getEmail());
        if (user != null){
            errors.add(new ErrorMessage("email", "email already inserted"));
        }

        for (ErrorMessage e: errors){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(e.getMessage())
                    .addPropertyNode(e.getField()).addConstraintViolation();
        }
        return errors.isEmpty();
    }
}
