package ru.fizteh.java2.podorozhnaya.filemap.cmdimpl.commands;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.AbstractCommand;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.State;
import java.io.File;
import java.io.IOException;

@Service("rm")
public class CommandRemove extends AbstractCommand {

    @Autowired
    private State state;
    
    CommandRemove() {
        super(1);
    }

    public void execute(String[] args) throws IOException {
        File f = state.getFileByName(args[1]);
        if (f.exists()) {
            if (f.getCanonicalPath().equals(state.getCurrentDir().getCanonicalPath())) {
                throw new IOException("rm: '" + args[1] + "' can't delete current directory");
            }
            FileUtils.deleteDirectory(f);
      //      Utils.deleteRecursivly(f);
        } else {
            throw new IOException("rm: '" + args[1] + "doesn't exist");
        }
    }


}
