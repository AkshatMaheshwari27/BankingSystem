package com.gdb;

import com.gdb.domain.IAccount;
import com.gdb.logging.FileLogDestination;
import com.gdb.logging.LogDestination;
import com.gdb.logging.TransactionLogger;
import com.gdb.service.AccountService;

public class Main {
    public static void main(String[] args) throws Exception {
        LogDestination dest = new FileLogDestination();
        TransactionLogger logger = new TransactionLogger(dest);
        AccountService service = new AccountService(logger);

        IAccount acc1 = service.openAccount("SAVINGS", "John Doe", 25, 15000);
        acc1.setPin(1234);
        IAccount acc2 = service.openAccount("SAVINGS", "Jane Smith", 30, 10000);
        acc2.setPin(5678);

        service.deposit(acc1.getAccountNumber(), 5000);
        service.withdraw(acc1.getAccountNumber(), 2000, 1234);
        service.transfer(acc1.getAccountNumber(), acc2.getAccountNumber(), 1000, 1234);
    }
}
