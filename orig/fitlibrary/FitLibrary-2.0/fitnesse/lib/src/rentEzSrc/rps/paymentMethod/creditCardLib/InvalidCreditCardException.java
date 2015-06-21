package rps.paymentMethod.creditCardLib;

public class InvalidCreditCardException extends Exception {

	private static final long serialVersionUID = 3616443484387619126L;

	public InvalidCreditCardException(String message){
		super(message);
	}
}
