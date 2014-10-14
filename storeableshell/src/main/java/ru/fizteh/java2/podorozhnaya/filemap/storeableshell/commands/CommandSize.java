package ru.fizteh.java2.podorozhnaya.filemap.storeableshell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.AbstractCommand;
import ru.fizteh.java2.podorozhnaya.filemap.storeableshell.StoreableState;

import java.io.IOException;

@Component
public class CommandSize extends AbstractCommand {

    @Autowired
    private StoreableState state;
    
    public CommandSize() {
        super(0);
    }
    
    public String getName() {
        return "size";
    }
    
    public void execute(String[] args) throws IOException {
        state.getOutputStream().println(state.getCurrentTableSize());
    } 
}
