package ru.fizteh.java2.podorozhnaya.filemap.cmdimpl.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.AbstractCommand;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.State;

import java.io.IOException;

@Service ("pwd")
public class CommandPrintWorkingDirectory extends AbstractCommand {

    @Autowired
    private State state;
    
    public CommandPrintWorkingDirectory() {
        super(0);
    }

    public void execute(String[] args) throws IOException {

        state.getOutputStream().println(state.getCurrentDir().getCanonicalPath());

    }
}
