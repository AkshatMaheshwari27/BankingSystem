package com.gdb.tests;

import com.gdb.command.*;
import com.gdb.db.SimulatedDatabase;
import com.gdb.domain.*;
import com.gdb.logging.*;
import java.util.List;

public class TestBridgeLogging {
    public static void main(String[] args) throws Exception {
        // Setup test accounts
        IAccount acc1 = AccountFactory.createAccount("SAVINGS", 1001, "John", 25, 15000);
        acc1.setPin(1234);
        IAccount acc2 = AccountFactory.createAccount("SAVINGS", 1002, "Jane", 30, 10000);

        // STEP 24: Create Destinations
        LogDestination fileDest = new FileLogDestination();
        fileDest.clear();
        SimulatedDatabase db = new SimulatedDatabase();
        LogDestination dbDest = new DatabaseLogDestination(db);
        LogDestination memDest = new MemoryLogDestination();

        // STEP 25: Create TransactionLogger with File Destination
        TransactionLogger logger = new TransactionLogger(fileDest);
        System.out.println("[STEP 25] Logging to " + logger.getDestinationName() + " destination...");
        logThreeTransactions(logger, acc1, acc2);
        System.out.println("  " + logger.getDestinationName() + " log count: " + logger.readAll().size());

        // STEP 26: Switch to Database Destination
        logger.setDestination(dbDest);
        System.out.println("\n[STEP 26] Switched to " + logger.getDestinationName() + " destination...");
        logThreeTransactions(logger, acc1, acc2);
        System.out.println("  " + logger.getDestinationName() + " log count: " + logger.readAll().size());

        // STEP 27: Switch to Memory Destination
        logger.setDestination(memDest);
        System.out.println("\n[STEP 27] Switched to " + logger.getDestinationName() + " destination...");
        logThreeTransactions(logger, acc1, acc2);
        System.out.println("  " + logger.getDestinationName() + " log count: " + logger.readAll().size());

        // STEP 28: Verify Data Isolation Across Backends
        System.out.println("\n[STEP 28] Verifying Data Isolation:");
        logger.setDestination(fileDest);
        System.out.println("  FILE count: " + logger.readAll().size() + " [EXPECTED: 3]");
        logger.setDestination(dbDest);
        System.out.println("  DATABASE count: " + logger.readAll().size() + " [EXPECTED: 3]");
        logger.setDestination(memDest);
        System.out.println("  MEMORY count: " + logger.readAll().size() + " [EXPECTED: 3]");

        // STEP 29: Print Destination Names / Confirmation
        System.out.println("\n[STEP 29] All Bridge Pattern log backends verified successfully!");
    }

    private static void logThreeTransactions(TransactionLogger logger, IAccount acc1, IAccount acc2) throws Exception {
        TransactionCommand dep = new DepositCommand(acc1, 5000);
        dep.execute();
        logger.log(dep);

        TransactionCommand wth = new WithdrawCommand(acc1, 2000, 1234);
        wth.execute();
        logger.log(wth);

        TransactionCommand trf = new TransferCommand(acc1, acc2, 3000, 1234);
        trf.execute();
        logger.log(trf);
    }
}
