package ru.fizteh.java2.podorozhnaya.filemap.cmdimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.Command;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.State;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;


public abstract class ShellState implements State {

    private InputStream in;
    private PrintStream out;
    private File currentDir;

    @Value("${ru.fizteh.java2.podorozhnaya.filemap.db.dir:D:/dir}")
    protected String path;

    public ShellState() throws IOException {
        this.in = System.in;
        this.out = System.out;
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
        try {
            setCurrentDir(file);
        } catch (IOException e) {
            //never thrown
        }
    }

    public InputStream getInputStream() {
        return in;
    }

    public PrintStream getOutputStream() {
        return out;
    }

    public File getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(File currentDir) throws IOException {
        this.currentDir = currentDir;
    }

    @Override
    public File getFileByName(String path) {
        File f = new File(path);
        if (f.isAbsolute()) {
            return f;
        } else {
            return new File(currentDir, path);
        }
    }

    @Override
    public int commitDif() throws IOException {
        return 0;
    }
}
