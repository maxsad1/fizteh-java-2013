package ru.fizteh.fivt.students.adanilyak.userinterface;

import ru.fizteh.fivt.students.adanilyak.tools.ShellLogic;

/**
 * User: Alexander
 * Date: 20.10.13
 * Time: 22:26
 */
public class GenericShell {
    public GenericShell() {

    }

    public GenericShell(String[] args, GenericCmdList cmdList) {
        if (args.length == 0) {
            ShellLogic.interactiveMode(System.in, cmdList.getCmdList(), System.out, System.err);
        } else {
            ShellLogic.packageMode(args, cmdList.getCmdList(), System.out, System.err);
        }
    }
}
