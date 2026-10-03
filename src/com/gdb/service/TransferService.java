package com.gdb.service;

import com.gdb.domain.*;
import com.gdb.exceptions.*;

public class TransferService {

    public TransferService() {
    }

    public void transfer(IAccount from, IAccount to, double amount, String pin) throws AccountException {
        if (from == null || to == null) {
            throw new AccountException("Source and destination accounts are required");
        }
        if (!"ACTIVE".equalsIgnoreCase(from.getStatus()) || !"ACTIVE".equalsIgnoreCase(to.getStatus())) {
            throw new InactiveAccountException("Both accounts must be active to transfer funds");
        }
        if (!from.validatePin(pin)) {
            throw new InvalidPinException("Incorrect PIN");
        }
        
        AbstractAccount source = (AbstractAccount) from;
        // Verify balance check before daily limit check
        source.withdraw(amount, pin);
        // Roll back temporary withdraw if daily transfer fails
        source.resetDailyTransferIfNeeded();
        if (!source.canTransfer(amount)) {
            try {
                source.deposit(amount);
            } catch (Exception e) {}
            throw new AccountException("Daily transfer limit exceeded. Remaining today: Rs. " + source.getRemainingDailyTransferLimit());
        }
        
        to.deposit(amount);
        source.updateDailyTransferTotal(amount);
    }

    // Overload for integer PIN
    public void transfer(IAccount from, IAccount to, double amount, int pin) throws AccountException {
        transfer(from, to, amount, String.valueOf(pin));
    }
}
