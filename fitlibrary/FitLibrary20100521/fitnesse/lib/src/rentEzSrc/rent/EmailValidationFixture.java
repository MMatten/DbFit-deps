package rent;

import rps.person.Client;

public class EmailValidationFixture {
	
	public boolean validEmail(String email){
		return Client.isValidEmail(email);
	}
}
