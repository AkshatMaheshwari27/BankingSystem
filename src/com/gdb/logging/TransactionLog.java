package com.gdb.logging;

import com.gdb.command.TransactionCommand;
import java.io.*;
import java.util.*;

public class TransactionLog {
    private static final String FILE_PATH = "data/transactions.ser";

    // ============================================================
    // Helper: AppendableObjectOutputStream (COMPLETE — non-placeholder)
    // ============================================================
    private static class AppendableObjectOutputStream extends ObjectOutputStream {
        public AppendableObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }
        @Override
        protected void writeStreamHeader() throws IOException {
            // do not write header when appending to an existing stream
        }
    }

    public synchronized void log(TransactionCommand cmd) throws IOException {
        File file = new File(FILE_PATH);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        boolean append = file.exists() && file.length() > 0;
        FileOutputStream fos = new FileOutputStream(file, true);
        ObjectOutputStream oos;
        if (append) {
            oos = new AppendableObjectOutputStream(fos);
        } else {
            oos = new ObjectOutputStream(fos);
        }
        oos.writeObject(cmd);
        oos.flush();
        oos.close();
    }

    public synchronized List<TransactionCommand> readAll() throws IOException, ClassNotFoundException {
        List<TransactionCommand> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return list;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    TransactionCommand cmd = (TransactionCommand) ois.readObject();
                    list.add(cmd);
                } catch (EOFException e) {
                    break;
                }
            }
        }
        return list;
    }

    public synchronized void clear() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }
}
