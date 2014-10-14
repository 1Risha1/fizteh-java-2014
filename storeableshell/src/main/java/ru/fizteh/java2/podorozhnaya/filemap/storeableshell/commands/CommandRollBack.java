package ru.fizteh.java2.podorozhnaya.filemap.storeableshell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.AbstractCommand;
import ru.fizteh.java2.podorozhnaya.filemap.storeableshell.StoreableState;

import java.io.IOException;

@Service ("rollback")
public class CommandRollBack extends AbstractCommand {
    @Autowired
    private StoreableState state;
    
    public CommandRollBack() {
        super(0);
    }
    
    public void execute(String[] args) throws IOException {
        System.out.println(state.rollBack());
    } 
}

