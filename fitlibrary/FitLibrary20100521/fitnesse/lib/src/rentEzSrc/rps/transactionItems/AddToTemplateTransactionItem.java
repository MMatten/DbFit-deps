package rps.transactionItems;

import rps.RentalItemType;
import rps.Template;
import rps.paymentMethod.Money;
import rps.transaction.AdminTransaction;

public class AddToTemplateTransactionItem extends TransactionItem {

    private Template template;
    private float count;
    private RentalItemType type;
    public AddToTemplateTransactionItem(AdminTransaction transaction,Template template, 
            RentalItemType type, float count) {
        super(transaction);
        this.template = template;
        this.count = count;
        this.type = type;
    }

    @Override
    public Money getTotalCost() {
        // TODO Auto-generated method stub
        return new Money();
    }

    @Override
    public void complete() {
        template.addItem(type,count);
    }

}
