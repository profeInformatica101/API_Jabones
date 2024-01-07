package com.dwes.api.errores;


public class JabonNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

	public JabonNotFoundException(String message) {
        super(message);
    }
}