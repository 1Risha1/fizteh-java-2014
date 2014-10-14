package ru.fizteh.java2.podorozhnaya.filemap.cmdimpl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.State;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Ирина on 30.09.2014.
 */
public class Mode {

    private Mode() {}

    public static void batchMode(String[] args, State st) throws IOException {
        parseAndExecute(Joiner.on(" ").join(args), st);
    }

    private static void parseAndExecute(String arg, State st) throws IOException {
        Iterable<String> com = Splitter.on(";").trimResults().omitEmptyStrings().split(arg);
        for (String s: com) {
            String[] args = s.split("\\s+");
            st.checkAndExecute(args);
        }
    }

    public static void interactiveMode(State st) {
        Scanner sc = new Scanner(st.getInputStream());
        do {
            try {
                st.getOutputStream().print("$ ");
                String s = sc.nextLine();
                parseAndExecute(s, st);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } while (!Thread.interrupted());
        sc.close();
    }
}
