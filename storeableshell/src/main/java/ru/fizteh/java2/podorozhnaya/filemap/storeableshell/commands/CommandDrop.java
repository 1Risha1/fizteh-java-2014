package ru.fizteh.java2.podorozhnaya.filemap.storeableshell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.AbstractCommand;
import ru.fizteh.java2.podorozhnaya.filemap.storeableshell.StoreableState;

import java.io.IOException;

@Service("drop")
public class CommandDrop extends AbstractCommand {

    @Autowired
    private StoreableState state;
    
    public CommandDrop() {
        super(1);
    }

    public void execute(String[] args) throws IOException {
        try {
            state.drop(args[1]);
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new IOException(e.getMessage());
        }
        state.getOutputStream().println("dropped");
    }
}
