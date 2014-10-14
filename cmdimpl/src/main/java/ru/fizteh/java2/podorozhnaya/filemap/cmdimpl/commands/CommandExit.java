package ru.fizteh.java2.podorozhnaya.filemap.cmdimpl.commands;

import org.springframework.stereotype.Service;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.AbstractCommand;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.ExitRuntimeException;

import java.io.IOException;

@Service("exit")
public class CommandExit extends AbstractCommand {

    public CommandExit() {
        super(0);
    }

    public void execute(String[] args) throws IOException {
        throw new ExitRuntimeException();
    }
}
