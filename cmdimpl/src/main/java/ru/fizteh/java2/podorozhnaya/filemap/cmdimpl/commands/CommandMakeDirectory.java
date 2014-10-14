package ru.fizteh.java2.podorozhnaya.filemap.cmdimpl.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.AbstractCommand;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.State;

import java.io.File;
import java.io.IOException;

@Service ("mkdir")
public class CommandMakeDirectory extends AbstractCommand {

    @Autowired
    private State state;
    
    public CommandMakeDirectory() {
        super(1);
    }
    
    public void execute(String[] args) throws IOException {
        File f = state.getFileByName(args[1]);
        if (!f.exists()) {
            f.mkdir();    
        } else {
            throw new IOException("mkdir: '" + args[1] + "' already exist");
        }
    }

}
