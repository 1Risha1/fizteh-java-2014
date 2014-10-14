package ru.fizteh.java2.podorozhnaya.filemap.cmdimpl;

import org.springframework.stereotype.Component;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.ExitRuntimeException;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.State;

import java.io.IOException;

/**
 * Created by Ирина on 30.09.2014.
 */
public class ShellImpl {

    private ShellImpl() {}

    public static int start(String[] args, State st) {
        int code = 0;
        try {
            if (args.length > 0) {
                Mode.batchMode(args, st);
            } else {
                Mode.interactiveMode(st);
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
