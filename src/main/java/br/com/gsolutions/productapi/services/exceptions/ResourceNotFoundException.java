package br.com.gsolutions.productapi.services.exceptions;

public class ResourceNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 320778620846161426L;

    public ResourceNotFoundException(String message){
        super(message);
    }
}
