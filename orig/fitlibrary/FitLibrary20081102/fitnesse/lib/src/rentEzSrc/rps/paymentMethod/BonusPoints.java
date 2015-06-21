/**
 * 
 */
package rps.paymentMethod;

/**
 * @author jpe001
 *
 */
public class BonusPoints {
	private Money value;
	
	public BonusPoints() {
		value = new Money();
	}
	public BonusPoints(double amount) {
		value = new Money(amount);
	}
	private BonusPoints(Money value){
		this.value = value;
	}
	public BonusPoints(long cents) {
		value = new Money(cents);
	}
	public boolean equals(Object other) {
		return other instanceof BonusPoints && 
			value.equals(((BonusPoints)other).value);
	}
    public BonusPoints plus(BonusPoints bonus) {
         return new BonusPoints(this.value.plus(bonus.value));
    }
    public BonusPoints minus(BonusPoints bonus) {
		return new BonusPoints(this.value.minus(bonus.value));
    }
	public double getValueInDouble(){
		return Double.parseDouble(this.toString());
	}
	public String toString() {
		return this.value.toString();
	}
	public static BonusPoints parse(String s) {
		return new BonusPoints(Money.parse(s));
	}
	private static final double[] SPENT_LIMIT_FOR_BONUS_POINT = new double[]{100.00, 500.00};
	private static final int[] CENTS_EARNED_FOR_SPENT_OVER_LIMIT = new int[] {5,10};
	
	public static BonusPoints bonusPointCalculation(Money moneySpent){
		double total = 0.00;
		for(int i = 0 ; i < SPENT_LIMIT_FOR_BONUS_POINT.length ; i++){
			if (moneySpent.getValueInDouble()<=SPENT_LIMIT_FOR_BONUS_POINT[i]){
				return new BonusPoints(total);
			}
			else{
				int bonusRate = CENTS_EARNED_FOR_SPENT_OVER_LIMIT[i];
				double calc = moneySpent.getValueInDouble()-SPENT_LIMIT_FOR_BONUS_POINT[i];
				if (i+1<SPENT_LIMIT_FOR_BONUS_POINT.length && moneySpent.getValueInDouble()>SPENT_LIMIT_FOR_BONUS_POINT[i+1])
					calc =  SPENT_LIMIT_FOR_BONUS_POINT[i+1]-SPENT_LIMIT_FOR_BONUS_POINT[i];
				double bonusEarnedInCents = calc*bonusRate;
				double bonusEarnedInCentsRounded = Math.ceil((double)((long)(bonusEarnedInCents*100.00))/100.00);
				total+= (double)(bonusEarnedInCentsRounded)/100.00;
			}
		}
		return new BonusPoints(total);
	}
}
