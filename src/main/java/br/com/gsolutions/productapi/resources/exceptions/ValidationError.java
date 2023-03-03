package br.com.gsolutions.productapi.resources.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ValidationError extends StandardError {

    private List<ErrorMessage> errors = new ArrayList<>();

    public void addError(String field, String message){
        this.errors.add(new ErrorMessage(field, message));
    }
}
