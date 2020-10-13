package br.com.gsolutions.productapi.services.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    public static final long serialVersionID=1L;

    public ResourceNotFoundException(String message){
        super(message);
    }
}
