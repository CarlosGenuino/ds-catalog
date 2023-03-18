package br.com.gsolutions.productapi.services.validation;

import br.com.gsolutions.productapi.dto.UserUpdateDTO;
import br.com.gsolutions.productapi.resources.exceptions.ErrorMessage;
import br.com.gsolutions.productapi.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;

@AllArgsConstructor
public class UserUpdateValidor implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

    private final UserService service;

    private final HttpServletRequest request;

    @Override
    public void initialize(UserUpdateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(UserUpdateDTO userUpdateDTO, ConstraintValidatorContext constraintValidatorContext) {
        List<ErrorMessage> errors = new ArrayList<>();

        Map<String, String> attribute = (Map<String, String>) request.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        var strId = attribute.get("id");
        long userId = 0L;
        if (strId != null && !strId.isEmpty() && !strId.isBlank()){
          userId = Long.parseLong(strId);
        }

        if (userId == 0){
            errors.add(new ErrorMessage("id", "invalid id"));
        }

        var user = service.findByEmail(userUpdateDTO.getEmail());
        if (user.isPresent()){
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
