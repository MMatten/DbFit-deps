package fixture;

import rent.MockClock;
import rps.RentEz;
import rps.time.MyDate;
import fitlibrary.DomainFixture;

public class SpecifyRentEz extends DomainFixture {
	MockClock mockClock = new MockClock();
	
	public SpecifyRentEz() {
		setSystemUnderTest(new RentEz(mockClock));
	}
	public void timeIsNow(MyDate date) {
		mockClock.setTime(date);
	}
}
