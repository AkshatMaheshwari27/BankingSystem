package com.gdb.logging;

import com.gdb.command.TransactionCommand;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileLogDestination implements LogDestination {
    private TransactionLog log;

    public FileLogDestination() {
        this.log = new TransactionLog();
    }

    @Override
    public void write(TransactionCommand cmd) {
        try {
            log.log(cmd);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file log", e);
        }
    }

    @Override
    public List<TransactionCommand> readAll() {
        try {
            return log.readAll();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void clear() {
        log.clear();
    }

    @Override
    public String getDestinationName() {
        return "FILE";
    }
}
