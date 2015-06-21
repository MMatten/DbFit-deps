package rent;

import rps.paymentMethod.Money;

public class ClientAdapter {
    private rps.person.Client client;

    public ClientAdapter(rps.person.Client client) {
        this.client = client;
    }
	public boolean accountOwingIs(Money accountOwing) {
		return client.getAmountOwing().equals(accountOwing);
	}
}
