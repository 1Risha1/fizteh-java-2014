package ru.fizteh.java2.podorozhnaya.filemap.storeabledbimpl;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.fizteh.java2.podorozhnaya.filemap.utils.Types;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.ColumnFormatException;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.Storeable;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.Table;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.TableProvider;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTableProvider implements TableProvider, AutoCloseable {

    private static final Logger TableProviderLogger = LoggerFactory.getLogger(TableProvider.class);
    private final File dataBaseDir;
    private final Map<String, Table> tables = new HashMap<>();
    private static final String STRING_NAME_FORMAT = "[a-zA-Z0-9_]+";
    private volatile boolean isClosed = false;

    private void checkClosed() {
        if (isClosed) {
            TableProviderLogger.error("call for closed object");
            throw new IllegalStateException("call for closed object");
        }
    }

    public MyTableProvider(File dataBaseDir) throws IOException {
        this.dataBaseDir = dataBaseDir;
        for (String tableName: dataBaseDir.list()) {
            tables.put(tableName, new MyTable(tableName, dataBaseDir, this));
      }
        TableProviderLogger.info("Provider Started");
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + dataBaseDir.getAbsolutePath() + "]";
    }

    @Override
    public synchronized Table getTable(String name) {
        TableProviderLogger.info("get table " + name);
        checkClosed();
        checkCorrectName(name);
        try {
            Table table = tables.get(name);
            if (table != null && table.isClosed()) {
                Table newTable = new MyTable(name, dataBaseDir, this);
                tables.put(name, newTable);
            }
            return tables.get(name);
        } catch (IOException e)  {
            TableProviderLogger.error("incorrect table name");
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public synchronized Table createTable(String name, List<Class<?>> columnTypes)
            throws IOException {

        checkClosed();
        checkCorrectName(name);
        if (columnTypes == null || columnTypes.isEmpty()) {
            TableProviderLogger.error("in table " + name + " bad column list");
            throw new IllegalArgumentException("bad column list");
        }
        File table = new File(dataBaseDir, name);
        if (table.isDirectory()) {
            return null;
        }
        if (!table.mkdir()) {
            TableProviderLogger.error(name + ": table has illegal name");
            throw new IllegalArgumentException("table has illegal name");
        }

        try (PrintStream signature = new PrintStream(new File(table, "signature.tsv"))) {
            boolean isFirst = true;
            for (Class<?> s : columnTypes) {
                if (!isFirst) {
                    signature.print(" ");
                } else {
                    isFirst = false;
                }
                signature.print(Types.getSimpleName(s));
            }
        }

        try (PrintStream sizePrint = new PrintStream(new File(table, "size.tsv"))) {
            sizePrint.print(0);
        }

        Table newTable = new MyTable(name, dataBaseDir, this, columnTypes);
        tables.put(name, newTable);
        TableProviderLogger.info(name + ": table created");
        return newTable;
   }

    @Override
    public synchronized void removeTable(String name) throws IOException {

        checkClosed();
        checkCorrectName(name);
        if (tables.remove(name) == null) {
            TableProviderLogger.error("can't remove " + name + " not exists");
            throw new IllegalStateException(name + " not exists");
        }
        File table = new File(dataBaseDir, name);
        FileUtils.deleteDirectory(table);
        TableProviderLogger.info(name + ": table removed");

    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {

        checkClosed();
        Storeable res;
        try {
            res = XMLSerializer.deserialize(table, value);
        } catch (XMLStreamException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        return res;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {

        checkClosed();
        String res;
        try {            
            res = XMLSerializer.serialize(table, value);
        } catch (XMLStreamException e) {
            throw new ColumnFormatException(e);
        }
        return res;
    }

    @Override
    public Storeable createFor(Table table) {
        checkClosed();
        if (table == null) {
            throw new IllegalArgumentException("table can't be null");
        }
        return new MyStoreable(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) 
                     throws ColumnFormatException, IndexOutOfBoundsException {
        checkClosed();
        if (table == null || values == null) {
            throw new IllegalArgumentException("table and values can't be null");
        }
        int size = table.getColumnsCount();
        if (size != values.size()) {
            throw new IndexOutOfBoundsException();
        }
        
        Storeable res = createFor(table);
        for (int i = 0; i < size; ++i) {
            res.setColumnAt(i, values.get(i));
        }
        return res;
    }
    
    public static void checkCorrectName(String name) {
        if (name == null || !name.matches(STRING_NAME_FORMAT)) {
            throw new IllegalArgumentException("table name is null or has illegal name");
        }
    }

    @Override
    public void close() {
        if (!isClosed) {
            for (Table table: tables.values()) {
                table.close();
            }
            isClosed = true;
        }
    }
}
