/*
 * TCSS 360 - Software Development & Quality Techniques
 * Group 1
 * AuctionCentral
 */
package model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Class represents a bid that can be placed on an item.
 * @author Yohei Sato
 */
public class Bid implements Serializable, Comparable<Bid> {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 7814537937865789379L;

	/**
	 * The value amount for the bid.
	 */
	private BigDecimal myValue;
	
	/**
   * The bidder who placed the respective bid.
	 */
	private Bidder myBidder;
	
	/**
	 * The item that will be associated with the Bid.
	 */
	private AuctionItem myItem;

  
	/**
	 * The Auction in which the bid was placed.
	 */
	
	public Bid(Bidder theBidder, AuctionItem theItem, BigDecimal theAmount) {
		myValue = theAmount;
		myBidder = theBidder;
		myItem =theItem;
		//myItem = theItem;
	}
	
	/**
	 * Gets the bid value.
	 * 
	 * @return the value of the bid
	 */
	public BigDecimal  getValue() {
		return myValue;
	}
	
	/**
	 * Gets the bidder for this bid.
	 * 
	 * @return the bidder for this bid
	 */
	public Bidder getBidder() {
		return myBidder;
	}
	
	/**
	 * Gets the item this bid is for.
	 * 
	 * @return the item this bid is for
	 */
	public AuctionItem getAuctionItem() {
		return myItem;
	}

	/**
	 * Compares bids by their item, and then by their value.
	 */
	@Override
	public int compareTo(Bid theOther) {
		if (myItem.equals(theOther.myItem)) {
			return myValue.compareTo(theOther.myValue);
		} else {
			return myItem.compareTo(theOther.myItem);
		}
	}	
}