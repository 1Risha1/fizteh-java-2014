package ru.fizteh.java2.podorozhnaya.filemap.cmdimpl.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.AbstractCommand;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.State;

import java.io.File;
import java.io.IOException;


@Service("cd")
public class CommandChangeDirectory extends AbstractCommand {

    @Autowired
    private State state;

    CommandChangeDirectory() {
        super(1);
    }
    
    public void execute(String[] args) throws IOException {    
        File f = state.getFileByName(args[1]);
        if (!f.isDirectory()) {
            throw new IOException("cd: '" + args[1] + "' is not an exicting directory");
        } else {
            state.setCurrentDir(f);
        }
    }
}
