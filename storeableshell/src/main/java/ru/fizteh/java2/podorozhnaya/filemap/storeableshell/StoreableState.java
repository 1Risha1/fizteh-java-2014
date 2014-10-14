package ru.fizteh.java2.podorozhnaya.filemap.storeableshell;

import ru.fizteh.java2.podorozhnaya.filemap.shellapi.State;

import java.io.IOException;
import java.util.List;

public interface StoreableState extends State {

    int commitDif() throws IOException;

    String getValue(String key) throws IOException;

    String removeValue(String key) throws IOException;

    String put(String key, String value) throws IOException;

    int getCurrentTableSize() throws IOException;

    int rollBack() throws IOException;

    void drop(String name) throws IOException;

    void create(String name, List<Class<?>> types) throws IOException;

    void use(String name) throws IOException;
}
