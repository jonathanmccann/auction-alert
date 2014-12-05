package com.app.exception;

/**
 * @author Jonathan McCann
 */
public class DatabaseConnectionException extends Exception {

	public DatabaseConnectionException() {
	}

	public DatabaseConnectionException(String msg) {
		super(msg);
	}

	public DatabaseConnectionException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DatabaseConnectionException(Throwable cause) {
		super(cause);
	}

}