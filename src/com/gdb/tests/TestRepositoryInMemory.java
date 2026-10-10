package com.gdb.tests;

import com.gdb.domain.*;
import com.gdb.repository.*;
import com.gdb.service.AccountService;
import com.gdb.logging.*;
import java.util.List;

public class TestRepositoryInMemory {
    public static void main(String[] args) throws Exception {
        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        TransactionLogger logger = new TransactionLogger(new MemoryLogDestination());

        AccountService service = new AccountService(accountRepo, txnRepo, logger);

        // STEP 17: Test Account Repository CRUD
        int accNo1 = accountRepo.nextAccountNumber();
        IAccount acc1 = AccountFactory.createAccount("SAVINGS", accNo1, "Rajesh Sharma", 30, 50000.0, 0);
        accountRepo.save(acc1);
        System.out.println("[STEP 17] Saved: " + acc1.getAccountInfo());

        IAccount found = accountRepo.findById(accNo1);
        System.out.println("[STEP 17] Found: " + found.getAccountInfo());

        acc1.deposit(15000.0);
        accountRepo.update(acc1);
        System.out.println("[STEP 17] Updated balance: Rs. " + accountRepo.findById(accNo1).getBalance());

        int accNo2 = accountRepo.nextAccountNumber();
        IAccount acc2 = AccountFactory.createAccount("CURRENT", accNo2, "Priya Patel", 28, 30000.0, 0);
        accountRepo.save(acc2);

        System.out.println("[STEP 17] Total accounts in repository: " + accountRepo.findAll().size());

        // STEP 18: Test Transaction Repository Operations
        Transaction t1 = new Transaction("TXN-001", null, accNo1, TransactionType.DEPOSIT, 15000.0, 65000.0, "SUCCESS", "Deposit", 0, accNo1);
        Transaction t2 = new Transaction("TXN-002", null, accNo1, TransactionType.WITHDRAW, 5000.0, 60000.0, "SUCCESS", "Withdrawal", accNo1, 0);
        txnRepo.save(t1);
        txnRepo.save(t2);
        System.out.println("[STEP 18] Saved 2 transaction records.");

        List<Transaction> acc1Txns = txnRepo.findByAccount(accNo1);
        System.out.println("[STEP 18] Found transactions for #" + accNo1 + ": " + acc1Txns.size());

        // STEP 19: Test Service Integration With Repositories
        IAccount acc3 = service.openAccount("SAVINGS", "Amit Kumar", 35, 20000.0);
        System.out.println("[STEP 19] Opened account #" + acc3.getAccountNumber() + " via AccountService.");

        Transaction depTxn = service.deposit(acc3.getAccountNumber(), 5000.0);
        System.out.println("[STEP 19] Deposit executed. New Balance: Rs. " + depTxn.getBalanceAfter());
    }
}
