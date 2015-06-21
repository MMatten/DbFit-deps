package rps.paymentMethod;

import rps.time.MyDate;

public class Voucher {
    private Money value;
    private MyDate expiryDate;

    public Voucher(double amount) {
        value = new Money(amount);
        expiryDate = null;
    }
    public Voucher(Money amount) {
        value = amount;
        expiryDate = null;
    }
    public Voucher(Money amount, MyDate expiryDate) {
        value = amount;
        this.expiryDate = expiryDate;
    }
    public Voucher(double amount, MyDate expiryDate) {
        value = new Money(amount);
        this.expiryDate = expiryDate;
    }
    @Override
	public boolean equals(Object other) {
        return other instanceof Voucher && 
            value.equals(((Voucher)other).value);
    }
    @Override
	public String toString() {
        return this.value.toString();
    }
    public double getValueInDouble(){
        return Double.parseDouble(this.toString());
    }
    public MyDate getExpiryDate() {
        return expiryDate;
    }
}
