package rps;

import rps.paymentMethod.Money;


public class BuyItemType implements ItemType{
	private String name;
	private int count;
	private Money sellingPrice;

	public BuyItemType(String name, int initialHash, Money sellingPrice) {
		this.name = name;
		this.count = initialHash;
		this.sellingPrice = sellingPrice;
	}
	public String getName() {
		return name;
	}
	public Money getSellingPrice() {
		return sellingPrice;
	}
	public Money getSellingPrice(int count) {
		return getSellingPrice().times(count);
	}
	public boolean sale(int saleCount) {
		if ((count - saleCount) < 0)
		    return false;
		count -= saleCount;
		return true;
	}
	public int getCount() {
		return count;
	}
	public boolean equals(Object other){
		if (other instanceof BuyItemType) {
			BuyItemType otherBuyItemType = (BuyItemType) other;
			return name.equals(otherBuyItemType.name)&&count == otherBuyItemType.count&&sellingPrice.equals(otherBuyItemType.sellingPrice);
		}
		return false;
	}
	public void add(int count) {
		this.count += count;
	}
}