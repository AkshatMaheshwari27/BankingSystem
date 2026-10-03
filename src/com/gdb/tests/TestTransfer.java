package com.gdb.tests;

import com.gdb.domain.*;
import com.gdb.service.TransferService;
import com.gdb.exceptions.*;

public class TestTransfer {
    public static void main(String[] args) throws Exception {
        TransferService svc = new TransferService();
        AccountRulesEngine engine = AccountRulesEngine.getInstance();

        AbstractAccount acc1 = (AbstractAccount) AccountFactory.createAccount("SAVINGS", 1001, "Rajesh Sharma", 30, 100000);
        AbstractAccount acc2 = (AbstractAccount) AccountFactory.createAccount("SAVINGS", 1002, "Priya Patel", 28, 20000);
        acc1.changePin("1234", "1234");

        System.out.println("[STEP 9] " + acc1.getAccountInfo());
        System.out.println("[STEP 9] " + acc2.getAccountInfo());

        svc.transfer(acc1, acc2, 5000, 1234);
        System.out.println("[STEP 10] Transfer of Rs. 5000.0: success. acc1 = " + acc1.getBalance() + ", acc2 = " + acc2.getBalance());

        try {
            svc.transfer(acc1, acc2, 100000, 1234);
        } catch (AccountException e) {
            System.out.println("[STEP 11] Insufficient balance for transfer of Rs. 100000.0");
        }

        System.out.println("[STEP 12] Daily limit for acc1: Rs. " + acc1.getDailyTransferLimit());
        int transferCount = 1;
        while (true) {
            try {
                svc.transfer(acc1, acc2, 20000, 1234);
                System.out.println("[STEP 12] Transfer #" + transferCount + " of Rs. 20000.0: success. used today = " + acc1.getDailyTransferTotal());
                transferCount++;
            } catch (AccountException e) {
                System.out.println("[STEP 12] Daily transfer limit exceeded. Remaining today: Rs. " + acc1.getRemainingDailyTransferLimit());
                break;
            }
        }

        System.out.println("[STEP 13] Used today: Rs. " + acc1.getDailyTransferTotal() + " | Remaining: Rs. " + acc1.getRemainingDailyTransferLimit());
    }
}
