package ru.fizteh.java2.podorozhnaya.filemap.storeabledbimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.fizteh.java2.podorozhnaya.filemap.utils.Types;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.ColumnFormatException;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.Storeable;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.Table;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.TableProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

public class MyTable extends GenericTable<Storeable> implements Table {

    private static final Logger TableLog = LoggerFactory.getLogger(MyTable.class);

    private final List<Class<?>> columnType;
    private final TableProvider provider;
    private volatile boolean isClosed = false;

    public MyTable(String name, File rootDir, TableProvider provider) throws IOException {
        super(name, rootDir);
        columnType = readSignature();
        this.provider = provider;
        TableLog.info("Table starts");
    }

    public MyTable(String name, File rootDir, TableProvider provider, List<Class<?>> columnType) throws IOException {
        super(name, rootDir);
        this.columnType = new ArrayList<>();
        this.columnType.addAll(columnType);
        this.provider = provider;
        TableLog.info("Table starts");
    }

    private void checkClosed() {
        if (isClosed) {
            TableLog.error(" call for closed object");
            throw new IllegalStateException("call for closed object");
        }
    }

    @Override
    public void close() {
        if (!isClosed) {
            rollback();
            isClosed = true;
        }

        TableLog.info("Table closed");
    }

    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + tableDirectory.getAbsolutePath() + "]";
    }

    private List<Class<?>> readSignature() throws IOException {
        List<Class<?>> columns = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(tableDirectory, "signature.tsv"))) {
            while (sc.hasNext()) {
                columns.add(Types.getTypeByName(sc.next()));
            }
        } catch (FileNotFoundException e) {
            TableLog.error(getName() + ": signature file not found");
            throw new IOException(getName() + ": signature file not found");
        }

        if (columns.isEmpty()) {
            TableLog.error(getName() + "empty signature");
            throw new IOException("empty signature");
        }
        return columns;        
    }

    @Override
    public int commit() throws IOException {
        checkClosed();
        TableLog.info("commit to: " + this.getName());
        return super.commit();
    }

    public int rollback() {
        checkClosed();
        TableLog.info("rollback in: " + getName());
        return super.rollback();
    }

    @Override
    public Storeable get(String key) {
        checkClosed();
        return super.get(key);
    }

    @Override
    public String getName() {
        checkClosed();
        return super.getName();
    }

    @Override
    public Storeable remove(String key) {
        checkClosed();
        TableLog.info("remove " + key + " from " + getName());
        return super.remove(key);
    }

    @Override
    public int size() {
        checkClosed();
        return super.size();
    }

    @Override
    protected boolean checkEquals(Storeable val1, Storeable val2) {
        if (val1 == null && val2 == null) {
            return true;
        }
        return val1 != null && val2 != null && provider.serialize(this, val1).equals(provider.serialize(this, val2));

    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        checkClosed();
        if (value == null || key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("null argument in put");
        }

        int sizeColumn = columnType.size();

        try {
            for (int i = 0; i < sizeColumn; ++i) {
                Object valueI = value.getColumnAt(i);
                if (valueI != null && valueI.getClass() != columnType.get(i)) {
                TableLog.error(getName() + " " + i + " column has incorrect format");
                    throw new ColumnFormatException(i + " column has incorrect format");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            TableLog.error(getName()  + " column has incorrect format");
            throw new ColumnFormatException("alien ru.fizteh.java2.podorozhnaya.database.filemap.Storeable");
        }

        try {
            value.getColumnAt(sizeColumn);
            TableLog.error(getName() + " column has incorrect format");
            throw new ColumnFormatException("alien ru.fizteh.java2.podorozhnaya.database.filemap.Storeable");
        } catch (IndexOutOfBoundsException e) {
            TableLog.info("put " + " key " + " to " + getName());
            return super.put(key, value);
        }
     }

    @Override
    public int getColumnsCount() {
        checkClosed();
        return columnType.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkClosed();
        return columnType.get(columnIndex);
    }

    @Override
    protected Map<String, String> serialize(Map<String, Storeable> values) {
        checkClosed();
        if (values == null) {
            return null;
        }
        Map<String, String> value = new HashMap<>();
        Set<Entry<String, Storeable>> t = values.entrySet();
        for (Entry<String, Storeable> k: t) {
            value.put(k.getKey(), provider.serialize(this, k.getValue()));
        }
        return value;
    }

    @Override
    protected Map<String, Storeable> deserialize(Map<String, String> values) throws IOException {
        checkClosed();
        if (values == null) {
            return null;
        }
        Map<String, Storeable> value = new HashMap<>();
        Set<Entry<String, String>> t = values.entrySet();
        for (Entry<String, String> k: t) {
            try { 
                value.put(k.getKey(), provider.deserialize(this, k.getValue()));
            } catch (ParseException e) {
                throw new IOException(e);
            }            
        }
        return value;
    }
}
