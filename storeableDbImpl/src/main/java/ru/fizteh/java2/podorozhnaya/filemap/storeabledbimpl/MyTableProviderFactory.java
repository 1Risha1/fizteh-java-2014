package ru.fizteh.java2.podorozhnaya.filemap.storeabledbimpl;

import org.springframework.stereotype.Component;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.TableProvider;
import ru.fizteh.java2.podorozhnaya.filemap.storeabledb.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class MyTableProviderFactory implements TableProviderFactory, AutoCloseable {

    public static final String LEGAL_NAME = "[^*?\"<>|]+";
    private Set<TableProvider> providers = new HashSet<>();
    private volatile boolean isClosed = false;

    @Override
    public TableProvider create(String dataBaseDir) throws IOException {
        if (isClosed) {
            throw new IllegalStateException("access to closed object");
        }

        if (dataBaseDir == null || dataBaseDir.trim().isEmpty() || !dataBaseDir.matches(LEGAL_NAME)) {
           throw new IllegalArgumentException("dir not defined or has illegal name");
        }
        
        File directory = new File(dataBaseDir);
        
        if (!directory.exists()) {
            throw new IOException(dataBaseDir + " directory not exists");
        } else if (!directory.isDirectory()) {
            throw new IllegalArgumentException(dataBaseDir + " not a directory");    
        }
        TableProvider provider = new MyTableProvider(directory);
        providers.add(provider);
        return provider;
    }

    @Override
    public void close() {
        if (!isClosed) {
            for (TableProvider provider: providers) {
                provider.close();
            }
            isClosed = true;
        }
    }
}
