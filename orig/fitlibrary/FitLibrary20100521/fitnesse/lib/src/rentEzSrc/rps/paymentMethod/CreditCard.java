package rps.paymentMethod;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import rps.RentEz;
import rps.paymentMethod.creditCardLib.CreditCardType;
import rps.paymentMethod.creditCardLib.InvalidCreditCardException;
import rps.time.MyDate;

public class CreditCard {
	private CreditCardType cardType;
	private String creditCardNo;
	private MyDate expiryDate;
	private RentEz rent;
	
	public CreditCard(String cardType, MyDate expiryDate, String creditCardNo,RentEz rent) throws InvalidCreditCardException{
		try {
			this.cardType = (CreditCardType) Class.forName("rps.paymentMethod.creditCardLib."+ridSpace(cardType)).newInstance();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (ClassNotFoundException e) {
			throw new InvalidCreditCardException("No such Credit Card type.");
		}
		this.rent = rent;
		this.expiryDate = expiryDate;
		this.creditCardNo = ridSpace(creditCardNo.trim());
		if (!this.cardType.isValidCreditCard(this)){
			throw new InvalidCreditCardException("Card Verification failed.");
		}
	}
	private String ridSpace(String string) {
		int spaceIndex;
		while((spaceIndex = string.indexOf(' ')) != -1){
			string = string.substring(0, spaceIndex)+string.substring(spaceIndex+1, string.length());
		}
		return string;
	}
	public CreditCard(String cardType, String expiryDate, String creditCardNo,RentEz rent) throws ParseException, InvalidCreditCardException{
		this(cardType, new MyDate(new SimpleDateFormat("MM/yy").parse(expiryDate)), creditCardNo,rent);
	}
	public String getCreditCardNo() {
		return creditCardNo;
	}
	
	public MyDate getExpiryDate() {
		return expiryDate;
	}
	public RentEz getRent() {
		return rent;
	}
	
}
