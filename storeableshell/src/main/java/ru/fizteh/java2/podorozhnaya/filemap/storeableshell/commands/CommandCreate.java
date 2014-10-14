package ru.fizteh.java2.podorozhnaya.filemap.storeableshell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.AbstractCommand;
import ru.fizteh.java2.podorozhnaya.filemap.utils.Types;
import ru.fizteh.java2.podorozhnaya.filemap.storeableshell.StoreableState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("create")
public class CommandCreate extends AbstractCommand {

    @Autowired
    private StoreableState state;
    
    public CommandCreate() {
        super(-1);
    }

    @Override
    public void execute(String[] args) throws IOException {
        try {
            state.create(args[1], parser(args));
        } catch (IllegalArgumentException e) {
            throw new IOException("wrong type (" + e.getMessage() + ")");
        }
        state.getOutputStream().println("created");
    }
    
    public static List<Class<?>> parser(String[] args) throws IOException {
        if (args.length < 3) {
            throw new IOException("create: too few arguments");
        }

        List<Class<?>> columnType = new ArrayList<>();

        String first = args[2];
        String last = args[args.length - 1];

        if (!first.startsWith("(") || !last.endsWith(")")) {
            throw new IOException("create: Input not matchs \"create tablename (type1 ... typeN)\"");
        }
        if (args.length == 3) {
            columnType.add(Types.getTypeByName(first.substring(1, first.length() - 1)));
            return columnType;
        }

        first = first.substring(1);
        last = last.substring(0, last.length() - 1);

        columnType.add(Types.getTypeByName(first));
        for (int i = 3; i < args.length - 1; ++i) {
            columnType.add(Types.getTypeByName(args[i]));
        }
        columnType.add(Types.getTypeByName(last));
        return columnType;
    }
}
