package ru.fizteh.java2.podorozhnaya.filemap.storeableshell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.AbstractCommand;
import ru.fizteh.java2.podorozhnaya.filemap.storeableshell.StoreableState;

import java.io.IOException;

@Service ("use")
public class CommandUse extends AbstractCommand {

    @Autowired
    private StoreableState state;

    public CommandUse() {
        super(1);
    }

    public void execute(String[] args) throws IOException {    
        try {
            state.use(args[1]);
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        }
        state.getOutputStream().println("using " + args[1]);        
    }
}
