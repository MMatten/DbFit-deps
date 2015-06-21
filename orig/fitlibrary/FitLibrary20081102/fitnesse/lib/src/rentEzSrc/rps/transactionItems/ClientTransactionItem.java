/*
 * @author Rick Mugridge on Nov 8, 2004
 *
 * Copyright (c) 2004 Rick Mugridge, University of Auckland, NZ
 * Released under the terms of the GNU General Public License version 2 or later.
 *
 */
package rps.transactionItems;

import rps.person.Client;
import rps.person.StaffMember;
import rps.transaction.ClientTransaction;

/**
 *
 */
public abstract class ClientTransactionItem extends TransactionItem {
    public ClientTransactionItem(ClientTransaction transaction) {
        super(transaction);
    }
    protected Client getClient() {
        return ((ClientTransaction)getTransaction()).getClient();
    }
    protected StaffMember getStaffMember() {
        return ((ClientTransaction)getTransaction()).getStaffMember();
    }
}
