package ru.fizteh.java2.podorozhnaya.filemap.shellapi;

public class ExitRuntimeException extends RuntimeException{

    public ExitRuntimeException() {
    }
    
    public ExitRuntimeException(String message) {
        super(message);
    }
    
    public ExitRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ExitRuntimeException(Throwable cause) {
        super(cause);
    }
}
