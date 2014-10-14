package ru.fizteh.java2.podorozhnaya.filemap.storeabledb;

/**
 * Created by Ирина on 08.10.2014.
 */
public class ColumnFormatException extends IllegalArgumentException {

    public ColumnFormatException() {
    }

    public ColumnFormatException(String s) {
        super(s);
    }

    public ColumnFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ColumnFormatException(Throwable cause) {
        super(cause);
    }
}
