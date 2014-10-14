package ru.fizteh.java2.podorozhnaya.filemap.shellapi;

import java.io.IOException;

public interface Command {

    void execute(String[] args) throws IOException;

    int getNumberOfArguments();
}
