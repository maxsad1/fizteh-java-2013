package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.students.belousova.filemap.TableState;
import ru.fizteh.fivt.students.belousova.shell.Command;

import java.io.IOException;

public class CommandCommit implements Command {
    private TableState state;

    public CommandCommit(TableState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "commit";
    }

    @Override
    public void execute(String[] args) throws IOException {
        if (state.getCurrentTable() == null) {
            System.out.println("no table");
        } else {
            System.out.println(state.commitCurrentTable());
        }
    }

    @Override
    public int getArgCount() {
        return 0;
    }
}
