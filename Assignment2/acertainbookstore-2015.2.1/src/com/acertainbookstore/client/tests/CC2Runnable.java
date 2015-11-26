package com.acertainbookstore.client.tests;

import java.util.List;

import com.acertainbookstore.business.StockBook;
import com.acertainbookstore.interfaces.StockManager;
import com.acertainbookstore.utils.BookStoreException;

public class CC2Runnable implements Runnable{

	
	StockManager store;
	int runTimes;
	int booksBought;
	List<StockBook> originalSS;
	
	boolean success = true;
	
	public CC2Runnable(StockManager store, int runTimes, int booksBought, List<StockBook> originalSS)
	{
		this.store = store;
		this.runTimes = runTimes;
		this.booksBought = booksBought;
		this.originalSS = originalSS;
	}
	
	public boolean getSuccess()
	{
		return success;
	}
	
	@Override
	public void run() {
		
		for(int i = 0; i < runTimes; i++)
		{
			try {
				List<StockBook> snapshot = store.getBooks();
				success &= Compare(originalSS, snapshot, booksBought);
				
			} catch (BookStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	
	private boolean Compare(List<StockBook> orig, List<StockBook> snapshot, int booksBought)
	{
		int numSnap = SumBooks(snapshot);
		int numOrig = SumBooks(orig);
		
		return numSnap == numOrig || numSnap == numOrig - booksBought; 
	}
	
	
	private int SumBooks(List<StockBook> snapshot)
	{
		int total = 0;
		for( StockBook sb : snapshot)
		{
			total += sb.getNumCopies();
		}
		return total;
	}

}
