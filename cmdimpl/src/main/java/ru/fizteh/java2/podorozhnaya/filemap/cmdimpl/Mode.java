package ru.fizteh.java2.podorozhnaya.filemap.cmdimpl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import ru.fizteh.java2.podorozhnaya.filemap.shellapi.State;

import java.io.IOException;
import java.util.Scanner;

public class Mode {

    private Mode() {}

    public static void batchMode(String[] args, ShellImpl sh) throws IOException {
        parseAndExecute(Joiner.on(" ").join(args), sh);
    }

    private static void parseAndExecute(String arg, ShellImpl st) throws IOException {
        Iterable<String> com = Splitter.on(";").trimResults().omitEmptyStrings().split(arg);
        for (String s: com) {
            String[] args = s.split("\\s+");
            st.checkAndExecute(args);
        }
    }

    public static void interactiveMode(State st, ShellImpl sh) {
        Scanner sc = new Scanner(st.getInputStream());
        do {
            try {
                st.getOutputStream().print("$ ");
                String s = sc.nextLine();
                parseAndExecute(s, sh);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } while (!Thread.interrupted());
        sc.close();
    }
}
