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
import rps.transactionItems.BookTransactionItem;

public class TestBookTransactionItem extends TestCase {
    
    BookTransactionItem transItem;
    RentEz rentEz;
    ClientTransaction trans;

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
        rentEz.createRentalItemType("cup",10,new Rates(m1,m2,m3),new Money());
        RentalItemType itemType = rentEz.getRentalItemType("cup");
        trans = rentEz.beginClientTransaction("Bob", "John");

        transItem = new BookTransactionItem(trans,10, itemType, new MyDate(), new Duration(0,1,0));

    }

    public void testGetTotalCost() {
        assertEquals(new Money(4.50),transItem.getTotalCost());
    }

    public void testComplete() {
        transItem.complete();
        
        int count = 0;

        RentalItemType itemType = rentEz.getRentalItemType("cup");

        for(Iterator<RentalItem> it = itemType.getIdentifedHireItems(); it.hasNext();){
            assertEquals("cup",(it.next()).getHireItemTypeName());
            count++;
        }
        assertEquals(10,count);
    }

}
