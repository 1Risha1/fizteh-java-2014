package ru.fizteh.java2.podorozhnaya.filemap.cmdimpl.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.AbstractCommand;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.State;

import java.io.File;
import java.io.IOException;

@Service("mv")
public class CommandMove extends AbstractCommand {

    @Autowired
    private State state;
    public CommandMove() {
        super(2);
    }

    public void execute(String[] args) throws IOException {    
        File source = state.getFileByName(args[1]);
        File dest = state.getFileByName(args[2]);
        if (!source.exists()) {
            throw new IOException("mv: '" + args[1] + "' not exist");
        } else if (dest.isDirectory()) {
            if (!source.renameTo(new File(dest + File.separator + source.getName()))) {
                throw new IOException("mv: '" + source.getName() + "' can't move file");
            }
        } else if (!source.renameTo(dest)) {
            throw new IOException("mv: '" + source.getName() + "' can't move file");
        }        
    }
}
