package com.vj.orm.exception;

public class DataException extends Exception {
    public DataException(String message) {
        super(message);
    }
    
    public DataException(String message, Throwable cause) {
        super(message, cause);
    }
}
