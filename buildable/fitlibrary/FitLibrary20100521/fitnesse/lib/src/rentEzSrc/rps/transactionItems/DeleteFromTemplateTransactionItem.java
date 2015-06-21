package rps.transactionItems;

import rps.RentalItemType;
import rps.Template;
import rps.paymentMethod.Money;
import rps.transaction.AdminTransaction;

public class DeleteFromTemplateTransactionItem extends TransactionItem {

    private Template template;
    private RentalItemType type;
    
    public DeleteFromTemplateTransactionItem(AdminTransaction transaction,Template template, RentalItemType type) {
        super(transaction);
        this.template = template;
        this.type = type;
    }

    public Money getTotalCost() {
        return new Money();
    }

    public void complete() {
        template.deleteItem(type);
    }

}
