package ru.fizteh.java2.podorozhnaya.filemap.cmdimpl.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.AbstractCommand;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.State;

import java.io.File;

@Service ("dir")
public class CommandDirectory extends AbstractCommand {

    @Autowired
    private State state;
    
    CommandDirectory() {
        super(0);
    }
    
    public void execute(String[] args) {
        File[] filesList = state.getCurrentDir().listFiles();
        for (File s: filesList) {
            state.getOutputStream().println(s.getName());
        }
    }
    }
