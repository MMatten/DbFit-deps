package rent;

import rps.time.Clock;
import rps.time.MyDate;

public class MockClock implements Clock {
	private MyDate currentTime;
	
	public void setTime(MyDate time) {
		currentTime = time;
	}
	public MyDate getTime() {
		return new MyDate(currentTime);
	}
}
