package br.com.gsolutions.productapi.services.exceptions;

public class DatabaseException extends RuntimeException{


	private static final long serialVersionUID = 6568473851118649823L;
	
	public static final long serialVersionID=1L;

    public DatabaseException(String message){
        super(message);
    }
}
