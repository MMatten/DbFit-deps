package rent;

import rps.RentEz;
import rps.exception.MissingException;
import rps.paymentMethod.BonusPoints;
import rps.paymentMethod.Money;
import rps.paymentMethod.Voucher;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transaction.ClientTransaction;
import fitlibrary.CombinationFixture;

public class PermittedMixtureOfPayments extends CombinationFixture {
    RentEz rentEz;
    ClientTransaction trans;

    public PermittedMixtureOfPayments(RentEz rentEz) {
        this.rentEz = rentEz;
    }
    private boolean payWithMethod(String method) {
        if(method.equals("cash")) {
            return trans.payWithCash(new Money(0.2));
        } else if(method.equals("account")) {
            return trans.payOnAccount(new Money(0.2));
            
        } else if(method.equals("voucher")) {
            return trans.payWithVoucher(new Voucher(0.2));
            
        } else if(method.equals("bonus")) {
            return trans.payWithBonusPoint(new BonusPoints(0.2));
        }
        return true;
    }

    public String combine(String method1, String method2) {

//        if (method1.equals("cash") || method2.equals("cash")
//                || method1.equals("account") || method2.equals("account") )
//            return "yes";
        try {
			rentEz.getClient("Joanna").topupBonusPoints(new BonusPoints(99));
            trans = rentEz.beginClientTransaction("Joanna","Bill");
        } catch (MissingException e) {
            e.printStackTrace();
        }
        try {
            trans.book(1, rentEz.getRentalItemType("cup"), new MyDate(), new Duration(0,1,0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean pay1 = payWithMethod(method1);
        boolean pay2 = payWithMethod(method2);
        trans.abort();
        trans.complete();
		
        if (pay1 && pay2)
			return "yes";
        return "no";
    }
}
