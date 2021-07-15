package br.com.gsolutions.productapi.services.exceptions;

public class ResourceNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 320778620846161426L;

	public static final long serialVersionID=1L;

    public ResourceNotFoundException(String message){
        super(message);
    }
}
