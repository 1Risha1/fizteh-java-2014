package ru.fizteh.java2.podorozhnaya.filemap.shellapi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public interface State {

    InputStream getInputStream();

    PrintStream getOutputStream();

    File getCurrentDir();

    void setCurrentDir(File currentDir) throws IOException;

    void checkAndExecute(String[] args) throws IOException;

    File getFileByName(String path);

    int commitDif() throws IOException;

}
