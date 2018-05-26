package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import backend.StorageIO;

/**
 * Manages new object requests from the front-end and 
 * @author Jared Malone
 * @version 5/5/2018
 *
 */
public class AuctionManager implements Manager {
	/** Generated Serial Version UID. */
	private static final long serialVersionUID = 4646613498773772086L;
	
	/** The filename to store persistent data. **/
	private static final String FILE_NAME = "data/storage.dat";
	
	/** The storage object to save and load persistent data. **/
	private StorageIO storage;
		
	/** The schedule of all past and future auctions. **/
	private AuctionCalendar myCalendar;
	
	
	/**
	 * Constructs a new manager object.
	 */
	public AuctionManager() {
		storage = new StorageIO(FILE_NAME);
		myCalendar = storage.getCalendar();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public User getUser(String theUsername) throws IllegalArgumentException {
		return storage.getUser(theUsername);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Auction> getAvailableAuctions(Bidder theBidder) {
		Collection<Auction> allFutureAuctions = new HashSet<>();
		Collection<Auction> result = new TreeSet<>();
		
		if (theBidder.isNewBidAllowed()) {
			allFutureAuctions = myCalendar.getFutureAuctions();	
		}
		
		for (Auction e : allFutureAuctions) {
			if (e.isAllowingNewBid(theBidder) && !e.getAllItems().isEmpty()) {
				result.add(e);
			}
		}
				
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNewAuctionRequestAllowed() {
		return myCalendar.isAllowingNewAuction();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Auction processNewAuctionRequest(
			NewAuctionRequest theAuctionRequest) 
		throws IllegalArgumentException {
		NonProfitContact sponsor = theAuctionRequest.getMySponsor();
		LocalDate auctionDate = theAuctionRequest.getMyDate();
		
		Auction newAuction = new Auction(auctionDate, sponsor);
		
		if (!sponsor.isDateForProposedAuctionValid(newAuction)) {
			throw new IllegalArgumentException(
					"You may not add an auction on this date. It is within "
			        + AuctionCalendar.MIN_MONTHS_BETWEEN_AUCTIONS_FOR_NONPROF
					        + "months of your latest auction.");
		}
		
		myCalendar.submitAuction(newAuction, auctionDate.getDayOfMonth(), 
				auctionDate.getMonthValue(), auctionDate.getYear());
				
		sponsor.addAuction(newAuction);
		storage.writeData();
		return newAuction;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNewItemRequestAllowed(Auction theAuction) {
		// check auction
		return theAuction.isAllowingNewItem();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuctionItem processNewItem(NewItemRequest theNewItemRequest) {
		Auction auction = theNewItemRequest.getMyAuction();
		String description = theNewItemRequest.getMyDescription();
		BigDecimal minimumAmount = theNewItemRequest.getMyMinimumBid();
		
//		if (!isNewItemRequestAllowed(auction)) {
//			throw new IllegalArgumentException("New item is not allowed.");
//		}
		
		AuctionItem newItem = new AuctionItem(minimumAmount, description);
		auction.addItem(newItem);
		storage.writeData();
		
		return newItem;
	}

	/**
	 * {@inheritDoc}
	 * Checks if the auction is allowing bids from the bidder. Checks if
	 * the bidder is allowed to make new bids.
	 */
	@Override
	public boolean isNewBidRequestAllowed(
			Auction theAuction, Bidder theBidder) {
		boolean result;
		
		// check auction status
		result = theAuction.isAllowingNewBid(theBidder);
						
		// check bidder status
		result = result && theBidder.isNewBidAllowed();
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bid processNewBid(NewBidRequest theNewBidRequest) 
			throws IllegalArgumentException {
		
		AuctionItem item = theNewBidRequest.getMyItem();
		BigDecimal bidValue = theNewBidRequest.getMyAmount();
		Bidder bidder = theNewBidRequest.getMyBidder();
		Auction auction = theNewBidRequest.getMyAuction();
		
		if (!item.isBidAmountValid(bidValue)) {
			throw new IllegalArgumentException("Bid is less than the minimum "
					+ "acceptable value.");
		}
		
		Bid newBid = new Bid(bidder, item, bidValue);
		auction.addBid(bidder, newBid);
		bidder.addAuction(auction);
		storage.writeData();
		return newBid;
	}

    /**
     * Changes the current maximum number of auctions being accepted for the 
     * calendar in the futureBTW.
     * 
     * pre-condition: the new maximum must be a positive integer
     * post-condition: the calendar will now accept future auctions up until 
     * reaching the new maximum.
     * 
     * @param theNewMax the new number of future auctions accepted
     * @throws IllegelArgumentException if the number is not positive
     */
	public void setFutureAuctionCapacity(final int theNewCap) {
	    myCalendar.setMaximumUpcomingAuctions(theNewCap);
	}
	
	/**
	 * Getter for the current number of max auctions accepted by the calendar
	 */
	public int getFutureAuctionCapacity() {
		return myCalendar.getMaximumUpcomingAuctions();
	}
	
    /**
     * Gets all auctions within a specified range of dates, inclusive.
     * 
     * pre-condition: Start date is before or equal to end date.
     * post_condition: returns all auctions in-between the given dates.
     * 
     * @param theStart the initial date of range
     * @param theEnd the closing date of range
     * @return all auctions between the two dates inclusive
     */
	public Collection<Auction> getAuctionsWithinRange(final LocalDate theStart, final LocalDate theEnd) {
	    return myCalendar.getAuctionsWithinRange(theStart, theEnd);
	}
	
    /**
     * Gets all auctions in the calendar, past, present, and future.
     * The auctions will be returned in sorted order.
     * 
     * pre-condition: 
     * post-Condition: all auctions in the calendar returned in sorted order
     * 
     * @return all auctions in the calendar
     */
	public Collection<Auction> getAllAuctionsSorted() {
	    return myCalendar.geAllAuctionsSorted();
	}

	public void removeAuction(final Auction theAuction) {
		if (theAuction.isEmptyBids()) {
			// remove auction from sponsor
			theAuction.getOwner().removeAuction(theAuction);
			
			// remove auction from calendar
			myCalendar.deleteAuction(theAuction);
		}
	}
	
}
