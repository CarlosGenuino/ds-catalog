package br.com.gsolutions.productapi.services.exceptions;

public class DatabaseException extends RuntimeException{
    public static final long serialVersionID=1L;

    public DatabaseException(String message){
        super(message);
    }
}
