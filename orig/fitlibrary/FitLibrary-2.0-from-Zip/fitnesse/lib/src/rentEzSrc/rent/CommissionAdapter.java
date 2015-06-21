/*
 * @author Rick Mugridge on Sep 26, 2004
 * Released under the terms of the GNU General Public License version 2 or later.
 *
 */
package rent;

import rps.paymentMethod.Money;
import rps.person.StaffMember;

/**
 *
 */
public class CommissionAdapter {
    private StaffMember staffMember;
	
	public CommissionAdapter(StaffMember staffMember) {
	    this.staffMember = staffMember;
	}
	
	public String getStaff() {
	    return staffMember.getName();
	}	
	
	public Money getTotal() {
	    return staffMember.getCommissionEarnings();
	}		

  
}
