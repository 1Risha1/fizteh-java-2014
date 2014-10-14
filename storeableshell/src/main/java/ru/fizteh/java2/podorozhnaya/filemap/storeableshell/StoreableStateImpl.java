package ru.fizteh.java2.podorozhnaya.filemap.storeableshell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.fizteh.java2.podorozhnaya.filemap.cmdimpl.ShellState;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.*;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Component
public class StoreableStateImpl extends ShellState implements StoreableState {

    private Table workingTable;
    private TableProvider provider;

    @Autowired
    private TableProviderFactory factory;

    public StoreableStateImpl() throws IOException {
    }

    @PostConstruct
    private void init() throws IOException {
        if (path == null) {
            throw new IOException("can't get property");
        }
        File file = new File(path);

        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IOException("can't create directory");
            }
        }
        provider = factory.create(path);

        try {
            setCurrentDir(file);
        } catch (IOException e) {
            //never thrown
        }
    }


    @Override
    public String getValue(String key) throws IOException {
        if (workingTable == null) {
            throw new IOException("no table");
        }

        return provider.serialize(workingTable, workingTable.get(key));
    }

    @Override
    public String removeValue(String key) throws IOException {
        if (workingTable == null) {
            throw new IOException("no table");
        }

        return provider.serialize(workingTable, workingTable.remove(key));
    }

    @Override
    public String put(String key, String value) throws IOException {
        if (workingTable == null) {
            throw new IOException("no table");
        }
        try {
            Storeable val = provider.deserialize(workingTable, value);
            return provider.serialize(workingTable, workingTable.put(key, val));
        } catch (ColumnFormatException | ParseException e) {
            throw new IOException("wrong type " + e.getMessage());
        }
    }

    @Override
    public int commitDif() throws IOException {
        if (workingTable != null) {
            return workingTable.commit();
        }
        return 0;
    }

    @Override
    public int getCurrentTableSize() throws IOException {
        if (workingTable != null) {
            return workingTable.size();
        } else {
            throw new IOException("no table");
        }
    }

    @Override
    public int rollBack() throws IOException {
        if (workingTable != null) {
            return workingTable.rollback();
        } else {
            throw new IOException("no table");
        }
    }

    @Override
    public void drop(String name) throws IOException {
        if (provider.getTable(name) == workingTable) {
            workingTable = null;
        }
        provider.removeTable(name);
    }

    @Override
    public void create(String name, List<Class<?>> columnType) throws IOException {
        try {
            if (provider.createTable(name, columnType) == null) {
                throw new IOException(name + " exists");
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("wrong type" + e.getMessage());
        }
    }

    @Override
    public void use(String name) throws IOException {
        try {
            Table table = provider.getTable(name);
            if (table == null) {
                throw new IOException(name + " not exists");
            }
            if (workingTable != null) {
                int n = workingTable.getChangedValuesNumber();
                if (n != 0) {
                    throw new IOException(n + " unsaved changes");
                }
            }
            this.workingTable = table;
        } catch (IllegalArgumentException e) {
            throw new IOException("illegal table name");
        }
    }
}
