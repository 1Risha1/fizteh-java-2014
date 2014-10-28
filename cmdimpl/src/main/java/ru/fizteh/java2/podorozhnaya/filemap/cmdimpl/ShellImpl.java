package ru.fizteh.java2.podorozhnaya.filemap.cmdimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.Command;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.ExitRuntimeException;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.State;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ShellImpl {

    @Autowired
    private Map<String, Command> commands;

    @Autowired
    private State st;

    public void checkAndExecute(String[] args) throws IOException {
        Command c = commands.get(args[0]);
        if (c != null) {
            int argsNumber = c.getNumberOfArguments();
            if (argsNumber >= 0) {
                if (argsNumber > args.length - 1) {
                    throw new IOException(args[0] + ": Too few arguments");
                } else if (argsNumber < args.length - 1) {
                    throw new IOException(args[0] + ": Too many arguments");
                }
            }
            c.execute(args);
        } else {
            throw new IOException(args[0] + ": No such command");
        }
    }

    public int start(String[] args) {
        int code = 0;
        try {
            if (args.length > 0) {
               Mode.batchMode(args, this);
            } else {
                Mode.interactiveMode(st, this);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            code = 1;
        } catch (ExitRuntimeException d) {
            try {
                st.commitDif();
            } catch (IOException e) {
                System.err.println("can't write data to file");
                code = 1;
            }
        }
        return code;
    }
}
