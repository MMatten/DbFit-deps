package rps.transactionItems.test;

import java.util.Iterator;

import junit.framework.TestCase;
import rps.Rates;
import rps.RentEz;
import rps.RentalItem;
import rps.RentalItemType;
import rps.paymentMethod.Money;
import rps.person.StaffMember;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transaction.ClientTransaction;
import rps.transactionItems.AcceptBookingTransactionItem;
import rps.transactionItems.BookTransactionItem;

public class TestAcceptBookingTransactionItem extends TestCase {
    AcceptBookingTransactionItem transItem;
    RentEz rentEz;

    @Override
	protected void setUp() throws Exception {
        super.setUp();
        rentEz = new RentEz();
        rentEz.createStaffMember(null,"John","5551234");
        StaffMember staff = rentEz.getStaffMember("John");
        rentEz.createClient(staff,"Bob","55554354");
        rentEz.getClient("Bob");
        Money m1 = new Money(0.05);
        Money m2 = new Money(0.45);
        Money m3 = new Money(2.00);
        rentEz.createRentalItemType("cup",12,new Rates(m1,m2,m3),new Money());
        RentalItemType itemType = rentEz.getRentalItemType("cup");
        ClientTransaction trans = rentEz.beginClientTransaction("Bob", "John");

        MyDate date = new MyDate();
        BookTransactionItem bookTransItem = new BookTransactionItem(trans, 10, itemType, date, new Duration(0,1,0));
        bookTransItem.complete();

        transItem = new AcceptBookingTransactionItem(trans, itemType, 10, date, new Duration(0,1,0));
    }

    public void testGetTotalCost() {
        assertEquals(new Money(),transItem.getTotalCost());
    }

    public void testComplete() {
        RentalItemType itemType = rentEz.getRentalItemType("cup");

        int count = 0;

        for(Iterator<RentalItem> it = itemType.getIdentifedHireItems(); it.hasNext();){
            assertEquals("cup",(it.next()).getHireItemTypeName());
            count++;
        }
        assertEquals(12,count);
        
        assertEquals(itemType.getFreeCount(), 12);
        //once the client 'accepts' (collects the items that they hired)
        //they become reserved and are therefore not available
        transItem.complete();
        assertEquals(itemType.getFreeCount(), 2);

    }

}
