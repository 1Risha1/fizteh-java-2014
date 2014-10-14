package ru.fizteh.java2.podorozhnaya.filemap.storeableshell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.AbstractCommand;
import ru.fizteh.java2.podorozhnaya.filemap.storeableshell.StoreableState;

import java.io.IOException;

@Service("put")
public class CommandPut extends AbstractCommand {

    @Autowired
    private StoreableState state;
    public CommandPut() {
        super(2);
    }

    public void execute(String[] args) throws IOException {
        String s = state.put(args[1], args[2]);
        if (s != null) {
            state.getOutputStream().println("overwrite\n" + s);
        } else {
            state.getOutputStream().println("new");
        }
    }
}
