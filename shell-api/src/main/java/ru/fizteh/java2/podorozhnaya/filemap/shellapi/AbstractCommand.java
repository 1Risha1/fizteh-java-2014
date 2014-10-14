package ru.fizteh.java2.podorozhnaya.filemap.shellapi;

public abstract class AbstractCommand implements Command {
    
    private final int argsNumber;
    
    public AbstractCommand(int argsNumber) {
        this.argsNumber = argsNumber;
    }
    
    public int getNumberOfArguments() {
        return argsNumber;
    }
}
