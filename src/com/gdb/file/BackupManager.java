package com.gdb.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Manages timestamped backups of data files and maintains retention limits.
 */
public class BackupManager {

    // ============================================================
    // 📝 STEP 6: Implement backup(String path, int maxBackups)
    //
    // INSTRUCTIONS:
    //   1. If target path does not exist, return early.
    //   2. Copy target file to path + ".bak." + System.currentTimeMillis().
    //   3. Find all existing backup files for target path.
    //   4. If backup count exceeds maxBackups, delete the oldest backups.
    // ============================================================
    public static void backup(String path, int maxBackups) throws IOException {
        if (path == null) {
            return;
        }
        File targetFile = new File(path);
        if (!targetFile.exists()) {
            return;
        }

        String backupPath = path + ".bak." + System.currentTimeMillis();
        Files.copy(targetFile.toPath(), Paths.get(backupPath), StandardCopyOption.REPLACE_EXISTING);

        File dir = targetFile.getParentFile();
        if (dir == null) {
            dir = new File(".");
        }
        final String baseName = targetFile.getName();
        File[] backups = dir.listFiles((d, name) -> name.startsWith(baseName + ".bak."));

        if (backups != null && backups.length > maxBackups) {
            Arrays.sort(backups, Comparator.comparingLong(File::lastModified));
            int toDelete = backups.length - maxBackups;
            for (int i = 0; i < toDelete; i++) {
                backups[i].delete();
            }
        }
    }
}
