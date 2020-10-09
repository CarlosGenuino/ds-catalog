package br.com.gsolutions.productapi.services.exceptions;

public class EntityNotFoundException extends RuntimeException{
    public static final long serialVersionID=1L;

    public EntityNotFoundException(String message){
        super(message);
    }
}
