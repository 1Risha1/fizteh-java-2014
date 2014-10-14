package ru.fizteh.java2.podorozhnaya.filemap.storeableshell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.AbstractCommand;
import ru.fizteh.java2.podorozhnaya.filemap.storeableshell.StoreableState;

import java.io.IOException;

@Service("remove")
public class CommandRemoveVal extends AbstractCommand {

    @Autowired
    private StoreableState state;

    public CommandRemoveVal() {
        super(1);
    }

    public void execute(String[] args) throws IOException {
        String s = state.removeValue(args[1]);
        if (s == null) {
            state.getOutputStream().println("not found");
        } else {
            state.getOutputStream().println("removed");
        }
    }
}
