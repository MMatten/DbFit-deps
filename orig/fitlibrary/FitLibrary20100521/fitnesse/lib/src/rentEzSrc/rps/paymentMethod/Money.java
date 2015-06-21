package rps.paymentMethod;
/*
 * @author Rick Mugridge 15/02/2004
 * Released under the terms of the GNU General Public License version 2 or later.
 *
 */

/**
  * Immutable money
*/
public class Money {
	private final long cents;
	
	public Money(long cents) {
		this.cents = cents;
	}
	public Money(double amount) {
        this.cents = (long)(amount*100+Math.signum(amount)*0.0000000001+Math.signum(amount)*0.5);
	}
    public Money() {
        this(0);
    }
    public boolean greaterThan(Money money) {
		return cents > money.cents;
	}
	public boolean greaterThanEqual(Money money) {
	    return cents >= money.cents;
	}
	public Money times(double rate) {
		return new Money((long)(cents*rate+Math.signum(cents * rate) * 0.5));
	}
	public boolean equals(Object other) {
		if (other instanceof Money) {
			Money otherMoney = (Money) other;
			if(this.cents==otherMoney.cents){
				return true;
			}
		}
		return false;
	}
	public int hashCode() {
		return (int)cents;
	}
	public String toString() {
	    long positiveCents = Math.abs(cents);
		String centString = ""+positiveCents%100;
		if (centString.length() == 1)
			centString = "0" + centString;
		String amountString = positiveCents/100+"."+centString;
		if (cents < 0)
		    return "-"+amountString;
		return ""+amountString;
	}
	public static Money parse(String s){
		int dot = s.indexOf(".");
		if (dot < 0){
			int amount = Integer.parseInt(s)*100;
			return new Money((long)amount);
		}
		else if (dot != s.length() - 3)
			throw new RuntimeException("Invalid money: "+s);
		double amount = Double.valueOf(s).doubleValue()*100;
		return new Money((long)(amount + Math.signum(amount) * 0.5));
	}
    public boolean isZero() {
        return cents == 0;
    }
    public Money plus(Money money) {
        return new Money(cents+money.cents);
    }
    public Money minus(Money money) {
        return new Money(cents-money.cents);
    }
    public Money negate() {
        return new Money(-cents);
    }
	public double getValueInDouble(){
		return Double.parseDouble(this.toString());
	}
}
